package aiad.agentbehaviours;

import aiad.Launcher;
import aiad.agents.AccessPoint;
import aiad.agents.TrafficPoint;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.proto.AchieveREResponder;

public class APRequestProtocolResponse extends AchieveREResponder {

    AccessPoint accessPoint;
    Launcher.Environment env;

    public APRequestProtocolResponse(AccessPoint a, MessageTemplate mt, Launcher.Environment env) {
        super(a, mt);
        this.accessPoint = a;
        this.env = env;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws RefuseException {
        this.accessPoint.removeBehaviour(this.accessPoint.getSubcontract());
        System.out.println("(handleRequest) " + this.accessPoint.getLocalName() + " Received request: " + request);
        ACLMessage response = new ACLMessage(ACLMessage.AGREE);
        try {
            TrafficPoint tp = (TrafficPoint) request.getContentObject();
            System.out.println("Traffic point traffic: " + tp.getTraffic());
            this.accessPoint.setSubcontract(new APSubContractNetInit(this.accessPoint, tp, new ACLMessage(ACLMessage.CFP), this.env));
            this.accessPoint.addBehaviour(this.accessPoint.getSubcontract());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
