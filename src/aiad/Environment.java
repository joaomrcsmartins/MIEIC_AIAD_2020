package aiad;

import aiad.access_point.AccessPoint;
import aiad.access_point.FlyingAccessPoint;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Integer.max;

public class Environment implements Serializable {
    private static Environment env_instance = null;

    private static Runtime rt;
    private static Profile profile;
    private static ContainerController ac;

    private ArrayList<TrafficPoint> traffic_points;
    private ArrayList<FlyingAccessPoint> drones;

    private Environment() {
        rt = Runtime.instance();
        profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        ac = rt.createMainContainer(profile);

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

    //TODO: create more data
    private void createAccessPoints() throws StaleProxyException {
        //TODO: create real id
        FlyingAccessPoint fap = new FlyingAccessPoint(120,new Coordinates(15,10));
        AgentController aa = this.ac.acceptNewAgent("zoe", fap);
        aa.start();
        drones.add(fap);
        //TODO: create real id
        FlyingAccessPoint fap2 = new FlyingAccessPoint(80,new Coordinates(16,20));
        AgentController aa2 = this.ac.acceptNewAgent("daisy", fap2);
        aa2.start();
        drones.add(fap2);
    }

    //TODO: create more data
    private void createTrafficPoints() throws StaleProxyException {
        //TODO: create id
        /*TrafficPoint tp = new TrafficPoint(80, new Coordinates(20, 0));
        AgentController aa = this.ac.acceptNewAgent("loki", tp);
        aa.start();
        traffic_points.add(tp);*/
        //TODO: create id
        TrafficPoint tp2 = new TrafficPoint(120.0, new Coordinates(10, 10));
        AgentController aa2 = this.ac.acceptNewAgent("bobby", tp2);
        aa2.start();
        traffic_points.add(tp2);
    }

    public AccessPoint getDroneByName(String name) {
        for (FlyingAccessPoint drone : drones) {
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

    public ArrayList<FlyingAccessPoint> getDrones() {
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
            if (dist <= actual_point.getMaxRange()) {
                near_drones.add(drone);
            }
        }
        return near_drones;
    }

    public boolean verifyNewPosition(Coordinates coord) {
        for (FlyingAccessPoint drone : drones) {
            if (drone.getPos().equals(coord))
                return false;
        }
        for (TrafficPoint trafficPoint : traffic_points) {
            if (trafficPoint.getPosition().equals(coord))
                return false;
        }
        return true;
    }

    public void startSystem() {
        createPoints();
        //TODO: trigger the agents activity
    }

    public Coordinates getPosInRange(Coordinates pos, double range) {
        Random rand = new Random();
        Integer dir = rand.nextInt(4);
        switch (dir) {
            case (0):
                return new Coordinates(pos.getX() + (int) range, pos.getY());
            case (1):
                return new Coordinates(max(pos.getX() - (int) range, 0), pos.getY());
            case (2):
                return new Coordinates(pos.getX(), pos.getY() + (int) range);
            case (3):
                return new Coordinates(pos.getX(), max(pos.getY() - (int) range, 0));
            default:
                return pos;
        }

    }
}
