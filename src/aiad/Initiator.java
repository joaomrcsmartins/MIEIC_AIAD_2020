package aiad;

import aiad.util.NetworkMap;

public class Initiator {
    public static void main(String[] args)  {
        Environment env = Environment.getInstance();
        env.startSystem();
        NetworkMap map = new NetworkMap(env);
    }
}
