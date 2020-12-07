package aiad;

import aiad.agents.AccessPoint;
import aiad.agents.TrafficPoint;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.max;

public class Launcher extends Repast3Launcher {

    private static final boolean BATCH_MODE = true;

    private int N = 5;

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
        for (DefaultDrawableNode node : nodes) {
            if (node.getNodeLabel().equals(label)) {
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

    //este
    @Override
    public String[] getInitParam() {
        return new String[]{"N"};
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

        if (SEPARATE_CONTAINERS) {
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
        int N_DRONE = N*2;

        traffic_points = new ArrayList<>();
        drones = new ArrayList<>();
        nodes = new ArrayList<DefaultDrawableNode>();

        try {

            AID resultsCollectorAID = null;
            // create trafficpoints
            //for (int i = 0; i < N_TRAFFICPOINT; i++) {
                //int x = random.nextInt(10);
                //int y = random.nextInt(10);

                TrafficPoint tp = new TrafficPoint((double) 120, new Coordinates(150, 200));
                agentContainer.acceptNewAgent("loki" , tp).start();
                DefaultDrawableNode node =
                        generateNode("loki", Color.RED,
                                150, 200);
                traffic_points.add(tp);
                nodes.add(node);
                tp.setNode(node);

            TrafficPoint tp2 = new TrafficPoint((double) 120, new Coordinates(250, 200));
            agentContainer.acceptNewAgent("bobby" , tp2).start();
            DefaultDrawableNode node2 =
                    generateNode("bobby", Color.RED,
                            250, 200);
            traffic_points.add(tp2);
            nodes.add(node2);
            tp2.setNode(node2);
            //}
            // create drones
           // for (int i = 0; i < N_DRONE; i++) {
                //int x = random.nextInt(10);
                //int y = random.nextInt(10);
                AccessPoint ca = new AccessPoint(100, new Coordinates(160,200));
                agentContainer.acceptNewAgent("dronezoe" , ca).start();
                drones.add(ca);
                DefaultDrawableNode node3 =
                        generateNode("dronezoe", Color.BLUE,
                                160,200);
                nodes.add(node3);
                ca.setNode(node3);
           // }

            AccessPoint ca2 = new AccessPoint(80, new Coordinates(20,200));
            agentContainer.acceptNewAgent("dronedaisy" , ca2).start();
            drones.add(ca2);
            DefaultDrawableNode node4 =
                    generateNode("dronedaisy", Color.BLUE,
                            20,200);
            nodes.add(node4);
            ca2.setNode(node4);

            AccessPoint ca3 = new AccessPoint(140, new Coordinates(300,200));
            agentContainer.acceptNewAgent("droneluna" , ca3).start();
            drones.add(ca3);
            DefaultDrawableNode node5 =
                    generateNode("droneluna", Color.BLUE,
                            300,200);
            nodes.add(node5);
            ca3.setNode(node5);

            Environment.setInstance(traffic_points, drones);

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


    }

    private DefaultDrawableNode generateNode(String label, Color color, int x, int y) {
        OvalNetworkItem oval = new OvalNetworkItem(x, y);
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
        if (!runInBatchMode) {
            buildAndScheduleDisplay();
        }
    }

    private DisplaySurface dsurf;
    private int WIDTH = 300, HEIGHT = 300;
    private OpenSequenceGraph plot;

    private void buildAndScheduleDisplay() {

        // display surface
        if (dsurf != null) dsurf.dispose();
        dsurf = new DisplaySurface(this, "Service Consumer/Provider Display");
        registerDisplaySurface("Service Consumer/Provider Display", dsurf);
        Network2DDisplay display = new Network2DDisplay(nodes, WIDTH, HEIGHT);
        dsurf.addDisplayableProbeable(display, "Network Display");
        dsurf.addZoomable(display);
        addSimEventListener(dsurf);
        dsurf.display();

        // graph
        if (plot != null) plot.dispose();
        plot = new OpenSequenceGraph("Evolução do tráfego assegurado ao longo do tempo", this);
        plot.setAxisTitles("time", "% successful service executions");

        plot.addSequence("Trafico", new Sequence() {
            public double getSValue() {
                // iterate through consumers
                double traffic_provided = 0.0;
                double traffic_all = 0.0;
                for (int i = 0; i < traffic_points.size(); i++) {
                    traffic_provided += traffic_points.get(i).getCollected() == 1 ? traffic_points.get(i).getTraffic() : 0;
                    traffic_all += traffic_points.get(i).getTraffic();
                }
                return traffic_provided / traffic_all;
            }
        });
        plot.display();

        getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(100, plot, "step", Schedule.LAST);
    }


    /**
     * Launching Repast3
     *
     * @param args
     */
    public static void main(String[] args) {
        boolean runMode = !BATCH_MODE;   // BATCH_MODE or !BATCH_MODE

        SimInit init = new SimInit();
        init.setNumRuns(1);   // works only in batch mode
        init.loadModel(new Launcher(runMode), null, runMode);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Utilitary class from First Project
    //////////////////////////////////////////////////////////////////////////////////////
    public static class Environment {
        private static Environment env_instance = null;
        private ArrayList<TrafficPoint> traffic_points;
        private ArrayList<AccessPoint> drones;

        public Environment() {
            traffic_points = new ArrayList<>();
            drones = new ArrayList<>();
        }

        public Environment(ArrayList<TrafficPoint> tps, ArrayList<AccessPoint> ap) {
            traffic_points = tps;
            drones = ap;
            env_instance = this;
        }

        public void setTrafficPoints(ArrayList<TrafficPoint> tps) {
            traffic_points = tps;
        }

        public void setDrones(ArrayList<AccessPoint> ap) {
            drones = ap;
        }


        public AccessPoint getDroneByName(String name) {
            for (AccessPoint drone : drones) {
                if (drone.getName().equals(name))
                    return drone;
            }
            return null;
        }

        public TrafficPoint getTrafficPointByName(String name) {
            for (TrafficPoint trafficPoint : traffic_points) {
                if (trafficPoint.getTPName().equals(name))
                    return trafficPoint;
            }
            return null;
        }

        public ArrayList<TrafficPoint> getTrafficPoints() {
            return traffic_points;
        }

        public ArrayList<AccessPoint> getDrones() {
            return drones;
        }

        public ArrayList<AccessPoint> getNearDrones(AccessPoint actual_drone) {
            ArrayList<AccessPoint> near_drones = new ArrayList<>();
            for (AccessPoint drone : drones) {
                if (drone.getName().equals(actual_drone.getName()))
                    continue;

                if (actual_drone.isNear(drone))
                    near_drones.add(drone);
            }
            return near_drones;
        }

        public ArrayList<AccessPoint> getNearDrones(TrafficPoint actual_point) {
            ArrayList<AccessPoint> near_drones = new ArrayList<>();
            for (AccessPoint drone : drones) {
                double dist = actual_point.isNearDrone(drone);
                if (dist <= TrafficPoint.MAX_RANGE) {
                    near_drones.add(drone);
                }
            }
            return near_drones;
        }

        public Coordinates getPosInRange(Coordinates pos, double range) {
            Random rand = new Random(System.currentTimeMillis());
            double angle = rand.nextDouble() * 2 * Math.PI;
            double reach = rand.nextDouble() * range;
            int newX = max((int) (pos.getX() + (reach * Math.cos(angle))), 0);
            int newY = max((int) (pos.getY() + (reach * Math.sin(angle))), 0);
            Coordinates newC = new Coordinates(newX, newY);
            return newC;
        }

        public double getPercentageOfTrafficCovered() {
            double allTraffic = 0, trafficCovered = 0;
            for (TrafficPoint tp : this.traffic_points) {
                allTraffic += tp.getTraffic();

                // If getCollected() == 1, then the entire traffic of the tp is covered.
                trafficCovered = tp.getCollected() == 1 ? trafficCovered + tp.getTraffic() : trafficCovered;
            }

            // If there is no traffic to be covered, we will assume all is covered.
            if (allTraffic == 0) return 100;

            return trafficCovered / allTraffic * 100;
        }

        public static Environment getInstance() {
            if (env_instance == null)
                env_instance = new Environment();
            return env_instance;
        }

        public static void setInstance(ArrayList<TrafficPoint> traffic_points, ArrayList<AccessPoint> access_points) {
            if (env_instance == null)
                env_instance = new Environment(traffic_points, access_points);
            else {
                env_instance.setDrones(access_points);
                env_instance.setTrafficPoints(traffic_points);
            }
        }
    }
}