package aiad;

import aiad.access_point.FlyingAccessPoint;
import aiad.agentbehaviours.TrafficPointContractNetInit;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class TrafficPoint extends Agent {
    protected Integer traffic;
    protected Coordinates position;
    protected Environment env;
    static double MAX_RANGE = 10.0;

    public Integer getTraffic() {
        return traffic;
    }

    public void setTraffic(Integer traffic) {
        this.traffic = traffic;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public TrafficPoint(Integer traffic, Coordinates position, Environment env){
        this.traffic = traffic;
        this.position = position;
        this.env = env;
    }

    public void setup() {
        addBehaviour(new TrafficPointContractNetInit(this, new ACLMessage(ACLMessage.CFP), this.env));
    }

    public boolean isNearDrone(FlyingAccessPoint drone) {
        return position.getDistance(drone.getPos()) <= MAX_RANGE;
    }
}
