package org.matsim.project.run;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.router.TripRouter;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.utils.timing.TimeInterpretation;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.components.estimators.AbstractTripRouterEstimator;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.pt.routes.TransitPassengerRoute;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public
class MyTripEstimator extends AbstractTripRouterEstimator {
    private final Collection<String> preroutedModes;
    private final Scenario scenario;


    public MyTripEstimator(ActivityFacilities facilities, TripRouter tripRouter,
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
            result = computeStandardTrip(routedTrip, person);
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

    private ComputationResult computeStandardTrip(List<? extends PlanElement> elements, Person person) {
        double utility = 0.0;
        double travelDistance = 0.0;
        double totalTravelDistance = 0.0;
        double travelTime = 0.0;
        double totalTravelTime = 0.0;

        for (PlanElement element : elements) {
            if (element instanceof Leg) {
                Leg leg = (Leg) element;
                travelDistance = leg.getRoute().getDistance() * 1e-3;
                travelTime = leg.getTravelTime().seconds() / 3600;
                totalTravelDistance += travelDistance;
                totalTravelTime += travelTime;
                switch (leg.getMode()) {
                    case TransportMode.car:
                        Id<Vehicle> vehicle = VehicleUtils.getVehicleId(person, TransportMode.car);
                        VehicleType car = scenario.getVehicles().getVehicles().get(vehicle).getType();
                        switch (car.getId().toString()) {
                            case "gasoline":
                                utility += -3.65 - (0.022 + 0.25) * travelDistance * (0.79) - 0.15 * travelTime; //Scenario 1
                                //utility += -3.25 - (0.885) * travelDistance * (0.79) - 0.15 * travelTime; // Scenario 2
                                //utility += -3.65 - (0.272 + 0.1590) * travelDistance * (0.79) - 0.15 * travelTime; // Scenario 3
                                //utility += -3.25 - (0.885 + 0.1590) * travelDistance * (0.79) - 0.15 * travelTime; // Scenario 4
                                break;
                            case "diesel":
                                utility += -3.65 - (0.022 + 0.25) * travelDistance * (0.79) - 0.15 * travelTime;
                                //utility += -3.25 - (0.885) * travelDistance * (0.79) - 0.15 * travelTime;
                                //utility += -3.65 - (0.272 + 0.1667) * travelDistance * (0.79) - 0.15 * travelTime;
                                //utility += -3.25 - (0.885 + 0.1667) * travelDistance * (0.79) - 0.15 * travelTime;
                                break;
                            case "bev":
                                utility += -3.65 - (0.022 + 0.25) * travelDistance * (0.79) - 0.15 * travelTime;
                                //utility += -3.25 - (0.885) * travelDistance * (0.79) - 0.15 * travelTime;
                                //utility += -3.65 - (0.272 + 0.1415) * travelDistance * (0.79) - 0.15 * travelTime;
                                //utility += -3.25 - (0.885 + 0.1415) * travelDistance * (0.79) - 0.15 * travelTime;
                                break;
                            case "phev":
                                utility += -3.65 - (0.022 + 0.25) * travelDistance * (0.79) - 0.15 * travelTime;
                                // utility += -3.25 - (0.885) * travelDistance * (0.79) - 0.15 * travelTime;
                                // utility += -3.65 - (0.272 + 0.1503) * travelDistance * (0.79) - 0.15 * travelTime;
                                //utility += -3.25 - (0.885 + 0.1503) * travelDistance * (0.79) - 0.15 * travelTime;
                                break;
                        }
                        break;
                    case TransportMode.walk:
                        utility += - 0 - (0.04 + 1.264) * travelDistance * (0.79); //0.499
                         //utility += - 0 - (0.499) * travelDistance * (0.79); //0.499
                        //utility += - 0 - (1.304 + 0.007) * travelDistance * (0.79);
                        //utility += - 0 - (0.499 + 0.007) * travelDistance * (0.79);
                        break;
                    case TransportMode.bike:
                        utility += -2.90 - (0.047 + 0.474) * travelDistance * (0.79); //0.147)
                        // utility += -2.90 - (0.147) * travelDistance * (0.79); //0.147)
                        //utility += -2.90 - (0.535 + 0.0672) * travelDistance * (0.79); //0.147)
                        //utility += -2.90 - (0.147 + 0.0672) * travelDistance * (0.79); //0.147)
                        break;
                }
            }
        }
        return new ComputationResult(totalTravelDistance, utility, totalTravelTime*3600);
    }

    private ComputationResult computePtTrip(List<? extends PlanElement> elements) {
        double utility = 0;//2.87;
        double travelDistance = 0.0;
        double totalTravelDistance = 0.0;
        double travelTime = 0.0;
        double totalTravelTime = 0.0;

        for (PlanElement element : elements) {
            if (element instanceof Leg) {

                Leg leg = (Leg) element;
                travelDistance = leg.getRoute().getDistance() * 1e-3;
                travelTime = leg.getTravelTime().seconds()/3600;
                totalTravelDistance += travelDistance;
                totalTravelTime += travelTime;

                switch (leg.getMode()){
                    case TransportMode.walk:
                        utility += - 0 - (0.04 + 1.264) * travelDistance * (0.79);
                        //utility += - 0 - (0.499) * travelDistance * (0.79);
                        //utility += - 0 - (1.304 + 0.007) * travelDistance * (0.79);
                        //utility += - 0 - (0.499 + 0.007) * travelDistance * (0.79);
                        break;

                    case TransportMode.pt:
                        TransitLine tl = scenario.getTransitSchedule().getTransitLines()
                                .get(((TransitPassengerRoute) leg.getRoute()).getLineId());
                        TransitRoute tr = tl.getRoutes().get(((TransitPassengerRoute) leg.getRoute()).getRouteId());

                        switch (tr.getTransportMode()){
                            case "bus":
                                utility += -2.50 -0.79*(0.18 * 1.2 * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*(0.298 * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*((0.18+0.1136) * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*((0.298+0.1136) * travelDistance) - 0.18*travelTime;
                                break;
                            case "tram":
                                utility += -2.50 -0.79*(0.18 * 1.2 * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*(0.298 * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*((0.18+0.0801) * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*((0.298+0.0801) * travelDistance) - 0.18*travelTime;
                                break;
                            case "subway":
                                utility += -2.50 -0.79*(0.18 * 1.2 * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*(0.18 * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*((0.18+0.0399) * travelDistance) - 0.18*travelTime;
                                //utility += -2.50 -0.79*((0.18+0.0399) * travelDistance) - 0.18*travelTime;
                                break;
                        }
                        break;
                }

            }
        }
        return new ComputationResult(totalTravelDistance, utility, totalTravelTime*3600);
    }
}