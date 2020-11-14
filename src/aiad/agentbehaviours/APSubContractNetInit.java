package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import aiad.access_point.FlyingAccessPoint;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class APSubContractNetInit extends ContractNetInitiator {
    AccessPoint accessPoint;
    TrafficPoint trafficPoint;
    Environment env;

    public APSubContractNetInit(AccessPoint accessPoint, TrafficPoint trafficPoint, ACLMessage msg, Environment env) {
        super(accessPoint, msg);
        this.accessPoint = accessPoint;
        this.trafficPoint = trafficPoint;
        this.env = env;
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();
        cfp.setContent(" (Init.prepareCfps) This is my new capacity: " + trafficPoint.getTraffic());
        cfp.setConversationId("sub-contract-net");
        ArrayList<AccessPoint> near_drones = env.getNearDrones(accessPoint);

        for (int i = 0; i < near_drones.size(); i++) {
            cfp.addReceiver(new AID(near_drones.get(i).getLocalName(), false));
        }
        v.add(cfp);

        return v;
    }


    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        System.out.println(" (Init.handleAllResponses)  got " + responses.size() + " responses! ");

        ArrayList<ACLMessage> aux = new ArrayList<>();
        ArrayList<String> aux_name = new ArrayList<>();

        int collected = this.trafficPoint.getCollected();

        Collections.sort(responses, (Comparator<ACLMessage>) (aclMessage, t1) -> {
            String content = aclMessage.getContent();
            String content2 = t1.getContent();
            double value = (content.equals("proposal-refused")) ? 0.0: Double.parseDouble(content);
            double value2 = (content2.equals("proposal-refused")) ? 0.0 : Double.parseDouble(content2);
            return (int) (value2 - value);
        });

        for (int i = 0; i < responses.size(); i++) {
            System.out.println(" (Init.handleAllResponses) Response from: " + ((ACLMessage) responses.get(i)).getSender().getLocalName() + " content:" + ((ACLMessage) responses.get(i)).getContent() );
            aux_name.add(((ACLMessage) responses.get(i)).getSender().getName());
            ACLMessage msg_reply = ((ACLMessage) responses.get(i)).createReply();
            ACLMessage msg = (ACLMessage) responses.get(i);
            String parseResponse = msg.getContent();
            double value = (parseResponse.equals("proposal-refused"))? 0 :Double.parseDouble(parseResponse);
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
            {
                auxiliar.setPerformative(ACLMessage.REJECT_PROPOSAL);
            }
            acceptances.add(auxiliar);
        }

        if(collected >= this.trafficPoint.getTraffic())
        {
            System.out.println("bannananas" + this.trafficPoint.getTraffic());
            this.trafficPoint.setCollected(this.trafficPoint.getTraffic());
            System.out.println("meias: " + this.trafficPoint.getCollected());
        }

    }


    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        System.out.println(" (Init.handleAllResultNotifications) got " + resultNotifications.size() + " result notifs!");
    }
}
