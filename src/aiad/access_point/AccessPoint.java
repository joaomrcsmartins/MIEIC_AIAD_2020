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

    public AccessPoint(double trafficCapacity, Coordinates pos, Environment env) {
        this.trafficCapacity = trafficCapacity;
        this.availableTraffic = trafficCapacity;
        this.pos = pos;
        this.clientPoints = initTPQueue();
        this.env = env;
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

    public void setPos(Coordinates pos) {
        this.pos = pos;
    }

    public boolean isInRange(Coordinates pos2) {
        return pos.getDistance(pos2) <= MAX_RANGE;
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
        if (!isInRange(point.getPosition()) || !isAvailable()) return false;
        return addClient(point);
    }

    private PriorityQueue<TrafficPoint> initTPQueue() {
        return new PriorityQueue<TrafficPoint>() {
            @Override
            public Comparator<? super TrafficPoint> comparator() {
                return new Comparator<TrafficPoint>() {
                    @Override
                    public int compare(TrafficPoint o1, TrafficPoint o2) {
                        if (o1.getTraffic() > o2.getTraffic())
                            return 1;
                        else
                            return o1.getTraffic().equals(o2.getTraffic()) ? 0 : -1;
                    }
                };
            }
        };
    }

    @Override
    protected void setup() {
        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        addBehaviour(new AccessPointContractNetResponder(this, template, this.env));

    }

    public boolean evaluateTrafficRequest() {
        //TODO: evaluate request from the TrafficPoint
        return true;
    }

    public boolean handleTrafficRequest() {
        //TODO: handle traffic request
        return true;
    }
}
