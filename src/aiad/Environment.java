package aiad;

import aiad.agents.AccessPoint;
import aiad.agents.TrafficPoint;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Integer.max;

public class Environment implements Serializable {
    private static Environment env_instance = null;

    private ArrayList<TrafficPoint> traffic_points;
    private ArrayList<AccessPoint> drones;

    private Environment() {
        traffic_points = new ArrayList<>();
        drones = new ArrayList<>();

    }

    public void createPoints() {
        try {
            createTrafficPoints();
            createAccessPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Environment getInstance() {
        if (env_instance == null)
            env_instance = new Environment();
        return env_instance;
    }

    private void createAccessPoints() throws StaleProxyException {
        AccessPoint fap = new AccessPoint(100, new Coordinates(120, 100));
        AgentController aa = this.ac.acceptNewAgent("zoe", fap);
        aa.start();
        drones.add(fap);
        AccessPoint fap2 = new AccessPoint(80, new Coordinates(20, 100));
        AgentController aa2 = this.ac.acceptNewAgent("daisy", fap2);
        aa2.start();
        drones.add(fap2);
        AccessPoint fap3 = new AccessPoint(140, new Coordinates(300, 100));
        AgentController aa3 = this.ac.acceptNewAgent("luna", fap3);
        aa3.start();
        drones.add(fap3);
    }

    private void createTrafficPoints() throws StaleProxyException {
        TrafficPoint tp = new TrafficPoint(120.0, new Coordinates(150, 100));
        AgentController aa = this.ac.acceptNewAgent("loki", tp);
        aa.start();
        traffic_points.add(tp);
        TrafficPoint tp2 = new TrafficPoint(120.0, new Coordinates(250, 100));
        AgentController aa2 = this.ac.acceptNewAgent("bobby", tp2);
        aa2.start();
        traffic_points.add(tp2);
    }

    public AccessPoint getDroneByName(String name) {
        for (AccessPoint drone : drones) {
            if (drone.getName().equals(name))
                return drone;
        }
        return null;
    }

    public TrafficPoint getTrafficPointByName(String name) {
        for (TrafficPoint trafficPoint : traffic_points) {
            if (trafficPoint.getName().equals(name))
                return trafficPoint;
        }
        return null;
    }

    public ArrayList<TrafficPoint> getTrafficPoints() {
        return traffic_points;
    }

    public ArrayList<AccessPoint> getDrones() {
        return drones;
    }

    public ArrayList<AccessPoint> getNearDrones(AccessPoint actual_drone) {
        ArrayList<AccessPoint> near_drones = new ArrayList<>();
        for (AccessPoint drone : drones) {
            if (drone.getName().equals(actual_drone.getName()))
                continue;

            if (actual_drone.isNear(drone))
                near_drones.add(drone);
        }
        return near_drones;
    }

    public ArrayList<AccessPoint> getNearDrones(TrafficPoint actual_point) {
        ArrayList<AccessPoint> near_drones = new ArrayList<>();
        for (AccessPoint drone : drones) {
            double dist = actual_point.isNearDrone(drone);
            if (dist <= TrafficPoint.MAX_RANGE) {
                near_drones.add(drone);
            }
        }
        return near_drones;
    }

    public void startSystem() {
        createPoints();
    }

    public Coordinates getPosInRange(Coordinates pos, double range) {
        Random rand = new Random(System.currentTimeMillis());
        double angle = rand.nextDouble() * 2 * Math.PI;
        double reach = rand.nextDouble() * range;
        int newX = max((int) (pos.getX() + (reach * Math.cos(angle))), 0);
        int newY = max((int) (pos.getY() + (reach * Math.sin(angle))), 0);
        Coordinates newC = new Coordinates(newX, newY);
        return newC;
    }

    public double getPercentageOfTrafficCovered(){
        double allTraffic = 0, trafficCovered = 0;
        for(TrafficPoint tp : this.traffic_points){
            allTraffic += tp.getTraffic();

            // If getCollected() == 1, then the entire traffic of the tp is covered.
            trafficCovered = tp.getCollected() == 1? trafficCovered + tp.getTraffic() : trafficCovered;
        }

        // If there is no traffic to be covered, we will assume all is covered.
        if(allTraffic == 0) return 100;

        return trafficCovered/allTraffic * 100;
    }
}
