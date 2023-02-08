package org.matsim.project.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.AbstractDiscreteModeChoiceExtension;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import org.matsim.examples.ExamplesUtils;


import java.net.URL;

public class TestRun {
    //Path to files
    public static final String PATH_TO_CONFIG_FILE = "scenarios/munich/configMunich_DMC.xml";
    public static final String PATH_TO_OUTPUT_FILE = "scenarios/test/output";
    static public void main(String[] args) {
        URL configURL = IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("siouxfalls-2014"), "config_default.xml");
        Config config = ConfigUtils.loadConfig(configURL);
        ControlerConfigGroup cc = config.controler();
        cc.setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        cc.setOutputDirectory(PATH_TO_OUTPUT_FILE);
        cc.setFirstIteration(0);
        cc.setLastIteration(1);

        Scenario scenario = ScenarioUtils.loadScenario(config);

        Controler controler = new Controler(scenario);
        controler.addOverridingModule(new DiscreteModeChoiceModule());
        DiscreteModeChoiceConfigurator.configureAsSubtourModeChoiceReplacement(config);
        DiscreteModeChoiceConfigurator.configureAsModeChoiceInTheLoop(config);
        controler.addOverridingModule(new MyDMCExtension());

        controler.run();
    }


}
