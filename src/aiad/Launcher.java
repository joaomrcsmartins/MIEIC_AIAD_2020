package aiad;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aiad.agents.AccessPoint;
import aiad.agents.TrafficPoint;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import sajas.sim.repast3.Repast3Launcher;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;

public class Launcher extends Repast3Launcher {

    private static final boolean BATCH_MODE = true;

    private int N = 10;

    private int FILTER_SIZE = 5;

    private double FAILURE_PROBABILITY_GOOD_PROVIDER = 0.2;
    private double FAILURE_PROBABILITY_BAD_PROVIDER = 0.8;

    private int N_CONTRACTS = 100;

    public static final boolean USE_RESULTS_COLLECTOR = true;

    public static final boolean SEPARATE_CONTAINERS = false;

    private ContainerController mainContainer;
    private ContainerController agentContainer;

    private ArrayList<TrafficPoint> traffic_points;
    private ArrayList<AccessPoint> drones;

    private static List<DefaultDrawableNode> nodes;

    private boolean runInBatchMode;

    public Launcher(boolean runInBatchMode) {
        super();
        this.runInBatchMode = runInBatchMode;
    }

    public static DefaultDrawableNode getNode(String label) {
        for(DefaultDrawableNode node : nodes) {
            if(node.getNodeLabel().equals(label)) {
                return node;
            }
        }
        return null;
    }

    public int getN() {
        return N;
    }

    public void setN(int N) {
        this.N = N;
    }

    public int getFILTER_SIZE() {
        return FILTER_SIZE;
    }

    public void setFILTER_SIZE(int FILTER_SIZE) {
        this.FILTER_SIZE = FILTER_SIZE;
    }

    public double getFAILURE_PROBABILITY_GOOD_PROVIDER() {
        return FAILURE_PROBABILITY_GOOD_PROVIDER;
    }

    public void setFAILURE_PROBABILITY_GOOD_PROVIDER(double FAILURE_PROBABILITY_GOOD_PROVIDER) {
        this.FAILURE_PROBABILITY_GOOD_PROVIDER = FAILURE_PROBABILITY_GOOD_PROVIDER;
    }

    public double getFAILURE_PROBABILITY_BAD_PROVIDER() {
        return FAILURE_PROBABILITY_BAD_PROVIDER;
    }

    public void setFAILURE_PROBABILITY_BAD_PROVIDER(double FAILURE_PROBABILITY_BAD_PROVIDER) {
        this.FAILURE_PROBABILITY_BAD_PROVIDER = FAILURE_PROBABILITY_BAD_PROVIDER;
    }

    public int getN_CONTRACTS() {
        return N_CONTRACTS;
    }

    public void setN_CONTRACTS(int N_CONTRACTS) {
        this.N_CONTRACTS = N_CONTRACTS;
    }

    //este
    @Override
    public String[] getInitParam() {
        return new String[] {"N", "FILTER_SIZE", "FAILURE_PROBABILITY_GOOD_PROVIDER", "FAILURE_PROBABILITY_BAD_PROVIDER", "N_CONTRACTS"};
    }

    //este
    @Override
    public String getName() {
        return "Service Consumer/Provider -- SAJaS Repast3 Test";
    }

    //este
    @Override
    protected void launchJADE() {

        Runtime rt = Runtime.instance();
        Profile p1 = new ProfileImpl();
        mainContainer = rt.createMainContainer(p1);

        if(SEPARATE_CONTAINERS) {
            Profile p2 = new ProfileImpl();
            agentContainer = rt.createAgentContainer(p2);
        } else {
            agentContainer = mainContainer;
        }

        launchAgents();
    }

    private void launchAgents() {
        Random random = new Random(System.currentTimeMillis());

        int N_TRAFFICPOINT = N;
        int N_DRONE = 4*N;

        traffic_points = new ArrayList<>();
        drones = new ArrayList<>();
        nodes = new ArrayList<DefaultDrawableNode>();

        try {

            AID resultsCollectorAID = null;
            // create trafficpoints
            for (int i = 0; i < N_TRAFFICPOINT; i++) {
                TrafficPoint pa = new TrafficPoint((double) 120, new Coordinates(150,100));
                agentContainer.acceptNewAgent("TrafficPoint" + i, pa).start();
                DefaultDrawableNode node =
                        generateNode("TrafficPoint" + i, Color.WHITE,
                                random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));
                traffic_points.add(pa);
                nodes.add(node);
                pa.setNode(node);
            }
            // create drones
            for (int i = 0; i < N_DRONE; i++) {
                AccessPoint ca = new AccessPoint(120, new Coordinates(120, 100));
                mainContainer.acceptNewAgent("Drone" + i, ca).start();
                drones.add(ca);
                DefaultDrawableNode node =
                        generateNode("Drone" + i, Color.RED,
                                WIDTH/2+random.nextInt(WIDTH/2),HEIGHT/2+random.nextInt(HEIGHT/2));
                nodes.add(node);
                ca.setNode(node);
            }


        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

    private DefaultDrawableNode generateNode(String label, Color color, int x, int y) {
        OvalNetworkItem oval = new OvalNetworkItem(x,y);
        oval.allowResizing(false);
        oval.setHeight(5);
        oval.setWidth(5);

        DefaultDrawableNode node = new DefaultDrawableNode(label, oval);
        node.setColor(color);

        return node;
    }

//	@Override
//	public void setup() {
//		super.setup();
//
//		// property descriptors
//		// ...
//	}

    @Override
    public void begin() {
        super.begin();
        if(!runInBatchMode) {
            buildAndScheduleDisplay();
        }
    }

    private DisplaySurface dsurf;
    private int WIDTH = 200, HEIGHT = 200;
    private OpenSequenceGraph plot;

    private void buildAndScheduleDisplay() {

        // display surface
        if (dsurf != null) dsurf.dispose();
        dsurf = new DisplaySurface(this, "Service Consumer/Provider Display");
        registerDisplaySurface("Service Consumer/Provider Display", dsurf);
        Network2DDisplay display = new Network2DDisplay(nodes,WIDTH,HEIGHT);
        dsurf.addDisplayableProbeable(display, "Network Display");
        dsurf.addZoomable(display);
        addSimEventListener(dsurf);
        dsurf.display();

        // graph
        if (plot != null) plot.dispose();
        plot = new OpenSequenceGraph("Service performance", this);
        plot.setAxisTitles("time", "% successful service executions");

       /* plot.addSequence("Consumers", new Sequence() {
            public double getSValue() {
                // iterate through consumers
                double v = 0.0;
                for(int i = 0; i < traffic_points.size(); i++) {
                    v += traffic_points.get(i).getMovingAverage(10);
                }
                return v / traffic_points.size();
            }
        });
        plot.addSequence("Filtering Consumers", new Sequence() {
            public double getSValue() {
                // iterate through filtering consumers
                double v = 0.0;
                for(int i = 0; i < drones.size(); i++) {
                    v += drones.get(i).getMovingAverage(10);
                }
                return v / drones.size();
            }
        });*/
        plot.display();

        getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(100, plot, "step", Schedule.LAST);
    }


    /**
     * Launching Repast3
     * @param args
     */
    public static void main(String[] args) {
        boolean runMode = !BATCH_MODE;   // BATCH_MODE or !BATCH_MODE
        Environment env = Environment.getInstance();
        env.startSystem();

        SimInit init = new SimInit();
        init.setNumRuns(1);   // works only in batch mode
        init.loadModel(new Launcher(runMode), null, runMode);
    }

}
