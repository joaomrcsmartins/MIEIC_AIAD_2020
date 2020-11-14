package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

public class AccessPointContractNetResponder extends ContractNetResponder {

    AccessPoint accessPoint;
    Environment env;

    public AccessPointContractNetResponder(AccessPoint a, MessageTemplate mt, Environment env) {
        super(a, mt);
        this.accessPoint = a;
        this.env = env;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException {
        System.out.println(" (handleCpf) FAP agent " + this.accessPoint.getLocalName() + ": CFP received from " + cfp.getSender().getLocalName() + ". Traffic requested is " + cfp.getContent());
        double requestedTraffic = Double.parseDouble(cfp.getContent());
        //double proposal = this.accessPoint.evaluateRequest(requestedTraffic);
        //if (proposal > 0) {
        boolean proposal = this.accessPoint.isAvailable();
        if(proposal) {
            System.out.println(" (handleCpf) FAP agent " + this.accessPoint.getLocalName() + ": Proposing " + this.accessPoint.getAvailableTraffic() + " to "+ cfp.getSender().getLocalName());
            ACLMessage propose = cfp.createReply();
            propose.setPerformative(ACLMessage.PROPOSE);
            propose.setContent(String.valueOf(this.accessPoint.getAvailableTraffic()));
            return propose;
        } else {
            System.out.println(" (handleCpf)  FAP agent " + this.accessPoint.getLocalName() + ": Refused contract from " + cfp.getSender().getName());
            throw new RefuseException("proposal-refused");
        }
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        System.out.println(" (handleAcceptProposal)  FAP Agent " + this.accessPoint.getLocalName() + ": Proposal accepted" + " from " + cfp.getSender().getName());
        TrafficPoint requestPoint = null;
        try {
            requestPoint = (TrafficPoint) accept.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
            throw new FailureException("failed-read-trafficPoint-obj");
        }
        if (this.accessPoint.serveRequest(requestPoint)) {
            System.out.println("(handleAcceptProposal) FAP Agent " + this.accessPoint.getLocalName() + ": Request accepted, connecting to Traffic Point" + " from " + cfp.getSender().getName());
            jade.lang.acl.ACLMessage inform = accept.createReply();
            inform.setPerformative(jade.lang.acl.ACLMessage.INFORM);

            //testing: inform former traffic point that will no longer support it
           /* TrafficPoint tp_client = this.accessPoint.getCloserClient();
            if(tp_client != null)
            {
                this.accessPoint.addBehaviour(new RequestTrafficPointShutdownRequestInit(this.accessPoint,new ACLMessage(ACLMessage.CFP),tp_client));
                MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                tp_client.addBehaviour(new RequestTrafficPointShutdownRequestResponder(tp_client,template));
            }*/


            return inform;
        } else {
            System.out.println(" (handleAcceptProposal) FAP Agent " + this.accessPoint.getLocalName() + ": Request denied, refusing connection" + " from " + cfp.getSender().getName());
            throw new FailureException("refused-traffic-request");
        }
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println(" (handleRejectProposal) FAP Agent " + this.accessPoint.getLocalName() + ": Proposal rejected from " + cfp.getSender().getLocalName());
    }
}
