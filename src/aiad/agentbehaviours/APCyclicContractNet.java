package aiad.agentbehaviours;

import aiad.agents.AccessPoint;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class APCyclicContractNet extends TickerBehaviour {
    private AccessPoint ap;
    private static int period = 5000;
    APContractNetResponder resp;

    public APCyclicContractNet(AccessPoint a) {
        super(a, period);
        this.ap = a;
    }

    @Override
    protected void onTick() {
        this.ap.removeBehaviour(resp);
        MessageTemplate templateContract = MessageTemplate.and(
                MessageTemplate.MatchConversationId("contract-net"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        resp = new APContractNetResponder(this.ap, templateContract, this.ap.getEnv());
        this.ap.addBehaviour(resp);
    }

}

