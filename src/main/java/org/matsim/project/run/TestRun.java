package org.matsim.project.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import org.matsim.examples.ExamplesUtils;


import java.net.URL;

public class TestRun {
    static public void main(String[] args) {
        URL configURL = IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("siouxfalls-2014"), "config_default.xml");
        Config config = ConfigUtils.loadConfig(configURL);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setOutputDirectory("output");

        Scenario scenario = ScenarioUtils.loadScenario(config);

        Controler controler = new Controler(scenario);
        controler.addOverridingModule(new DiscreteModeChoiceModule());
        DiscreteModeChoiceConfigurator.configureAsSubtourModeChoiceReplacement(config);
        controler.run();
    }
}
