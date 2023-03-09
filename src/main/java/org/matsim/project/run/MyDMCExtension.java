package org.matsim.project.run;

import com.google.inject.Provides;
import org.matsim.core.config.Config;
import org.matsim.core.router.TripRouter;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.utils.timing.TimeInterpretation;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.components.utils.home_finder.HomeFinder;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.AbstractDiscreteModeChoiceExtension;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import org.matsim.facilities.ActivityFacilities;


public class MyDMCExtension extends AbstractDiscreteModeChoiceExtension {
        @Override
        public void installExtension() {
            bindTripEstimator("MyTripEstimator").to(MyTripEstimator.class);

        }

        @Provides
        public MyTripEstimator provideMyTripEstimator(ActivityFacilities facilities, TripRouter tripRouter,
                                                  DiscreteModeChoiceConfigGroup dmcConfig, TimeInterpretation timeInterpretation,
                                                      Scenario scenario){
            return new MyTripEstimator(facilities, tripRouter, timeInterpretation, dmcConfig.getCachedModes(), scenario);
        }
}
