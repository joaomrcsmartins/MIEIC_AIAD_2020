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
        Environment env = new Environment();
        env.startSystem();
    }
}
