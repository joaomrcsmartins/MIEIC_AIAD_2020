package aiad.agentbehaviours;

import aiad.agents.AccessPoint;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class APCyclicContractNet extends TickerBehaviour {
    private AccessPoint ap;
    private static int period = 3000;
    APContractNetResponder resp;
    APSubContractNetResponder sub;
    APRequestProtocolResponse request_resp;

    public APCyclicContractNet(AccessPoint a) {
        super(a, period);
        this.ap = a;
    }

    @Override
    protected void onTick() {
        this.ap.removeBehaviour(resp);
        this.ap.removeBehaviour(sub);
        this.ap.removeBehaviour(request_resp);
        MessageTemplate templateSubContract = MessageTemplate.and(
                MessageTemplate.MatchConversationId("sub-contract-net"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        MessageTemplate templateContract = MessageTemplate.and(
                MessageTemplate.MatchConversationId("contract-net"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        resp = new APContractNetResponder(this.ap, templateContract, this.ap.getEnv());
        sub = new APSubContractNetResponder(this.ap, templateSubContract, this.ap.getEnv());
        request_resp = new APRequestProtocolResponse(this.ap, MessageTemplate.MatchPerformative(ACLMessage.REQUEST), this.ap.getEnv());
        this.ap.addBehaviour(resp);
        this.ap.addBehaviour(sub);
        this.ap.addBehaviour(request_resp);
    }

}

