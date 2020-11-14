package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetResponder;

public class AccessPointRequestProtocolResponse extends AchieveREResponder {

    AccessPoint accessPoint;
    Environment env;

    public AccessPointRequestProtocolResponse(AccessPoint a, MessageTemplate mt, Environment env) {
        super(a, mt);
        this.accessPoint = a;
        this.env = env;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws RefuseException {
        System.out.println("Received request: " + request.getContent());
        try {
            TrafficPoint tp = (TrafficPoint) request.getContentObject();
            this.accessPoint.addBehaviour(new TrafficPointContractNetInit(tp, new ACLMessage(ACLMessage.CFP), this.env));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return request;


    }

}
