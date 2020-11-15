package aiad;

import aiad.access_point.AccessPoint;
import aiad.agentbehaviours.TPContractNetInit;
import aiad.agentbehaviours.TPCyclicContractNet;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.Serializable;

public class TrafficPoint extends Agent implements Serializable {
    protected Double traffic;
    protected Coordinates position;
    protected transient Environment env;
    public static double MAX_RANGE = 100.0;

    double collected;

    public Double getTraffic() {
        return traffic;
    }

    public void setTraffic(Double traffic) {
        this.traffic = traffic;
        this.collected = 0;
    }

    public Coordinates getPosition() {
        return position;
    }

    public Environment getEnv() {
        return env;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public TrafficPoint(Double traffic, Coordinates position) {
        this.traffic = traffic;
        this.position = position;
        this.env = Environment.getInstance();
        this.collected = 0;
    }

    public void setCollected(double collected) {
        this.collected = collected;
    }

    public double getCollected() {
        return collected;
    }

    @Override
    public void setup() {
        addBehaviour(new TPCyclicContractNet(this));
    }

    public double isNearDrone(AccessPoint drone) {
        return position.getDistance(drone.getPos());
    }

}
