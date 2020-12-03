package aiad.agentbehaviours;

import aiad.Launcher;
import aiad.agents.AccessPoint;
import aiad.agents.TrafficPoint;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sajas.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;


public class TPContractNetInit extends ContractNetInitiator {

    TrafficPoint trafficPoint;
    Launcher.Environment env;

    int collected_aux;

    public TPContractNetInit(TrafficPoint a, ACLMessage msg, Launcher.Environment env) {
        super(a, msg);
        this.trafficPoint = a;
        this.env = env;
        this.collected_aux = 0;
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

    public String parseResponse(String response) {
        if (response.contains(":")) {
            return response.substring(0, response.indexOf(":"));
        } else
            return response;
    }

    public String collectedAux(String response) {
        if (response.contains(":")) {
            collected_aux += Double.parseDouble(response.substring(0, response.indexOf(":")));
            return response.substring(0, response.indexOf(":"));
        } else
            return response;
    }


    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        ArrayList<ACLMessage> aux = new ArrayList<>();
        ArrayList<String> aux_name = new ArrayList<>();

        double collected = 0;
        System.out.println(" (Init.handleAllResponses)  got " + responses.size() + " responses! ");

        Collections.sort(responses, (Comparator<ACLMessage>) (aclMessage, t1) -> {
            String content = parseResponse(aclMessage.getContent());
            String content2 = parseResponse(t1.getContent());
            double value = (content.equals("proposal-refused")) ? 0.0 : Double.parseDouble(content);
            double value2 = (content2.equals("proposal-refused")) ? 0.0 : Double.parseDouble(content2);
            return (int) (value2 - value);
        });

        for (Object response : responses) {
            System.out.println(" (Init.handleAllResponses) Response from: " + ((ACLMessage) response).getSender().getLocalName() + " content:" + ((ACLMessage) response).getContent());
            aux_name.add(((ACLMessage) response).getSender().getName());
            ACLMessage msg_reply = ((ACLMessage) response).createReply();
            ACLMessage msg = (ACLMessage) response;
            String parseResponse = msg.getContent();
            double value = parseResponse.equals("proposal-refused") ? 0 : Double.parseDouble(collectedAux(parseResponse));
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
            if (collected < this.trafficPoint.getTraffic())
                auxiliary.setPerformative(ACLMessage.REJECT_PROPOSAL);
            acceptances.add(auxiliary);
        }

        if (collected < this.trafficPoint.getTraffic()) {
            this.trafficPoint.setCollected(collected - collected_aux);
            this.trafficPoint.addBehaviour(new TPRequestProtocolInit(this.trafficPoint, new ACLMessage(ACLMessage.REQUEST), this.env));
        } else {
            this.env.getTrafficPointByName(this.trafficPoint.getName()).setCollected(1);
        }

    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        System.out.println(" (Init.handleAllResultNotifications) got " + resultNotifications.size() + " result notifs!");
    }

}