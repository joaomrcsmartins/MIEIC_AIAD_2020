package aiad;

import aiad.access_point.FlyingAccessPoint;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.Serializable;
import java.util.ArrayList;

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
        TrafficPoint tp = new TrafficPoint(80.0, new Coordinates(20, 20));
        AgentController aa = this.ac.acceptNewAgent("loki", tp);
        aa.start();
        traffic_points.add(tp);
        //TODO: create id
        TrafficPoint tp2 = new TrafficPoint(120.0, new Coordinates(10, 10));
        AgentController aa2 = this.ac.acceptNewAgent("bobby", tp2);
        aa2.start();
        traffic_points.add(tp2);
    }

    public ArrayList<TrafficPoint> getTrafficPoints() {
        return traffic_points;
    }

    public ArrayList<FlyingAccessPoint> getDrones() {
        return drones;
    }

    /*public ArrayList<FlyingAccessPoint> getNearDrones(FlyingAccessPoint actual_drone) {
        ArrayList<FlyingAccessPoint> near_drones = new ArrayList<>();
        for (FlyingAccessPoint drone : drones) {
            if (actual_drone.isNear(drone))
                near_drones.add(drone);
        }
        return near_drones;
    }*/

    public ArrayList<FlyingAccessPoint> getNearDrones(TrafficPoint actual_point) {
        ArrayList<FlyingAccessPoint> near_drones = new ArrayList<>();
        for (FlyingAccessPoint drone : drones) {
            double dist = actual_point.isNearDrone(drone);
            if (dist <= actual_point.getMaxRange())
            {
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
}
