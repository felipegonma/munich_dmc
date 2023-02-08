package org.matsim.project.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

public class RunMunichDMC {
    // Input values for the number of iterations (so far, maximum for 18 GB RAM: 15 iterations)
    public static final int FIRST_ITERATION = 0;
    public static final int LAST_ITERATION = 2;

    // Activate or deactivate MATSim extensions
    public static final boolean USE_DISCRETE_MODE_CHOICE = true;
    public static final boolean USE_EMISSIONS = false;

    //Path to files
    public static final String PATH_TO_CONFIG_FILE = "scenarios/munich/configMunich_DMC.xml";
    public static final String PATH_TO_OUTPUT_FILE = "scenarios/munich/output";

    public static void main(String[] args) {
        if ( args==null || args.length==0 || args[0]==null ){
            args = new String[] {PATH_TO_CONFIG_FILE};
        }
        Config config = ConfigUtils.loadConfig(args);
        ControlerConfigGroup cc = config.controler();
        cc.setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
        cc.setOutputDirectory(PATH_TO_OUTPUT_FILE);
        cc.setFirstIteration(FIRST_ITERATION);
        cc.setLastIteration(LAST_ITERATION);
        //vehicles
        config.vehicles().setVehiclesFile("cars.xml");
        config.transit().setVehiclesFile("mucVehicles.xml");

        Scenario scenario = ScenarioUtils.loadScenario(config);

        Controler controler = new Controler(scenario);
        controler.run();
    }
}
