package aiad.agents;

import aiad.Coordinates;
import aiad.Launcher;
import aiad.agentbehaviours.TPCyclicContractNet;
import sajas.core.Agent;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.io.Serializable;

public class TrafficPoint extends Agent implements Serializable {
    protected Double traffic;
    protected Coordinates position;
    protected transient Launcher.Environment env;
    public static double MAX_RANGE = 10.0;

    DefaultDrawableNode myNode;

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

    public Launcher.Environment getEnv() {
        return env;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public TrafficPoint(Double traffic, Coordinates position) {
        this.traffic = traffic;
        this.position = position;
        this.env = Launcher.Environment.getInstance();
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

    // Simulation Util Functions

    public void setNode(DefaultDrawableNode node) {
        this.myNode = node;
    }

}
