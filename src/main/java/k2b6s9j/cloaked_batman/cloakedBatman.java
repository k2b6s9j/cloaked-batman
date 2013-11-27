package k2b6s9j.cloaked_batman;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import k2b6s9j.cloaked_batman.util.chickenbones.DepLoader;
import org.mcstats.MetricsLite;

import java.util.logging.Logger;

@Mod(modid = "cloaked-batman", name = "cloaked batman", version = "1.0.1", dependencies="after:CodeChickenCore;")
public class cloakedBatman {

    //Logger
    public static final Logger logger = Logger.getLogger("cloaked batman");
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
        if (!Loader.isModLoaded("CodeChickenCore")) {
            DepLoader.load();
        }
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
        logger.info("cloaked batman");
        logger.info("Copyright Kepler Sticka-Jones 2013");
        logger.info("http://k2b6s9j.com/projects/minecraft/cloaked-batman");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) throws Exception {
		for (ModContainer mod : Loader.instance().getModList()) {
			try {
			    MetricsLite metrics = new MetricsLite(mod.getName(), mod.getVersion());
			    metrics.start();
			    logger.info("Sent statistical information for "+mod.getModId ()+"("+ mod.getName()+")"+" version "+mod.getVersion()+" to MCStats.");
			} catch (Exception e) {
			    logger.warning(mod.getModId ()+"("+mod.getName()+") failed to submit statistical information to MCStats");
			}
		}
	}

}
