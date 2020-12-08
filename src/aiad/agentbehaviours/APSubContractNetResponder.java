package aiad.agentbehaviours;

import aiad.Coordinates;
import aiad.Launcher;
import aiad.agents.AccessPoint;
import aiad.agents.TrafficPoint;
import aiad.util.ClientPair;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sajas.proto.ContractNetResponder;

public class APSubContractNetResponder extends ContractNetResponder {
    AccessPoint accessPoint;
    Launcher.Environment env;

    public APSubContractNetResponder(AccessPoint a, MessageTemplate mt, Launcher.Environment env) {
        super(a, mt);
        this.accessPoint = a;
        this.env = env;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException {
        System.out.println(" (SubContractNet-handleCpf) FAP agent " + this.accessPoint.getLocalName() + ": CFP received from " + cfp.getSender().getLocalName() + ". Request original from: " + cfp.getContent());

        ClientPair trafficPoint_pair = this.accessPoint.getClientByName(cfp.getContent());
        if (trafficPoint_pair != null) {
            this.accessPoint.removeClient(trafficPoint_pair);
        }

        boolean isNear = this.env.isNear(cfp.getContent(), this.accessPoint.getPos());

        boolean proposal = this.accessPoint.isAvailable();
        if (proposal && !isNear) {
            System.out.println(" (SubContractNet-handleCpf) FAP agent " + this.accessPoint.getLocalName() + ": Proposing " + this.accessPoint.getAvailableTraffic() + " to " + cfp.getSender().getLocalName());
            ACLMessage propose = cfp.createReply();
            propose.setPerformative(ACLMessage.PROPOSE);
            propose.setContent(String.valueOf(this.accessPoint.getAvailableTraffic()));
            return propose;
        } else {
            System.out.println(" (SubContractNet-handleCpf)  FAP agent " + this.accessPoint.getLocalName() + ": Refused contract from " + cfp.getSender().getName());
            throw new RefuseException("proposal-refused");
        }
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {

        System.out.println(" (SubContractNet-handleAcceptProposal)  FAP Agent " + this.accessPoint.getLocalName() + ": Proposal accepted" + " from " + cfp.getSender().getName());
        TrafficPoint requestPoint = null;
        try {
            requestPoint = (TrafficPoint) accept.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
            throw new FailureException("failed-read-trafficPoint-obj");
        }
        System.out.println("(SubContractNet-handleAcceptProposal) FAP Agent " + this.accessPoint.getLocalName() + ": Request from " + cfp.getSender().getLocalName() + " was accepted");
        jade.lang.acl.ACLMessage inform = accept.createReply();
        inform.setPerformative(jade.lang.acl.ACLMessage.INFORM);

        Coordinates coord = this.accessPoint.getClientIntersection(requestPoint.getPosition());
        if (coord == null) {
            coord = this.env.getPosInRange(requestPoint.getPosition(), TrafficPoint.MAX_RANGE);
            this.accessPoint.removeClients();
        }
        System.out.println("(SubContractNet-handleAcceptProposal) New position : " + coord);
        this.env.getDroneByName(this.accessPoint.getName()).setPos(coord);

        return inform;

    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println(" (SubContractNet-handleRejectProposal) FAP Agent " + this.accessPoint.getLocalName() + ": Proposal rejected from " + cfp.getSender().getLocalName());
    }
}
