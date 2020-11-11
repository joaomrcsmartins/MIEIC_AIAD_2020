package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.FlyingAccessPoint;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;


public class TrafficPointContractNetInit extends ContractNetInitiator {

    TrafficPoint trafficPoint;
    Environment env;

    public TrafficPointContractNetInit(TrafficPoint a, ACLMessage msg, Environment env) {
        super(a, msg);
        this.trafficPoint = a;
        this.env = env;
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();
        cfp.setContent("This is my new capacity: " + trafficPoint.getTraffic());
        ArrayList<FlyingAccessPoint> near_drones = env.getNearDrones(trafficPoint);

        System.out.println(near_drones);
        for (int i = 0; i < near_drones.size(); i++) {
            cfp.addReceiver(new AID(near_drones.get(i).getLocalName(), false));
        }
        v.add(cfp);

        return v;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        ArrayList<ACLMessage> aux = new ArrayList<>();
        int collected = 0;
        System.out.println("got " + responses.size() + " responses! ");

        Collections.sort(responses, new Comparator<ACLMessage>() {
            public int compare(ACLMessage aclMessage, ACLMessage t1) {
                String content = aclMessage.getContent();
                String content2 = t1.getContent();
                double value = Double.parseDouble(content);
                double value2 = Double.parseDouble(content2);
                return value < value2 ? 1 : 0;
            }
        });

        for (int i = 0; i < responses.size(); i++) {
            ACLMessage msg_reply = ((ACLMessage) responses.get(i)).createReply();
            ACLMessage msg = (ACLMessage) responses.get(i);
            String parseResponse = msg.getContent();
            double value = Double.parseDouble(parseResponse);
            if (this.trafficPoint.getTraffic() >= collected + value || collected < this.trafficPoint.getTraffic()) {
                msg_reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                try {
                    msg_reply.setContentObject(trafficPoint);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                aux.add(msg_reply);
                collected += value;
            } else {
                msg_reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                acceptances.add(msg_reply);
            }
        }

        for (ACLMessage auxiliar : aux) {
            if (collected < this.trafficPoint.getTraffic())
                auxiliar.setPerformative(ACLMessage.REJECT_PROPOSAL);
            acceptances.add(auxiliar);
        }

        //TODO: subcontract
        //if(collected < this.trafficPoint.getTraffic())

    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        System.out.println("got " + resultNotifications.size() + " result notifs!");
    }

}