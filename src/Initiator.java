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
        profile.setParameter(Profile.GUI,"true");
        ContainerController ac = rt.createMainContainer(profile);
        AgentController agc = ac.createNewAgent("joao","TrafficPoint",null);
        agc.start();
    }
}
