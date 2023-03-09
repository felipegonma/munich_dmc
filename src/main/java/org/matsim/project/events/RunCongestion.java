package org.matsim.project.events;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsReaderXMLv1;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.io.File;

public class RunCongestion {

    public static void main(String[] args) {

        EventsManager manager = EventsUtils.createEventsManager();

        //manager.addHandler(new MyEventHandler1());

		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile("scenarios/test/output_base1/output_network.xml.gz");
		manager.addHandler(new CongestionDetectionEventHandler(network));

        //new MatsimEventsReader(manager).readFile("output/output_events.xml.gz");

    }
}