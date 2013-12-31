/*
 * Copyright 2011-2013 Tyler Blair. All rights reserved.
 * Ported to Minecraft Forge by Mike Primm
 * Rewritten in Scala by Kepler "k2b6s9j" Sticka-Jones
 * Largely Modified by Kepler "k2b6s9j" Sticka-Jones
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package k2b6s9j.alfred.metrics

import cpw.mods.fml.common.{FMLLog, Loader}
import cpw.mods.fml.relauncher.Side
import net.minecraft.server.MinecraftServer
import java.io.{BufferedReader, ByteArrayOutputStream, File, IOException, InputStreamReader}
import java.net.{Proxy, URL, URLConnection, URLEncoder}
import java.util.{Collections, EnumSet, HashSet, LinkedHashSet, Set, UUID}
import java.util.zip.GZIPOutputStream
import Metrics._
import scala.reflect.BeanProperty
import net.minecraftforge.common.config.Configuration
import java.util

object Metrics {

  private val REVISION = 7

  private val BASE_URL = "http://report.mcstats.org"

  private val REPORT_URL = "/plugin/%s"

  private val PING_INTERVAL = 15

  def gzip(input: String): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    var gzos: GZIPOutputStream = null
    try {
      gzos = new GZIPOutputStream(baos)
      gzos.write(input.getBytes("UTF-8"))
    } catch {
      case e: IOException => e.printStackTrace()
    } finally {
      if (gzos != null) try {
        gzos.close()
      } catch {
        case ignore: IOException =>
      }
    }
    baos.toByteArray
  }

  private def appendJSONPair(json: StringBuilder, key: String, value: String) {
    var isValueNumeric = false
    try {
      if (value == "0" || !value.endsWith("0")) {
        java.lang.Double.parseDouble(value)
        isValueNumeric = true
      }
    } catch {
      case e: NumberFormatException => isValueNumeric = false
    }
    if (json.charAt(json.length - 1) != '{') {
      json.append(',')
    }
    json.append(escapeJSON(key))
    json.append(':')
    if (isValueNumeric) {
      json.append(value)
    } else {
      json.append(escapeJSON(value))
    }
  }

  private def escapeJSON(text: String): String = {
    val builder = new StringBuilder()
    builder.append('"')
    for (index <- 0 until text.length) {
      val chr = text.charAt(index)
      chr match {
        case '"' | '\\' =>
          builder.append('\\')
          builder.append(chr)

        case '\b' => builder.append("\\b")
        case '\t' => builder.append("\\t")
        case '\n' => builder.append("\\n")
        case '\r' => builder.append("\\r")
        case _ => if (chr < ' ') {
          val t = "000" + java.lang.Integer.toHexString(chr)
          builder.append("\\u" + t.substring(t.length - 4))
        } else {
          builder.append(chr)
        }
      }
    }
    builder.append('"')
    builder.toString()
  }

  private def urlEncode(text: String): String = URLEncoder.encode(text, "UTF-8")

  class Graph private (@BeanProperty val name: String) {

    private val plotters = new LinkedHashSet[Plotter]()

    def addPlotter(plotter: Plotter) {
      plotters.add(plotter)
    }

    def removePlotter(plotter: Plotter) {
      plotters.remove(plotter)
    }

    def getPlotters: util.Set[Plotter] = Collections.unmodifiableSet(plotters)

    override def hashCode(): Int = name.hashCode

    override def equals(`object`: Any): Boolean = {
      if (!`object`.isInstanceOf[Graph]) {
        return false
      }
      val graph = `object`.asInstanceOf[Graph]
      graph.name == name
    }

    protected def onOptOut() {
    }
  }

  abstract class Plotter(private val name: String) {

    def this() {
      this("Default")
    }

    def getValue: Int

    def getColumnName: String = name

    def reset() {
    }

    override def hashCode(): Int = getColumnName.hashCode

    override def equals(`object`: Any): Boolean = {
      if (!`object`.isInstanceOf[Plotter]) {
        return false
      }
      val plotter = `object`.asInstanceOf[Plotter]
      plotter.name == name && plotter.getValue == getValue
    }
  }
}

class Metrics(private val modname: String, private val modversion: String) {

  private val graphs = Collections.synchronizedSet(new util.HashSet[Graph]())

  private val configuration = new Configuration(configurationFile)

  private val configurationFile = getConfigFile

  private val guid = configuration.get(Configuration.CATEGORY_GENERAL, "guid", UUID.randomUUID().toString,
    "Server unique ID")
    .getString

  private val debug = configuration.get(Configuration.CATEGORY_GENERAL, "debug", false, "Set to true for verbose debug")
    .getBoolean(false)

  @volatile private var task: IScheduledTickHandler = null

  private var stopped: Boolean = false

  if ((modname == null) || (modversion == null)) {
    throw new IllegalArgumentException("modname and modversion cannot be null")
  }

  configuration.get(Configuration.CATEGORY_GENERAL, "opt-out", false, "Set to true to disable all reporting")

  configuration.save()

  def createGraph(name: String): Graph = {
    if (name == null) {
      throw new IllegalArgumentException("Graph name cannot be null")
    }
    val graph = new Graph(name)
    graphs.add(graph)
    graph
  }

  def addGraph(graph: Graph) {
    if (graph == null) {
      throw new IllegalArgumentException("Graph cannot be null")
    }
    graphs.add(graph)
  }

  def start(): Boolean = {
    if (isOptOut) {
      return false
    }
    stopped = false
    if (task != null) {
      return true
    }
    task = new IScheduledTickHandler() {

      private var firstPost: Boolean = true

      private var thrd: Thread = null

      override def tickStart(`type`: util.EnumSet[TickType], tickData: AnyRef*) {
      }

      override def tickEnd(`type`: util.EnumSet[TickType], tickData: AnyRef*) {
        if (stopped) return
        if (isOptOut()) {
          for (graph <- graphs) {
            graph.onOptOut()
          }
          stopped = true
          return
        }
        if (thrd == null) {
          thrd = new Thread(new Runnable() {

            def run() {
              try {
                postPlugin(!firstPost)
                firstPost = false
              } catch {
                case e: IOException => if (debug) {
                  FMLLog.info("[Metrics] Exception - %s", e.getMessage)
                }
              } finally {
                thrd = null
              }
            }
          })
          thrd.start()
        }
      }

      override def ticks(): util.EnumSet[TickType] = EnumSet.of(TickType.SERVER)

      override def getLabel: String = modname + " Metrics"

      override def nextTickSpacing(): Int = {
        if (firstPost) 100 else PING_INTERVAL * 1200
      }
    }
    TickRegistry.registerScheduledTickHandler(task, Side.SERVER)
    true
  }

  def stop() {
    stopped = true
  }

  def isOptOut: Boolean = {
    configuration.load()
    configuration.get(Configuration.CATEGORY_GENERAL, "opt-out", false)
      .getBoolean(false)
  }

  def enable() {
    if (isOptOut) {
      configuration.getCategory(Configuration.CATEGORY_GENERAL)
        .get("opt-out")
        .set("false")
      configuration.save()
    }
    if (task == null) {
      start()
    }
  }

  def disable() {
    if (!isOptOut) {
      configuration.getCategory(Configuration.CATEGORY_GENERAL)
        .get("opt-out")
        .set("true")
      configuration.save()
    }
  }

  def getConfigFile: File = new File(Loader.instance().getConfigDir, "PluginMetrics.cfg")

  private def postPlugin(isPing: Boolean) {
    val pluginName = modname
    val onlineMode = MinecraftServer.getServer.isServerInOnlineMode
    val pluginVersion = modversion
    var serverVersion: String = null
    serverVersion = if (MinecraftServer.getServer.isDedicatedServer) "MinecraftForge (MC: " + MinecraftServer.getServer.getMinecraftVersion +
      ")" else "MinecraftForgeSSP (MC: " + MinecraftServer.getServer.getMinecraftVersion +
      ")"
    val playersOnline = MinecraftServer.getServer.getCurrentPlayerCount
    val json = new StringBuilder(1024)
    json.append('{')
    appendJSONPair(json, "guid", guid)
    appendJSONPair(json, "plugin_version", pluginVersion)
    appendJSONPair(json, "server_version", serverVersion)
    appendJSONPair(json, "players_online", java.lang.Integer.toString(playersOnline))
    val osname = System.getProperty("os.name")
    var osarch = System.getProperty("os.arch")
    val osversion = System.getProperty("os.version")
    val java_version = System.getProperty("java.version")
    val coreCount = Runtime.getRuntime.availableProcessors()
    if (osarch == "amd64") {
      osarch = "x86_64"
    }
    appendJSONPair(json, "osname", osname)
    appendJSONPair(json, "osarch", osarch)
    appendJSONPair(json, "osversion", osversion)
    appendJSONPair(json, "cores", java.lang.Integer.toString(coreCount))
    appendJSONPair(json, "auth_mode", if (onlineMode) "1" else "0")
    appendJSONPair(json, "java_version", java_version)
    if (isPing) {
      appendJSONPair(json, "ping", "1")
    }
    if (graphs.size > 0) {
      synchronized (graphs) {
        json.append(',')
        json.append('"')
        json.append("graphs")
        json.append('"')
        json.append(':')
        json.append('{')
        var firstGraph = true
        val iter = graphs.iterator()
        while (iter.hasNext) {
          val graph = iter.next()
          val graphJson = new StringBuilder()
          graphJson.append('{')
          for (plotter <- graph.getPlotters) {
            appendJSONPair(graphJson, plotter.getColumnName, java.lang.Integer.toString(plotter.getValue))
          }
          graphJson.append('}')
          if (!firstGraph) {
            json.append(',')
          }
          json.append(escapeJSON(graph.getName))
          json.append(':')
          json.append(graphJson)
          firstGraph = false
        }
        json.append('}')
      }
    }
    json.append('}')
    val url = new URL(BASE_URL + String.format(REPORT_URL, urlEncode(pluginName)))
    var connection: URLConnection = null
    connection = if (isMineshafterPresent) url.openConnection(Proxy.NO_PROXY) else url.openConnection()
    val uncompressed = json.toString().getBytes
    val compressed = gzip(json.toString())
    connection.addRequestProperty("User-Agent", "MCStats/" + REVISION)
    connection.addRequestProperty("Content-Type", "application/json")
    connection.addRequestProperty("Content-Encoding", "gzip")
    connection.addRequestProperty("Content-Length", java.lang.Integer.toString(compressed.length))
    connection.addRequestProperty("Accept", "application/json")
    connection.addRequestProperty("Connection", "close")
    connection.setDoOutput(true)
    if (debug) {
      println("[Metrics] Prepared request for " + pluginName + " uncompressed=" +
        uncompressed.length +
        " compressed=" +
        compressed.length)
    }
    val os = connection.getOutputStream
    os.write(compressed)
    os.flush()
    val reader = new BufferedReader(new InputStreamReader(connection.getInputStream))
    var response = reader.readLine()
    os.close()
    reader.close()
    if (response == null || response.startsWith("ERR") || response.startsWith("7")) {
      if (response == null) {
        response = "null"
      } else if (response.startsWith("7")) {
        response = response.substring(if (response.startsWith("7,")) 2 else 1)
      }
      throw new IOException(response)
    } else {
      if (response == "1" ||
        response.contains("This is your first update this hour")) {
        synchronized (graphs) {
          val iter = graphs.iterator()
          while (iter.hasNext) {
            val graph = iter.next()
            for (plotter <- graph.getPlotters) {
              plotter.reset()
            }
          }
        }
      }
    }
  }

  private def isMineshafterPresent: Boolean = {
    try {
      Class.forName("mineshafter.MineServer")
      true
    } catch {
      case e: Exception => false
    }
  }
}
