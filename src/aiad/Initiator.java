package aiad;

import aiad.access_point.FlyingAccessPoint;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Initiator {
    public static void main(String[] args) throws StaleProxyException {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        ContainerController ac = rt.createMainContainer(profile);
        Object[] agentArgs = new Object[]{120, new Coordinates(10, 10)};
        AgentController agc = ac.createNewAgent("joao", FlyingAccessPoint.class.getName(), agentArgs);
        agc.start();
    }
}
