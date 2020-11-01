package aiad;

import jade.wrapper.StaleProxyException;

public class Initiator {
    public static void main(String[] args) throws StaleProxyException {
        Environment env = new Environment();
        env.startSystem();
    }
}
