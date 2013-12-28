package k2b6s9j.cloaked_batman.util.log

import cpw.mods.fml.common.FMLLog

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

object ModLogger extends FMLLog {
    def log: Logger = Logger.getLogger("cloaked batman")
    log.setParent(FMLLog.getLogger)
}
