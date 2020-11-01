package aiad;

import aiad.access_point.FlyingAccessPoint;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;

public class Environment {

    private Runtime rt;
    private Profile profile;
    private ContainerController ac;

    ArrayList<TrafficPoint> traffic_points;
    ArrayList<FlyingAccessPoint> drones;

    public Environment()
    {
        rt = Runtime.instance();
        profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        ac = rt.createMainContainer(profile);

        traffic_points = new ArrayList<>();
        drones = new ArrayList<>();

        try {
            createTrafficPoints();
            createAccesPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    //TODO: create more data
    private void createAccesPoints() throws StaleProxyException {
        //TODO: create real id
        FlyingAccessPoint fap = new FlyingAccessPoint(120,new Coordinates(10,10), this);
        AgentController aa = this.ac.acceptNewAgent("joao", fap);
        aa.start();
        drones.add(fap);
    }

    //TODO: create more data
    private void createTrafficPoints() throws StaleProxyException {
        //TODO: create id
        TrafficPoint tp = new TrafficPoint(120, new Coordinates(20,20), this);
        AgentController aa = this.ac.acceptNewAgent("joana", tp);
        aa.start();
        traffic_points.add(tp);
    }

    public ArrayList<TrafficPoint> getTraffic_points() {
        return traffic_points;
    }

    public ArrayList<FlyingAccessPoint> getDrones() {
        return drones;
    }

    public ArrayList<FlyingAccessPoint> getNearDrones(FlyingAccessPoint actual_drone) {
        ArrayList<FlyingAccessPoint> near_drones = new ArrayList<>();
        for (FlyingAccessPoint drone : drones) {
            if (actual_drone.isNear(drone))
                near_drones.add(drone);
        }
        return near_drones;
    }

    public ArrayList<FlyingAccessPoint> getNearDrones(TrafficPoint actual_point) {
        ArrayList<FlyingAccessPoint> near_drones = new ArrayList<>();
        for (FlyingAccessPoint drone : drones) {
            if (actual_point.isNearDrone(drone))
                near_drones.add(drone);
        }
        return near_drones;
    }

    public boolean verifyNewPosition(Coordinates coord) {
        for(FlyingAccessPoint drone : drones) {
            if (drone.getPos().equals(coord))
                return false;
        }
        for(TrafficPoint trafficPoint : traffic_points) {
            if (trafficPoint.getPosition().equals(coord))
                return false;
        }
        return true;
    }

    public void startSystem() {
        //TODO: trigger the agents activity
    }
}
