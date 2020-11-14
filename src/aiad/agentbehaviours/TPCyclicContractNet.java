package aiad.agentbehaviours;

import aiad.TrafficPoint;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class TPCyclicContractNet extends TickerBehaviour {
    private TrafficPoint tp;
    private static int period = 6000;

    public TPCyclicContractNet(TrafficPoint tp) {
        super(tp, period);
        this.tp = tp;
    }

    @Override
    protected void onTick() {
        if (tp.getCollected() == tp.getTraffic())
            tp.addBehaviour(new TPContractNetInit(tp, new ACLMessage(ACLMessage.CFP), tp.getEnv()));
    }
}
