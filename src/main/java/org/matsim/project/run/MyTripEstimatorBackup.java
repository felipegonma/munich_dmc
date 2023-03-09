package org.matsim.project.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.TripRouter;
import org.matsim.core.utils.timing.TimeInterpretation;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.components.estimators.AbstractTripRouterEstimator;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.pt.routes.TransitPassengerRoute;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public
class MyTripEstimatorBackup extends AbstractTripRouterEstimator {
    private final Collection<String> preroutedModes;
    private final Scenario scenario;


    public MyTripEstimatorBackup(ActivityFacilities facilities, TripRouter tripRouter,
                                 TimeInterpretation timeInterpretation, Collection<String> preroutedModes,
                                 Scenario scenario) {
        super(tripRouter, facilities, timeInterpretation, Collections.emptySet());
        this.preroutedModes = preroutedModes;
        this.scenario = scenario;
    }

    @Override
    public double estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
                                  List<TripCandidate> previousTrips, List<? extends PlanElement> routedTrip) {
        // I) Calculate total travel time
        ComputationResult result = null;
        if (mode.equals(TransportMode.pt)) {
            result = computePtTrip(routedTrip);
        } else {
            result = computeStandardTrip(routedTrip);
        }

        // II) Compute mode-specific utility
        return result.utility;
    }

    private class ComputationResult {
        double travelDistance;
        double utility;
        double travelTime;

        ComputationResult(double travelDistance, double utility, double travelTime) {
            this.travelDistance = travelDistance;
            this.utility = utility;
            this.travelTime = travelTime;
        }
    }

    private double computeLegUtility(String mode,
                                     double travelDistance, double travelTime) {

        double utility = 0;
        switch (mode) {
            case TransportMode.car:

                /*if ( = "gasoline") {
                    utility = -0.5 - 0.010 * totalTravelDistance;
                }
                if (VehicleUtils.getDefaultVehicleType().getDescription() = "bev") {
                    utility = -0.5 - 0.015 * totalTravelDistance;
                }
                if (VehicleUtils.getDefaultVehicleType().getDescription() = "phev") {
                utility = -0.5 - 0.020 * totalTravelDistance;
                }
                if (VehicleUtils.getVehicleId(person, mode) = Id.createVehicleId("diesel")) {
                utility = -0.5 - 0.030 * totalTravelDistance;
                }*/
                utility = -3.4 -(0.022 + 0.25) * travelDistance * (0.79) - 0.15 * travelTime;
                break;
            case TransportMode.walk:
                utility = - 0 - (0.04 + 1.264 + 0.036) * travelDistance * (0.79); //0.499
                break;
            case TransportMode.bike:
                utility = -2.4 - (0.047 + 0.474 + 0.014) * travelDistance * (0.79); //0.147)
                break;
        }
        return utility;
    }

    private ComputationResult computeStandardTrip(List<? extends PlanElement> elements) {
        double utility = 0.0;
        double travelDistance = 0.0;
        double travelTime = 0.0;

        for (PlanElement element : elements) {
            if (element instanceof Leg) {
                Leg leg = (Leg) element;
                travelDistance += leg.getRoute().getDistance() * 1e-3;
                utility += computeLegUtility(leg.getMode(),
                        leg.getRoute().getDistance()* 1e-3,
                        leg.getTravelTime().seconds()/3600);
            }
        }
        return new ComputationResult(travelDistance, utility, travelTime);
    }

    private ComputationResult computePtTrip(List<? extends PlanElement> elements) {
        double utility = 0;//2.87;
        double travelDistance = 0.0;
        double travelTime = 0.0;

        for (PlanElement element : elements) {
            if (element instanceof Leg) {
                Leg leg = (Leg) element;
                travelDistance += leg.getRoute().getDistance() * 1e-3;
                switch (leg.getMode()){
                    case TransportMode.walk:
                        utility += - 0 - (0.04 + 1.264 + 0.036) * travelDistance * (0.79);
                        break;

                    case TransportMode.pt:
                        TransitLine tl = scenario.getTransitSchedule().getTransitLines()
                                .get(((TransitPassengerRoute) leg.getRoute()).getLineId());
                        TransitRoute tr = tl.getRoutes().get(((TransitPassengerRoute) leg.getRoute()).getRouteId());
                        travelTime = leg.getTravelTime().seconds()/3600;
                        switch (tr.getTransportMode()){
                            case "bus":
                                utility +=  -0.79*(0.18 * leg.getRoute().getDistance() * 1e-3)
                                        - 0.18*travelTime;
                                break;
                            case "tram":
                                utility +=  -0.79*(0.18 * leg.getRoute().getDistance() * 1e-3)
                                        - 0.18*travelTime;
                                break;
                            case "subway":
                                utility +=  -0.79*(0.18 * leg.getRoute().getDistance() * 1e-3)
                                        - 0.18*travelTime;
                                break;
                        }
                        break;
                }

            }
        }
        return new ComputationResult(travelDistance, utility, travelTime);
    }
}


