package aiad;

import aiad.access_point.AccessPoint;
import aiad.access_point.FlyingAccessPoint;
import aiad.agentbehaviours.TrafficPointContractNetInit;
import aiad.agentbehaviours.TPContractNetInit;
import aiad.agentbehaviours.TPCyclicContractNet;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.Serializable;

public class TrafficPoint extends Agent implements Serializable {
    protected Double traffic;
    protected Coordinates position;
    protected transient Environment env;
    static double MAX_RANGE = 10.0;

    int collected = 0;

    public double getMaxRange(){
        return MAX_RANGE;
    }
    public Double getTraffic() {
        return traffic;
    }

    public void setTraffic(Double traffic) {
        this.traffic = traffic;
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
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public int getCollected() {
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
