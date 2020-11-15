package aiad.agents;

import aiad.Coordinates;
import aiad.Environment;
import aiad.agentbehaviours.APContractNetResponder;
import aiad.agentbehaviours.APCyclicContractNet;
import aiad.agentbehaviours.APRequestProtocolResponse;
import aiad.agentbehaviours.APSubContractNetResponder;
import aiad.util.ClientPair;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class AccessPoint extends Agent {
    static double MAX_RANGE = 200.0;
    private final double trafficCapacity;
    private double availableTraffic;
    private final PriorityQueue<ClientPair> clientPoints;
    private Coordinates pos;
    private Environment env;

    public AccessPoint(double trafficCapacity, Coordinates pos) {
        this.trafficCapacity = trafficCapacity;
        this.availableTraffic = trafficCapacity;
        this.pos = pos;
        this.clientPoints = new PriorityQueue<>();
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

    public boolean isNear(AccessPoint drone) {
        return drone.getPos().getDistance(this.getPos()) <= MAX_RANGE;
    }

    public void setPos(Coordinates pos) {
        this.pos = pos;
    }

    public boolean isInRange(TrafficPoint tp) {
        double dist = pos.getDistance(tp.getPosition());
        if (dist <= TrafficPoint.MAX_RANGE) {
            return true;
        }
        return false;

    }


    public Environment getEnv() {
        return env;
    }

    public PriorityQueue<ClientPair> getClientPoints() {
        return clientPoints;
    }

    public ClientPair getClientByName(String clientname) {
        for (ClientPair clientPair : clientPoints) {
            if (clientPair.getKey().getName().equals(clientname))
                return clientPair;
        }
        return null;
    }

    public boolean addClient(TrafficPoint point) {
        double servedTraffic;
        if (getAvailableTraffic() < point.getTraffic()) {
            servedTraffic = getAvailableTraffic();
            this.availableTraffic = 0;
            System.out.println("Not enough traffic available to fulfill the request!");
        } else
            this.availableTraffic -= servedTraffic = point.getTraffic();
        return this.clientPoints.add(new ClientPair(point, servedTraffic));
    }

    public boolean removeClient(ClientPair client) {
        if (!this.clientPoints.contains(client)) return false;
        this.availableTraffic += client.getValue();
        return this.clientPoints.remove(client);
    }

    public boolean serveRequest(TrafficPoint point) {
        if (point == null || !isInRange(point) || !isAvailable()) return false;
        return addClient(point);
    }

    public void removeClients() {
        for (ClientPair pair : clientPoints) {
            pair.getKey().setCollected(0);
        }
        clientPoints.clear();

    }

    @Override
    protected void setup() {
        MessageTemplate templateSubContract = MessageTemplate.and(
                MessageTemplate.MatchConversationId("sub-contract-net"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));

        MessageTemplate templateContract = MessageTemplate.and(
                MessageTemplate.MatchConversationId("contract-net"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));

        addBehaviour(new APContractNetResponder(this, templateContract, this.env));
        addBehaviour(new APCyclicContractNet(this));
        addBehaviour(new APSubContractNetResponder(this, templateSubContract, this.env));
        addBehaviour(new APRequestProtocolResponse(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST), this.env));

    }

    public Coordinates getClientIntersection(Coordinates requestPoint) {
        if (this.getClientPoints().size() == 0) return null;
        ArrayList<Coordinates> points = new ArrayList<>() {
        };
        this.getClientPoints().forEach(client -> points.add(client.getKey().getPosition()));
        Ellipse2D circle = new Ellipse2D.Double(requestPoint.getX() - TrafficPoint.MAX_RANGE,
                requestPoint.getY() - TrafficPoint.MAX_RANGE,
                2 * TrafficPoint.MAX_RANGE,
                2 * TrafficPoint.MAX_RANGE);
        Area intersection = new Area(circle);

        for (Coordinates point : points) {
            circle.setFrame(point.getX() - TrafficPoint.MAX_RANGE,
                    point.getY() - TrafficPoint.MAX_RANGE,
                    2 * TrafficPoint.MAX_RANGE,
                    2 * TrafficPoint.MAX_RANGE);
            intersection.intersect(new Area(circle));
            if (intersection.isEmpty())
                return null;
        }

        Rectangle rectIntersection = intersection.getBounds();
        return new Coordinates((int) rectIntersection.getCenterX(),
                (int) rectIntersection.getCenterY());
    }
}
