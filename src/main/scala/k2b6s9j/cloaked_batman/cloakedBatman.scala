package k2b6s9j.cloaked_batman

import cpw.mods.fml.common.{Mod, Loader, ModContainer}

import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLServerStartingEvent
import k2b6s9j.cloaked_batman.util.chickenbones.DepLoader
import org.mcstats.Metrics
import org.mcstats.Metrics.{Graph}
import k2b6s9j.cloaked_batman.util.log.ModLogger
import java.io.IOException
import scala.collection.JavaConversions._

@Mod(modid = "cloaked-batman", name = "cloaked batman", version = "1.1-SNAPSHOT", modLanguage = "scala", dependencies="after:CodeChickenCore;")
object cloakedBatman {

	@EventHandler
	def serverStarting(event: FMLServerStartingEvent) {
    if (!Loader.isModLoaded("CodeChickenCore")) {
      DepLoader.load()
    }
	}

	@EventHandler
	def Init(event: FMLInitializationEvent) {
    ModLogger.info("cloaked batman")
    ModLogger.info("Copyright Kepler Sticka-Jones 2013")
    ModLogger.info("http://k2b6s9j.com/projects/minecraft/cloaked-batman")
  }

	@EventHandler
	def postInit(event: FMLPostInitializationEvent) {
    val modList = Loader.instance().getActiveModList
    modList.toList.foreach(submitIndividualMod)
    modList.toList.foreach(addLoadedMod)
	}

  /*
    This method finds all of the mods currently loaded in game, their names, IDs, and versions and submits them as individual plugins on MCStats.
    @author Kepler (k2b6s9j) B.I. Sticka-Jones
   */
  def submitIndividualMod(mod: ModContainer): Unit = {
    try {
      val metrics: Metrics = new Metrics(mod.getName, mod.getVersion)
      metrics.start()
      ModLogger.info("Sent statistical information for " + mod.getName + " (" + mod.getModId + ")" + " version " + mod.getVersion + " to MCStats.")
    }
    catch {
      case e: Exception => ModLogger.warning(mod.getName + " (" + mod.getModId + ") failed to submit statistical information to MCStats")
      case e: Exception => e.printStackTrace()
    }
  }

  /*
    This method finds all of the mods currently loaded in game, their name, IDs, and version and adds them to a graph on the cloaked batman page on MCStats.
    @author Kepler (k2b6s9j) B.I. Sticka-Jones
   */
  def addLoadedMod(mod: ModContainer): Unit = {
    try {
      def metrics: Metrics = new Metrics("cloaked batman", "1.1-SNAPSHOT")

      val modsLoaded: Graph = metrics.createGraph("Mods Loaded")

      modsLoaded.addPlotter(Metrics.Plotter("%s %s", mod.getName, mod.getVersion))

      metrics.start()
    }
    catch{
      case e: IOException => ModLogger.warning(e.getMessage)
    }
  }
}
