package aiad;

import aiad.util.NetworkMap;
import jade.wrapper.StaleProxyException;

public class Initiator {
    public static void main(String[] args) throws StaleProxyException {
        Environment env = Environment.getInstance();
        env.startSystem();
        NetworkMap map = new NetworkMap(env);
    }
}
