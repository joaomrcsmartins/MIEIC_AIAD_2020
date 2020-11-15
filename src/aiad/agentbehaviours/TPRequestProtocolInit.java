package aiad.agentbehaviours;

import aiad.Environment;
import aiad.agents.TrafficPoint;
import aiad.agents.AccessPoint;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class TPRequestProtocolInit extends
        AchieveREInitiator {

    TrafficPoint trafficPoint;
    Environment env;
    Integer currentReceiverDrone;

    public TPRequestProtocolInit(TrafficPoint a, ACLMessage msg, Environment env) {
        super(a, msg);
        this.trafficPoint = a;
        this.env = env;
        this.currentReceiverDrone = Math.abs(new Random().nextInt() % env.getNearDrones(a).size());
    }

    @Override
    protected Vector prepareRequests(ACLMessage request) {
        System.out.println("Preparing request: " + trafficPoint.getTraffic());
        Vector v = new Vector();
        try {
            request.setContentObject(this.trafficPoint);
            ArrayList<AccessPoint> near_drones = env.getNearDrones(trafficPoint);
            request.addReceiver(new AID(near_drones.get(currentReceiverDrone).getLocalName(), false));
            v.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        System.out.println("handle refuse");
    }

    @Override
    protected void handleAgree(ACLMessage agree) {
        System.out.println("Drone accepted request.");
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        System.out.println("got " + resultNotifications.size() + " result notifs!");
    }

}