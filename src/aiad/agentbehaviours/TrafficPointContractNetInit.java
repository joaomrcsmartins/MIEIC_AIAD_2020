package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import aiad.access_point.FlyingAccessPoint;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.ArrayList;
import java.util.Vector;

public class TrafficPointContractNetInit extends ContractNetInitiator {

    TrafficPoint trafficPoint;
    Environment env;

    public TrafficPointContractNetInit(TrafficPoint a, ACLMessage msg, Environment env) {
        super(a, msg);
        this.trafficPoint = a;
        this.env = env;
    }

    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();
        cfp.setContent("This is my new capacity");
        ArrayList<FlyingAccessPoint> near_drones =  env.getNearDrones(trafficPoint);

        for (int i = 0; i < near_drones.size(); i++) {
            cfp.addReceiver(new AID(near_drones.get(i).getLocalName(), false));
        }
        v.add(cfp);

        return v;
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {

        System.out.println("got " + responses.size() + " responses!");

        //TODO: choice algorithm
        for(int i=0; i<responses.size(); i++) {
            ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
            msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
            acceptances.add(msg);
        }
    }

    protected void handleAllResultNotifications(Vector resultNotifications) {
        System.out.println("got " + resultNotifications.size() + " result notifs!");
    }

}