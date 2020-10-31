package aiad.access_point;

import aiad.Coordinates;
import aiad.TrafficPoint;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AccessPoint extends Agent {
    static double MAX_RANGE = 20.0; //fixed value, but might change later
    private final double trafficCapacity;
    private double availableTraffic;
    private PriorityQueue<TrafficPoint> clientPoints;
    private Coordinates pos;

    public AccessPoint(double trafficCapacity, Coordinates pos) {
        this.trafficCapacity = trafficCapacity;
        this.availableTraffic = trafficCapacity;
        this.pos = pos;
        this.clientPoints = initTPQueue();
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
        System.out.println("Configuring FAP...");

        MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        addBehaviour(new ContractNetResponder(this, template) {
            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
                System.out.println("FAP agent " + getLocalName() + ": CFP received from " + cfp.getSender().getName() + ". Action is " + cfp.getContent());
                boolean proposal = evaluateTrafficRequest();
                if (proposal) {
                    System.out.println("FAP agent " + getLocalName() + ": Proposing " + getAvailableTraffic());
                    ACLMessage propose = cfp.createReply();
                    propose.setPerformative(ACLMessage.PROPOSE);
                    propose.setContent(String.valueOf(getAvailableTraffic()));
                    return propose;
                } else {
                    System.out.println("FAP agent " + getLocalName() + ": Refused contract from " + cfp.getSender().getName());
                    throw new RefuseException("proposal-refused");
                }
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                System.out.println("FAP Agent " + getLocalName() + ": Proposal accepted");
                if (handleTrafficRequest()) {
                    System.out.println("FAP Agent " + getLocalName() + ": Request accepted, connecting to Traffic Point");
                    ACLMessage inform = accept.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    return inform;
                } else {
                    System.out.println("FAP Agent " + getLocalName() + ": Request denied, refusing connection");
                    throw new FailureException("refused-traffic-request");
                }
            }

            @Override
            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                System.out.println("FAP Agent " + getLocalName() + ": Proposal rejected");
            }
        });
    }

    private boolean evaluateTrafficRequest() {
        //TODO: evaluate request from the TrafficPoint
        return true;
    }

    private boolean handleTrafficRequest() {
        //TODO: handle traffic request
        return true;
    }
}
