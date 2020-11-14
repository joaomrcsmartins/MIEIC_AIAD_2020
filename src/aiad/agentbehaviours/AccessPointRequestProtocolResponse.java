package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import aiad.access_point.FlyingAccessPoint;
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
        System.out.println("(handleRequest) " + this.accessPoint.getLocalName() + " Received request: " + request);
        try {
            TrafficPoint tp = (TrafficPoint) request.getContentObject();
            System.out.println("Traffic point traffic: " + tp.getTraffic());
            //TODO: remove this cast.
            this.accessPoint.addBehaviour(new AccessPointContractNetInit((FlyingAccessPoint) this.accessPoint, tp, new ACLMessage(ACLMessage.CFP), this.env));
//            ACLMessage response = new ACLMessage()
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return request;
    }

}
