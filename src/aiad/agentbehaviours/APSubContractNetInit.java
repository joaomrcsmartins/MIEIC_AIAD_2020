package aiad.agentbehaviours;

import aiad.Launcher;
import aiad.agents.AccessPoint;
import aiad.agents.TrafficPoint;
import jade.lang.acl.ACLMessage;
import sajas.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class APSubContractNetInit extends ContractNetInitiator {
    AccessPoint accessPoint;
    TrafficPoint trafficPoint;
    Launcher.Environment env;

    public APSubContractNetInit(AccessPoint accessPoint, TrafficPoint trafficPoint, ACLMessage msg, Launcher.Environment env) {
        super(accessPoint, msg);
        this.accessPoint = accessPoint;
        this.trafficPoint = trafficPoint;
        this.env = env;
    }

    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();
        cfp.setContent(trafficPoint.getName());
        cfp.setConversationId("sub-contract-net");
        ArrayList<AccessPoint> near_drones = env.getNearDrones(accessPoint);

        for (int i = 0; i < near_drones.size(); i++) {
            cfp.addReceiver(new sajas.core.AID(near_drones.get(i).getLocalName(), false));
        }
        v.add(cfp);

        return v;
    }


    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        System.out.println(" (Init.handleAllResponses)  got " + responses.size() + " responses! ");

        ArrayList<ACLMessage> aux = new ArrayList<>();
        ArrayList<String> aux_name = new ArrayList<>();

        double collected = this.trafficPoint.getCollected();

        Collections.sort(responses, (Comparator<ACLMessage>) (aclMessage, t1) -> {
            String content = aclMessage.getContent();
            String content2 = t1.getContent();
            double value = (content.equals("proposal-refused")) ? 0.0 : Double.parseDouble(content);
            double value2 = (content2.equals("proposal-refused")) ? 0.0 : Double.parseDouble(content2);
            return (int) (value2 - value);
        });

        for (Object respons : responses) {
            System.out.println(" (Init.handleAllResponses) Response from: " + ((ACLMessage) respons).getSender().getName() + " content:" + ((ACLMessage) respons).getContent());
            aux_name.add(((ACLMessage) respons).getSender().getName());
            ACLMessage msg_reply = ((ACLMessage) respons).createReply();
            ACLMessage msg = (ACLMessage) respons;
            String parseResponse = msg.getContent();
            double value = (parseResponse.equals("proposal-refused")) ? 0 : Double.parseDouble(parseResponse);

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

        for (ACLMessage auxiliary : aux) {
            if (collected < this.trafficPoint.getTraffic()) {
                auxiliary.setPerformative(ACLMessage.REJECT_PROPOSAL);
            }
            acceptances.add(auxiliary);
        }

        if (collected >= this.trafficPoint.getTraffic()) {
            this.env.getTrafficPointByName(this.trafficPoint.getTPName()).setCollected(this.trafficPoint.getTraffic());
        }

    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        System.out.println(" (Init.handleAllResultNotifications) got " + resultNotifications.size() + " result notifs!");
    }
}
