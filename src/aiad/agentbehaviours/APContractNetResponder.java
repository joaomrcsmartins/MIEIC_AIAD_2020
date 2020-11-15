package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import aiad.util.ClientPair;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

public class APContractNetResponder extends ContractNetResponder {

    AccessPoint accessPoint;
    Environment env;

    public APContractNetResponder(AccessPoint a, MessageTemplate mt, Environment env) {
        super(a, mt);
        this.accessPoint = a;
        this.env = env;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException {
        System.out.println(" (ContractNet-handleCpf) FAP agent " + this.accessPoint.getLocalName() + ": CFP received from " + cfp.getSender().getLocalName() + ". Traffic requested is " + cfp.getContent());

        ClientPair trafficPoint_pair = this.accessPoint.getClientByName(cfp.getSender().getName());
        if (trafficPoint_pair != null) {
            System.out.println("will remove point");
            this.accessPoint.removeClient(trafficPoint_pair);
        }

        boolean proposal = this.accessPoint.isAvailable();
        if (proposal) {
            System.out.println(" (ContractNet-handleCpf) FAP agent " + this.accessPoint.getLocalName() + ": Proposing " + this.accessPoint.getAvailableTraffic() + " to " + cfp.getSender().getLocalName());
            ACLMessage propose = cfp.createReply();
            propose.setPerformative(ACLMessage.PROPOSE);
            propose.setContent(String.valueOf(this.accessPoint.getAvailableTraffic()));
            return propose;
        } else {
            System.out.println(" (ContractNet-handleCpf)  FAP agent " + this.accessPoint.getLocalName() + ": Refused contract from " + cfp.getSender().getName());
            throw new RefuseException("proposal-refused");
        }
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        System.out.println(" (ContractNet-handleAcceptProposal)  FAP Agent " + this.accessPoint.getLocalName() + ": Proposal accepted" + " from " + cfp.getSender().getName());
        TrafficPoint requestPoint = null;
        try {
            requestPoint = (TrafficPoint) accept.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
            throw new FailureException("failed-read-trafficPoint-obj");
        }
        if (this.accessPoint.serveRequest(requestPoint)) {
            System.out.println("(ContractNet-handleAcceptProposal) FAP Agent " + this.accessPoint.getLocalName() + ": Request accepted, connecting to Traffic Point" + " from " + cfp.getSender().getName());
            jade.lang.acl.ACLMessage inform = accept.createReply();
            inform.setPerformative(jade.lang.acl.ACLMessage.INFORM);
            return inform;
        } else {
            System.out.println(" (ContractNet-handleAcceptProposal) FAP Agent " + this.accessPoint.getLocalName() + ": Request denied, refusing connection" + " from " + cfp.getSender().getName());
            throw new FailureException("refused-traffic-request");
        }
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println(" (ContractNet-handleRejectProposal) FAP Agent " + this.accessPoint.getLocalName() + ": Proposal rejected from " + cfp.getSender().getLocalName());
    }
}
