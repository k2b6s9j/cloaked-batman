package k2b6s9j.cloaked_batman

import cpw.mods.fml.common.{Mod, Loader, ModContainer}

import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import org.mcstats.Metrics
import k2b6s9j.cloaked_batman.util.log.ModLogger
import k2b6s9j.cloaked_batman.report.AddLoadedMod
import scala.collection.JavaConversions._

@Mod(modid = "cloaked-batman", name = "cloaked batman", version = "1.1-SNAPSHOT", modLanguage = "scala", dependencies="required-after:CodeChickenCore;")
object cloakedBatman {

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
    modList.toList.foreach(AddLoadedMod.addLoadedMod)
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
}
