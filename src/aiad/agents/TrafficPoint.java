package aiad.agents;

import aiad.Coordinates;
import aiad.Launcher;
import aiad.agentbehaviours.TPCyclicContractNet;
import aiad.agentbehaviours.TPRequestProtocolInit;
import jade.core.AID;
import sajas.core.Agent;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.io.Serializable;

public class TrafficPoint extends Agent implements Serializable {
    private String tpName;
    protected Double traffic;
    protected Coordinates position;
    protected transient Launcher.Environment env;
    public static double MAX_RANGE = 100.0;
    TPRequestProtocolInit request;
    private transient DefaultDrawableNode myNode;

    double collected;

    private boolean satisfied;

    public Double getTraffic() {
        return traffic;
    }

    public void setTraffic(Double traffic) {
        this.traffic = traffic;
        this.collected = 0;
    }

    public TPRequestProtocolInit getRequest() {
        return request;
    }

    public void setRequest(TPRequestProtocolInit request) {
        this.request = request;
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
        this.satisfied = false;
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

    public DefaultDrawableNode getNode() {
        return myNode;
    }

    public String getTPName() {
        return tpName;
    }

    @Override
    public void setAID(AID aid) {
        super.setAID(aid);
        tpName = aid.getLocalName();
    }

    public void satisfy() {
        this.satisfied = true;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public void dissatisfy() {
        this.satisfied = false;
    }
}
