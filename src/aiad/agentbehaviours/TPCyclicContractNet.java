package aiad.agentbehaviours;

import aiad.agents.TrafficPoint;
import sajas.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class TPCyclicContractNet extends TickerBehaviour {
    private TrafficPoint tp;
    private static int period = 1000;
    TPContractNetInit init;

    public TPCyclicContractNet(TrafficPoint tp) {
        super(tp, period);
        this.tp = tp;
    }

    @Override
    protected void onTick() {
        tp.removeBehaviour(init);
        if (tp.getCollected() == tp.getTraffic() || tp.getCollected() == 0) {
            init = new TPContractNetInit(tp, new ACLMessage(ACLMessage.CFP), tp.getEnv());
            tp.addBehaviour(init);
        }
    }
}
