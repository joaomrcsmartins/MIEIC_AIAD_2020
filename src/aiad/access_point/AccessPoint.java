package aiad.access_point;

import aiad.Coordinates;
import aiad.Environment;
import aiad.TrafficPoint;
import aiad.agentbehaviours.AccessPointContractNetResponder;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AccessPoint extends Agent {
    static double MAX_RANGE = 20.0; //fixed value, but might change later
    private final double trafficCapacity;
    private double availableTraffic;
    private PriorityQueue<TrafficPoint> clientPoints;
    private Coordinates pos;
    private Environment env;

    public AccessPoint(double trafficCapacity, Coordinates pos) {
        this.trafficCapacity = trafficCapacity;
        this.availableTraffic = trafficCapacity;
        this.pos = pos;
        this.clientPoints = new PriorityQueue<TrafficPoint>();
        this.env = Environment.getInstance();
    }

    public double getTrafficCapacity() {
        return trafficCapacity;
    }

    public double getAvailableTraffic() {
        return availableTraffic;
    }

    public boolean isAvailable() {
        return availableTraffic > 0;
    }

    public Coordinates getPos() {
        return pos;
    }

    public Environment getEnv() {
        return env;
    }

    public TrafficPoint getCloserClient(){
        return clientPoints.peek();
    }


    public void setPos(Coordinates pos) {
        this.pos = pos;
    }

    public boolean isInRange(Coordinates pos2) {
        double dist = pos.getDistance(pos2);
        if (dist <= MAX_RANGE) {
            //System.out.println("Point in " + this.pos + " and point in " + pos2 + " have distance " + dist + " within range " + MAX_RANGE);
            return true;
        }
        return false;

    }

    public boolean addClient(TrafficPoint point) {
        if (point.getTraffic() > getAvailableTraffic()) {
            this.availableTraffic = 0;
            //TODO: behavior when the drone cannot deal with the request single-handedly
            System.out.println("Not enough traffic available to fulfill the request!");
        } else
            this.availableTraffic -= point.getTraffic();
        return this.clientPoints.add(point);
    }

    public void removeClient(TrafficPoint point) {
        if (!this.clientPoints.contains(point)) return;
        this.clientPoints.remove(point);
        //TODO: deal with the case when a AP doesn't fulfill the request of the client on its own
        //TODO: so that the available traffic doesn't grow past the maximum capacity
        this.availableTraffic += point.getTraffic();
    }

    public boolean serveRequest(TrafficPoint point) {
        if (point == null || !isInRange(point.getPosition()) || !isAvailable()) return false;
        return addClient(point);
    }

    @Override
    protected void setup() {
        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        addBehaviour(new AccessPointContractNetResponder(this, template, this.env));

    }
}
