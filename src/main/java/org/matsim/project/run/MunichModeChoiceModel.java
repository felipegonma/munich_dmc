package org.matsim.project.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.router.TripRouter;
import org.matsim.core.utils.timing.TimeInterpretation;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.components.utils.home_finder.HomeFinder;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.AbstractDiscreteModeChoiceExtension;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.matsim.facilities.ActivityFacilities;

public class MunichModeChoiceModel extends AbstractDiscreteModeChoiceExtension {
    static public final String MODE_AVAILABILITY_NAME = "MunichModeAvailability";
    @Override
    protected void installExtension(){

        bindModeAvailability(MODE_AVAILABILITY_NAME).to(MunichModeAvailability.class);
        bindTripConstraintFactory("WalkDurationConstraint")
                .to(WalkDurationConstraint.Factory.class);
        bindTripEstimator("MyTripEstimator").to(MyTripEstimator.class);
    }
    @Provides
    @Singleton
    public WalkDurationConstraint.Factory provideWalkDurationConstraintFactory(DiscreteModeChoiceConfigGroup dmcConfig,
                                                                               HomeFinder homeFinder, Config config) {
        return new WalkDurationConstraint.Factory(config);
    }

    @Provides
    @Singleton
    public MyTripEstimator provideMyTripEstimator(ActivityFacilities facilities, TripRouter tripRouter,
                                                  DiscreteModeChoiceConfigGroup dmcConfig, TimeInterpretation timeInterpretation,
                                                  Scenario scenario){
        return new MyTripEstimator(facilities, tripRouter, timeInterpretation, dmcConfig.getCachedModes(), scenario);
    }
}
