package aiad;

import aiad.access_point.FlyingAccessPoint;

import java.util.ArrayList;

public class Environment {

    ArrayList<TrafficPoint> traffic_points;
    ArrayList<FlyingAccessPoint> drones;

    public ArrayList<TrafficPoint> getTraffic_points() {
        return traffic_points;
    }

    public ArrayList<FlyingAccessPoint> getDrones() {
        return drones;
    }

    public void setDrones(ArrayList<FlyingAccessPoint> drones) {
        this.drones = drones;
    }

    public void setTraffic_points(ArrayList<TrafficPoint> traffic_points) {
        this.traffic_points = traffic_points;
    }

    public void addNewDrone(FlyingAccessPoint drone) {
        drones.add(drone);
    }

    public void addNewTrafficPoint(TrafficPoint traffic_point) {
        traffic_points.add(traffic_point);
    }

    public ArrayList<FlyingAccessPoint> getNearDrones(FlyingAccessPoint actual_drone) {
        ArrayList<FlyingAccessPoint> near_drones = new ArrayList<>();
        for (FlyingAccessPoint drone : drones) {
            if (actual_drone.isNear(drone))
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

}
