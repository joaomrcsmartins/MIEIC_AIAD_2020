package aiad;

import aiad.access_point.FlyingAccessPoint;
import aiad.agentbehaviours.TrafficPointContractNetInit;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.Serializable;

public class TrafficPoint extends Agent implements Serializable, Comparable {
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

    public TrafficPoint(Integer traffic, Coordinates position) {
        this.traffic = traffic;
        this.position = position;
        this.env = Environment.getInstance();
    }

    @Override
    public void setup() {
        addBehaviour(new TrafficPointContractNetInit(this, new ACLMessage(ACLMessage.CFP), this.env));
    }

    public boolean isNearDrone(FlyingAccessPoint drone) {
        return position.getDistance(drone.getPos()) <= MAX_RANGE;
    }


    @Override
    public int compareTo(Object o) {
        if (!(o instanceof TrafficPoint)) {
            throw new InstantiationError("Object is not a TrafficPoint Object");
        }

        TrafficPoint tp = (TrafficPoint) o;
        if (this.getTraffic() > tp.getTraffic())
            return 1;
        else
            return this.getTraffic().equals(tp.getTraffic()) ? 0 : -1;
    }
}
