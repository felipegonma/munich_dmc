package org.matsim.project.run;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.*;

public class CleanPopulation {
    //Paths to input and output files and column name of zone id
    private static final String ZONE_FILE = "./scenarios/munich/georef-germany-kreis-millesime.shp";
    private static final String ZONE_ID = "krs_code";
    private static final String OUTPUT_FILE = "./scenarios/munich/Population.xml";
    //Object to convert CRS
    private static final CoordinateTransformation ct = TransformationFactory.
            getCoordinateTransformation(TransformationFactory.DHDN_GK4, TransformationFactory.WGS84);
    //Data containers for MATSim objects and the geometry of zones
    private static final Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
    private static Map<String, Geometry> shapeMap;

    public static void main(String[] args) {
        new PopulationReader(scenario).readFile("scenarios/munich/munich-v1.0-5pct.plans.xml.gz");
        //Read zone shapefile
        shapeMap = readShapeFile(ZONE_FILE, ZONE_ID);
        //Create the commuter flow by OD relations
        PopulationCleaner("09162"); //471859
        //Write out the generated commuter flow
        new PopulationWriter(scenario.getPopulation()).write("scenarios/munich/Population_2.xml");

    }

    private static void PopulationCleaner(String origin) {
        Geometry home = shapeMap.get(origin);
            RemoveAgents(home);
    }

    private static void RemoveAgents(Geometry g) {
        List<Id<Person>> peopleOut = new ArrayList<>();
        int in = 0;
        int out = 0;

        for (Person person: scenario.getPopulation().getPersons().values()) {
            int n = 0;
            Coord z = new Coord();
            for (Plan plan: person.getPlans()){
                for (PlanElement planElement : plan.getPlanElements()) {
                    if (planElement instanceof Activity) {
                        Activity activity = (Activity) planElement;
                        Coord p = activity.getCoord();
                        Point q;
                        p = ct.transform(p);
                        q = MGC.coord2Point(p);
                        if (!g.contains(q) || z.equals(p)){
                            n = n + 1;
                            if (z.equals(p)){System.out.println(z.equals(p));}
                        }
                        z = activity.getCoord();
                    }
                }
            }
            if (n > 0){
                peopleOut.add(person.getId());
                out = out +1;
            } else {
                in = in +1 ;
            }
        }
        System.out.println("in" + in);
        System.out.println("out" + out);
        Iterator<Id<Person>> it = peopleOut.iterator();
        while(it.hasNext()){
            scenario.getPopulation().removePerson((Id<Person>) it.next());
        }
        int pop_size = 0;
        for (Person person: scenario.getPopulation().getPersons().values()){
           pop_size = pop_size +1;
        }
        System.out.println(pop_size);
    }


    public static Map<String, Geometry> readShapeFile(String filename, String attrString) {
        Map<String, Geometry> shapeMap = new HashMap<>();
        for (SimpleFeature ft : ShapeFileReader.getAllFeatures(filename)) {
            GeometryFactory geometryFactory = new GeometryFactory();
            WKTReader wktReader = new WKTReader(geometryFactory);
            Geometry geometry;
            try {
                geometry = wktReader.read((ft.getAttribute("the_geom")).toString());
                shapeMap.put(ft.getAttribute(attrString).toString(), geometry);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return shapeMap;
    }

}
