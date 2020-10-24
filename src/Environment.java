import Drone.DroneAgent;

import java.util.ArrayList;

public class Environment {

    ArrayList<AccessPoint> traffic_points;
    ArrayList<DroneAgent> drones;

    public ArrayList<AccessPoint> getTraffic_points() {
        return traffic_points;
    }

    public ArrayList<DroneAgent> getDrones() {
        return drones;
    }

    public void setDrones(ArrayList<DroneAgent> drones) {
        this.drones = drones;
    }

    public void setTraffic_points(ArrayList<AccessPoint> traffic_points) {
        this.traffic_points = traffic_points;
    }

    public void addNewDrone(DroneAgent drone)
    {
        drones.add(drone);
    }

    public void addNewTrafficPoint(AccessPoint traffic_point)
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
