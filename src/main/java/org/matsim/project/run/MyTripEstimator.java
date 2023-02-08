package org.matsim.project.run;

import com.google.inject.Inject;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.TripRouter;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.components.estimators.AbstractTripRouterEstimator;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import org.matsim.facilities.ActivityFacilities;

import java.util.List;

public class MyTripEstimator extends AbstractTripRouterEstimator {
    @Inject
    public MyTripEstimator(TripRouter tripRouter, Network network, ActivityFacilities facilities) {
        super(tripRouter, network, facilities);
    }

    @Override
    protected double estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
                                  List<TripCandidate> previousTrips, List<? extends PlanElement> routedTrip) {
        // I) Calculate total travel time
        double totalTravelTime = 0.0;
        double totalTravelDistance = 0.0;

        for (PlanElement element : routedTrip) {
            if (element instanceof Leg) {
                Leg leg = (Leg) element;
                totalTravelTime = totalTravelTime + leg.getTravelTime().seconds();
                totalTravelDistance += leg.getRoute().getDistance() * 1e-3;
            }
        }

        // II) Compute mode-specific utility
        double utility = 0;

        switch (mode) {
            case TransportMode.car:
                utility = -0.5 - 0.025 * totalTravelDistance;
                break;
            case TransportMode.pt:
                utility = -0.15 - 0.15 * totalTravelTime;
                break;
            case TransportMode.walk:
                utility = -1.0 * totalTravelTime;
                break;
        }

        return utility;
    }
}
