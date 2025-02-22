package org.matsim.project.run;

import com.google.inject.Inject;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.components.utils.home_finder.HomeFinder;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.constraints.AbstractTripConstraint;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.trip_based.TripConstraint;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.trip_based.TripConstraintFactory;

import java.util.Collection;
import java.util.List;

public class WalkDurationConstraint extends AbstractTripConstraint {
    public static final String WALK_MODE = "walk";
    public static final String BIKE_MODE = "bike";
    public static final String CAR_MODE = "car";
    private Config config;

// eqasim Sao Paulo mode_choice constraints

    @Override
    public boolean validateBeforeEstimation (DiscreteModeChoiceTrip trip, String mode, List<String> previousModes){

        double distance = CoordUtils.calcEuclideanDistance(trip.getOriginActivity().getCoord(),
                trip.getDestinationActivity().getCoord());
        if (mode.equals(BIKE_MODE) && (distance > 8000)) {
            return false;
        }
        if (mode.equals(WALK_MODE) && (distance > 2000)) {
            return false;
        }
        if (mode.equals(CAR_MODE) && (distance < 1000)) {
            return false;
        }
        return true;
    }
    static public class Factory implements TripConstraintFactory {

        @Override
        public TripConstraint createConstraint(Person person, List<DiscreteModeChoiceTrip> planTrips,
                                               Collection<String> availableModes) {
            return new WalkDurationConstraint();
        }
    }
}
