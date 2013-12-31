package k2b6s9j.alfred.report;

import cpw.mods.fml.common.ModContainer;
import k2b6s9j.alfred.util.log.ModLogger;
import k2b6s9j.alfred.metrics.Metrics;

import java.io.IOException;

public class AddLoadedMod {

    /*
    This method finds all of the mods currently loaded in game, their name, IDs, and version and adds them to a graph on the cloaked batman page on MCStats.
    @author Kepler (k2b6s9j) B.I. Sticka-Jones
   */
    public static void addLoadedMod(ModContainer mod) {
        try {
            Metrics metrics = new Metrics("Alfred", "1.0-SNAPSHOT");

            Metrics.Graph weaponsUsedGraph = metrics.createGraph("Percentage of weapons used");

            weaponsUsedGraph.addPlotter(new Metrics.Plotter(mod.getName() + " version " + mod.getVersion()) {
                @Override
                public int getValue() {
                    return 1;
                }
            });

            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
            ModLogger.warning(mod.getName() + " could not be added to the mod page.");
        }
    }
}
