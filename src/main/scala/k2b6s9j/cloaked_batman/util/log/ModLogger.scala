package k2b6s9j.cloaked_batman.util.log

import cpw.mods.fml.common.FMLLog
import java.util.logging.{Level, Logger}

object ModLogger {
    def log = Logger.getLogger("cloaked batman")

    def info(msg: String) {
        log.log(Level.INFO, msg)
    }

    def warning(msg: String) {
        log.log(Level.WARNING, msg)
    }

    def severe(msg: String) {
        log.log(Level.SEVERE, msg)
    }

    def getLogger(): Logger = {
        log: Logger
    }
}
