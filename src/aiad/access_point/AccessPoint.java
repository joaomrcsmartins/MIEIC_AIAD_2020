package aiad.access_point;

import aiad.Coordinates;
import aiad.Environment;
import aiad.TrafficPoint;
import aiad.agentbehaviours.APContractNetResponder;
import aiad.agentbehaviours.APRequestProtocolResponse;
import aiad.agentbehaviours.APSubContractNetResponder;
import aiad.util.ClientPair;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Optional;
import java.util.PriorityQueue;

public class AccessPoint extends Agent {
    static double MAX_RANGE = 20.0; //fixed value, but might change later
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
            //System.out.println("Point in " + this.pos + " and point in " + pos2 + " have distance " + dist + " within range " + MAX_RANGE);
            return true;
        }
        return false;

    }

    public Environment getEnv() {
        return env;
    }

    public ClientPair getCloserClient() {
        return clientPoints.peek();
    }

    public PriorityQueue<ClientPair> getClientPoints() {
        return clientPoints;
    }

    public boolean addClient(TrafficPoint point) {
        double servedTraffic;
        if (getAvailableTraffic() < point.getTraffic()) {
            servedTraffic = getAvailableTraffic();
            this.availableTraffic = 0;
            //TODO: behavior when the drone cannot deal with the request single-handedly
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
        MessageTemplate templatesubcontract = MessageTemplate.and(
                MessageTemplate.MatchConversationId("sub-contract-net"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP) );

        MessageTemplate templatecontract = MessageTemplate.and(
                MessageTemplate.MatchConversationId("contract-net"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP) );

        //TODO: modify template
        addBehaviour(new APContractNetResponder(this, templatecontract, this.env));
        addBehaviour(new APSubContractNetResponder(this, templatesubcontract, this.env));
        addBehaviour(new APRequestProtocolResponse(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST), this.env));

    }

    public double evaluateRequest(double requestedTraffic) {
        if(getAvailableTraffic() == 0)
            return 0;

        double optimizableTraffic = requestedTraffic - getAvailableTraffic();
        if (optimizableTraffic > 0) {
            //find the first client that isn't fully satisfied by the AP and whose supplied traffic is enough to fully satisfy this request
            Optional<ClientPair> result = clientPoints.stream().filter(client -> !client.isSatisfied() && client.getValue() >= optimizableTraffic).findFirst();
            if (result.isEmpty())
                return getAvailableTraffic(); //when no optimization is possible, return the available value
            else
                removeClient(result.get()); //otherwise removes a not fully satisfied client to have necessary traffic to fully satisfy this one
        }
        return requestedTraffic; //fully satisfies the request the available traffic is enough or an optimization is made
    }
}
