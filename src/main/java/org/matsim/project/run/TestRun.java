package org.matsim.project.run;

import java.util.Collection;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceModel;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.AbstractDiscreteModeChoiceExtension;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.DiscreteModeChoiceConfigurator;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import org.matsim.examples.ExamplesUtils;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;

import java.net.URL;

public class TestRun {
    //Path to files
    public static final String PATH_TO_CONFIG_FILE = "scenarios/munich/input_config.xml";
    public static final String PATH_TO_OUTPUT_FILE = "scenarios/munich/output_scenario_pt20";
    static public void main(String[] args) {
        Config config = ConfigUtils.loadConfig(PATH_TO_CONFIG_FILE, new DiscreteModeChoiceConfigGroup());
        ControlerConfigGroup cc = config.controler();
        cc.setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        cc.setOutputDirectory(PATH_TO_OUTPUT_FILE);
        cc.setFirstIteration(0);
        cc.setLastIteration(30);

        Scenario scenario = ScenarioUtils.loadScenario(config);
        VehicleClass vehicleClass = new VehicleClass();
        vehicleClass.addVehicles(scenario);
        Controler controler = new Controler(scenario);
        controler.addOverridingModule(new DiscreteModeChoiceModule());



        //DiscreteModeChoiceConfigurator.configureAsSubtourModeChoiceReplacement(config);
        DiscreteModeChoiceConfigurator.configureAsModeChoiceInTheLoop(config);
        controler.addOverridingModule(new MunichModeChoiceModel());
        controler.addOverridingModule(new MyDMCExtension());
        StrategyConfigGroup scg = new StrategyConfigGroup();
        DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
                .get(DiscreteModeChoiceConfigGroup.GROUP_NAME);
        dmcConfig.setModeAvailability(MunichModeChoiceModel.MODE_AVAILABILITY_NAME);

        Collection<String> tripConstraints = dmcConfig.getTripConstraints();
        tripConstraints.add("WalkDurationConstraint");
        dmcConfig.setTripConstraints(tripConstraints);
        dmcConfig.setTourConstraintsAsString("FromTripBased");
        dmcConfig.setFallbackBehaviour(DiscreteModeChoiceModel.FallbackBehaviour.INITIAL_CHOICE);


        controler.run();
    }


}
