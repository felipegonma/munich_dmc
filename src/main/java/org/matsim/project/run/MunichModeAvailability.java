package org.matsim.project.run;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.discrete_mode_choice.src.main.java.org.matsim.contribs.discrete_mode_choice.model.mode_availability.ModeAvailability;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MunichModeAvailability implements ModeAvailability {
    @Override
    public Collection<String> getAvailableModes(Person person, List<DiscreteModeChoiceTrip> trips){
        Collection<String> modes = new HashSet<>();

        modes.add(TransportMode.walk);
        modes.add(TransportMode.bike);
        modes.add(TransportMode.car);
        modes.add(TransportMode.pt);

        return modes;
    }
}
