package aiad.agentbehaviours;

import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class APCyclicContractNet extends TickerBehaviour {
    private AccessPoint ap;
    private static int period = 5000;

    public APCyclicContractNet(AccessPoint a) {
        super(a, period);
        this.ap = a;
    }

    @Override
    protected void onTick() {
        MessageTemplate templateContract = MessageTemplate.and(
                MessageTemplate.MatchConversationId("contract-net"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));

        this.ap.removeBehaviour(new APContractNetResponder(this.ap, templateContract, this.ap.getEnv()));
        this.ap.addBehaviour(new APContractNetResponder(this.ap, templateContract, this.ap.getEnv()));
    }

}

