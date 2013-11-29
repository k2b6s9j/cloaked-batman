package k2b6s9j.cloaked_batman.util.log

import cpw.mods.fml.common.FMLLog

import java.util.logging.Level
import java.util.logging.Logger

class ModLogger {
    def log: Logger = Logger.getLogger("cloaked batman")

    def info(msg: String) {
        log.log(Level.INFO, msg)
    }

    def warning(msg: String) {
        log.log(Level.WARNING, msg)
    }

    def severe(msg: String) {
        log.log(Level.SEVERE, msg)
    }

    def getLogger() {
        log: Logger
    }

    static {
        log.setParent(FMLLog.getLogger)
    }
}
