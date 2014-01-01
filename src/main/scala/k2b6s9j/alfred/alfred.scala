package k2b6s9j.alfred

import cpw.mods.fml.common.{Mod, Loader, ModContainer}
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent}
import k2b6s9j.alfred.metrics.Metrics
import k2b6s9j.alfred.report.AddLoadedMod
import scala.collection.JavaConversions._
import k2b6s9j.alfred.util.log.ModLogger

@Mod(modid = "Alfred", name = "Alfred", version = "1.0-SNAPSHOT", modLanguage = "scala")
object alfred {

	@EventHandler
	def Init(event: FMLInitializationEvent) {
    ModLogger.info("Alfred")
    ModLogger.info("Copyright Kepler Sticka-Jones 2013")
    ModLogger.info("http://k2b6s9j.com/projects/minecraft/alfred")
  }

	@EventHandler
	def postInit(event: FMLPostInitializationEvent) {
    val modList = Loader.instance().getActiveModList
    modList.toList.foreach(submitIndividualMod)
    modList.toList.foreach(AddLoadedMod.addLoadedMod)
	}

  /*
    This method finds all of the mods currently loaded in game, their names, IDs, and versions and submits them as individual plugins on MCStats.
    TODO: This will all be a configuration option for the library, to 'forcefully' posts statistics for all installed mods.
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
}
