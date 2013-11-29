package k2b6s9j.cloaked_batman.util.log;

import cpw.mods.fml.common.FMLLog;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ModLogger {
    private static Logger log = Logger.getLogger("cloaked batman");

    public static void info(String msg) {
        log.log(Level.INFO, msg);
    }

    public static void warning(String msg) {
        log.log(Level.WARNING, msg);
    }

    public static void severe(String msg) {
        log.log(Level.SEVERE, msg);
    }

    public static Logger getLogger() {
        return log;
    }

    static {
        log.setParent(FMLLog.getLogger());
    }
}
