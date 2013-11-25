package k2b6s9j.cloaked_batman;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.mcstats.MetricsLite;

@Mod(modid = "cloaked-batman", name = "cloaked batman", version = "1.0.1")
public class cloakedBatman {
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) throws Exception {
		for (ModContainer mod : Loader.instance().getModList()) {
			try {
			    MetricsLite metrics = new MetricsLite(mod.getName(), mod.getVersion());
			    metrics.start();
			    FMLLog.info("Sent statistical information for %s (%s) version %s to MCStats.", mod.getModId(), mod.getName(), mod.getVersion());
			} catch (Exception e) {
			    FMLLog.warning("%s (%s) failed to submit statistical information to MCStats", mod.getModId(), mod.getName());
			}
		}
	}

}
