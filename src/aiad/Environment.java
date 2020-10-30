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

    public void addNewDrone(FlyingAccessPoint drone)
    {
        drones.add(drone);
    }

    public void addNewTrafficPoint(TrafficPoint traffic_point)
    {
        traffic_points.add(traffic_point);
    }

    //verify if the new position that the drone wants to move to is empty
    public boolean verifyNewPosition(Coordinates coord)
    {
        return true;
        //verify traffic_points
        //verify drones
    }

    //verify if communication received is valid (is within the range of the drone)
    public boolean verifyDroneRange(Coordinates coord, Integer range)
    {
        return true;
    }


}
