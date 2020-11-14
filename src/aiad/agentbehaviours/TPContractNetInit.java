package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;


public class TPContractNetInit extends ContractNetInitiator {

    TrafficPoint trafficPoint;
    Environment env;

    public TPContractNetInit(TrafficPoint a, ACLMessage msg, Environment env) {
        super(a, msg);
        this.trafficPoint = a;
        this.env = env;
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();
        cfp.setContent(String.valueOf(trafficPoint.getTraffic()));
        cfp.setConversationId("contract-net");
        ArrayList<AccessPoint> near_drones = env.getNearDrones(trafficPoint);

        for (int i = 0; i < near_drones.size(); i++) {
            cfp.addReceiver(new AID(near_drones.get(i).getLocalName(), false));
        }
        v.add(cfp);

        return v;
    }


    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        ArrayList<ACLMessage> aux = new ArrayList<>();
        ArrayList<String> aux_name = new ArrayList<>();

        int collected = 0;
        System.out.println(" (Init.handleAllResponses)  got " + responses.size() + " responses! ");

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
                auxiliar.setPerformative(ACLMessage.REJECT_PROPOSAL);
            acceptances.add(auxiliar);
        }

        if(collected < this.trafficPoint.getTraffic())
        {
            this.trafficPoint.setCollected(collected);
            this.trafficPoint.addBehaviour(new TPRequestProtocolInit(this.trafficPoint, new ACLMessage(ACLMessage.REQUEST), this.env));
        }
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        System.out.println(" (Init.handleAllResultNotifications) got " + resultNotifications.size() + " result notifs!");
    }

}