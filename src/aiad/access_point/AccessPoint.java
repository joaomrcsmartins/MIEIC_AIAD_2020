package aiad.access_point;

import aiad.Coordinates;
import aiad.Environment;
import aiad.TrafficPoint;
import aiad.agentbehaviours.APContractNetResponder;
import aiad.agentbehaviours.APCyclicContractNet;
import aiad.agentbehaviours.APRequestProtocolResponse;
import aiad.agentbehaviours.APSubContractNetResponder;
import aiad.util.ClientPair;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Optional;
import java.util.PriorityQueue;

public class AccessPoint extends Agent {
    static double MAX_RANGE = 20.0;
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

    public boolean isInRange(Coordinates pos2) {
        double dist = pos.getDistance(pos2);
        if (dist <= MAX_RANGE) {
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
        if (point == null || !isInRange(point.getPosition()) || !isAvailable()) return false;
        return addClient(point);
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

}
