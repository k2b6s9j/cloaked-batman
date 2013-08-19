package k2b6s9j.cloaked_batman;

import java.io.IOException;

import org.mcstats.MetricsLite;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "cloaked-batman", name = "cloaked batman", version = "1.0")
public class cloakedBatman {
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	@Init
	public void Init(FMLInitializationEvent event) {
		
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) throws IOException {
		for (ModContainer mod : Loader.instance().getModList()) {
			try {
			    MetricsLite metrics = new MetricsLite(mod.getName(), mod.getVersion());
			    metrics.start();
			    FMLLog.info("Sent statistical information for %s (%s) version %s to MCStats.", mod.getModId(), mod.getName(), mod.getVersion());
			} catch (IOException e) {
			    FMLLog.warning("%s (%s) failed to submit statistical information to MCStats", mod.getModId(), mod.getName());
			}
		}
	}

}
