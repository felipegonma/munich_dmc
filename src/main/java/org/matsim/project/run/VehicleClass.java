package org.matsim.project.run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.utils.objectattributes.attributable.Attributes;
import org.matsim.vehicles.*;

import java.util.HashMap;
import java.util.Map;

public class VehicleClass {
    public void addVehicles (Scenario scenario) {
        Vehicles vehicles = VehicleUtils.createVehiclesContainer();
        VehicleType bevVehicleType = VehicleUtils.getFactory()
                .createVehicleType(Id.create("bev", VehicleType.class));
        VehicleType phevVehicleType = VehicleUtils.getFactory()
                .createVehicleType(Id.create("phev", VehicleType.class));
        VehicleType dieselVehicleType = VehicleUtils.getFactory()
                .createVehicleType(Id.create("diesel", VehicleType.class));
        VehicleType gasolineVehicleType = VehicleUtils.getFactory()
                .createVehicleType(Id.create("gasoline", VehicleType.class));

        bevVehicleType.setNetworkMode("car");
        bevVehicleType.setDescription("bev");
        phevVehicleType.setNetworkMode("car");
        phevVehicleType.setDescription("phev");
        dieselVehicleType.setNetworkMode("car");
        dieselVehicleType.setDescription("diesel");
        gasolineVehicleType.setNetworkMode("car");
        gasolineVehicleType.setDescription("gasoline");

        vehicles.addVehicleType(bevVehicleType);
        vehicles.addVehicleType(phevVehicleType);
        vehicles.addVehicleType(dieselVehicleType);
        vehicles.addVehicleType(gasolineVehicleType);

        for (Person person: scenario.getPopulation().getPersons().values()) {
            Vehicle vehicle;
            Id<Vehicle> vehicleId = Id.createVehicleId(person.getId());
            Map<String, Id<Vehicle>> modeVehicle = new HashMap<>();
            double rnd = Math.random();

            if (rnd < 0.0081){vehicle = VehicleUtils.createVehicle(vehicleId, bevVehicleType);}
            else if (rnd < 0.0416){vehicle = VehicleUtils.createVehicle(vehicleId, phevVehicleType);}
            else if (rnd < 0.3945){vehicle = VehicleUtils.createVehicle(vehicleId, dieselVehicleType);}
            else {vehicle = VehicleUtils.createVehicle(vehicleId, gasolineVehicleType);}
            vehicles.addVehicle(vehicle);
            modeVehicle.put("car", vehicleId);
            VehicleUtils.insertVehicleIdsIntoAttributes(person, modeVehicle);

        }

        new MatsimVehicleWriter(vehicles).writeFile("scenarios/munich/cars.xml");
    }
}
