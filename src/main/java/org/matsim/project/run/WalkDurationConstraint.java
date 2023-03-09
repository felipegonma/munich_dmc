package org.matsim.project.run;

import com.google.inject.Inject;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.constraints.AbstractTripConstraint;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.trip_based.TripConstraint;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.trip_based.TripConstraintFactory;

import java.util.Collection;
import java.util.List;

public class WalkDurationConstraint extends AbstractTripConstraint {
    public static final String WALK_MODE = "walk";
    private Config config;

// eqasim Sao Paulo mode_choice constraints
    @Inject
    public WalkDurationConstraint(Config config){
        this.config = config;
    }

    @Override
    public boolean validateBeforeEstimation (DiscreteModeChoiceTrip trip, String mode, List<String> previousModes){
        if (mode.equals(WALK_MODE)) {
            double distance = CoordUtils.calcEuclideanDistance(trip.getOriginActivity().getCoord(), trip.getDestinationActivity().getCoord());
            double walkSpeed = this.config.plansCalcRoute().getTeleportedModeSpeeds().get(WALK_MODE);
            double walkFactor = this.config.plansCalcRoute().getBeelineDistanceFactors().get(WALK_MODE);
            if ((distance * walkFactor / walkSpeed > 60 * 40)||(distance > 5000)) {
                return false;
            }
        }
        return true;
    }
    static public class Factory implements TripConstraintFactory {

        private Config config;

        @Inject
        public Factory(Config config) {
            this.config = config;
        }

        @Override
        public TripConstraint createConstraint(Person person, List<DiscreteModeChoiceTrip> planTrips,
                                               Collection<String> availableModes) {
            return new WalkDurationConstraint(this.config);
        }
    }
}
