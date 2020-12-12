package aiad;

import aiad.agents.AccessPoint;
import aiad.agents.TrafficPoint;
import aiad.util.TextDrawableNode;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.plot.OpenGraph;
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

    private int N_DRONES = 50;

    private int N_TRAFFIC_POINTS = 100;

    public static final boolean USE_RESULTS_COLLECTOR = true;

    public static final boolean SEPARATE_CONTAINERS = false;

    private ContainerController mainContainer;
    private ContainerController agentContainer;

    private ArrayList<TrafficPoint> traffic_points;
    private ArrayList<AccessPoint> drones;

    private static List<TextDrawableNode> nodes;

    private boolean runInBatchMode;

    private DisplaySurface dsurf;
    private final int WIDTH = 500, HEIGHT = 500;
    private ArrayList<OpenSequenceGraph> plots = new ArrayList<>();

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

    public int getN_DRONES() {
        return N_DRONES;
    }

    public void setN_DRONES(int N) {
        this.N_DRONES = N;
    }

    public int getN_TRAFFIC_POINTS() {
        return N_TRAFFIC_POINTS;
    }

    public void setN_TRAFFIC_POINTS(int n_TRAFFIC_POINTS) {
        N_TRAFFIC_POINTS = n_TRAFFIC_POINTS;
    }

    @Override
    public String[] getInitParam() {
        return new String[]{"N_DRONES","N_TRAFFIC_POINTS"};
    }

    @Override
    public String getName() {
        return "Service Consumer/Provider -- SAJaS Repast3 Test";
    }

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

        traffic_points = new ArrayList<>();
        drones = new ArrayList<>();
        nodes = new ArrayList<>();

        try {

            // create trafficpoints
            for (int i = 0; i < N_TRAFFIC_POINTS; i++) {
                int x = random.nextInt(400);
                int y = random.nextInt(400);
                int value = random.nextInt(120);

                TrafficPoint tp = new TrafficPoint((double) value, new Coordinates(x, y));
                agentContainer.acceptNewAgent("tp" + i, tp).start();
                TextDrawableNode node =
                        generateNode("tp" + i, Color.RED,
                                x, y, String.valueOf(value));
                traffic_points.add(tp);
                nodes.add(node);
                tp.setNode(node);

            }
            // create drones
            for (int i = 0; i < N_DRONES; i++) {
                int x = random.nextInt(400);
                int y = random.nextInt(400);
                AccessPoint ca = new AccessPoint(new Coordinates(x, y));
                agentContainer.acceptNewAgent("ap" + i, ca).start();
                drones.add(ca);
                TextDrawableNode node3 =
                        generateNode("ap" + i, Color.BLUE,
                                x, y, String.valueOf(ca.getTrafficCapacity()));
                nodes.add(node3);
                ca.setNode(node3);
            }

            Environment.setInstance(traffic_points, drones);

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


    }

    private TextDrawableNode generateNode(String label, Color color, int x, int y, String value) {
        OvalNetworkItem oval = new OvalNetworkItem(x, y);
        oval.allowResizing(false);
        oval.setHeight(5);
        oval.setWidth(5);

        TextDrawableNode node = new TextDrawableNode(label, value, oval);
        node.setColor(color);

        return node;
    }

    @Override
    public void begin() {
        super.begin();
        if (!runInBatchMode) {
            buildAndScheduleDisplay();
        }
    }

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
        getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        setupPlots();
    }

    private void setupPlots() {
        plots.forEach(OpenGraph::dispose);
        plots.clear();

        // plot requested traffic coverage
        OpenSequenceGraph coveragePlot = new OpenSequenceGraph("Evolution of assured traffic over time", this,"traffic_covered.txt",0);
        coveragePlot.setAxisTitles("time", "% Traffic coverage");
        coveragePlot.addSequence("Traffic Covered / tick", () -> {
            // iterate through consumers
            double traffic_provided = 0.0;
            double traffic_all = 0.0;
            for (TrafficPoint traffic_point : traffic_points) {
                traffic_provided += traffic_point.getCollected() == 1 ? traffic_point.getTraffic() : 0;
                traffic_all += traffic_point.getTraffic();
            }
            return traffic_provided / traffic_all * 100;
        });
        coveragePlot.display();
        plots.add(coveragePlot);

        // plot number of contracts
        OpenSequenceGraph contractsPlot = new OpenSequenceGraph("Evolution of the number of contracts initiated over time", this, "n_contracts.txt", 0);
        contractsPlot.setAxisTitles("time", "N contracts");
        contractsPlot.addSequence("N contracts / tick", () -> Environment.contracts);
        contractsPlot.display();
        plots.add(contractsPlot);

        // plot average number of requests per Traffic point
        OpenSequenceGraph contractsAvgPlot = new OpenSequenceGraph("Evolution of the number of contracts per Traffic Point over time", this, "n_contracts_avg.txt", 0);
        contractsAvgPlot.setAxisTitles("time", "N contracts per TP");
        contractsAvgPlot.addSequence("N contracts avg / tick", () -> (double) Environment.contracts / Environment.getInstance().getTrafficPoints().size());
        contractsAvgPlot.display();
        plots.add(contractsAvgPlot);

        // plot percentage of satisfied Traffic Points
        OpenSequenceGraph satisfiedPlot = new OpenSequenceGraph("Evolution of satisfied TPs over time", this, "satisfied_tp.txt", 0);
        satisfiedPlot.setAxisTitles("time", "% satisfied TP");
        satisfiedPlot.addSequence("Satisfied TPs / tick", () -> {
            double satisfied = traffic_points.stream().filter(TrafficPoint::isSatisfied).count();
            return satisfied / traffic_points.size() * 100;
        });
        satisfiedPlot.display();
        plots.add(satisfiedPlot);

        // plot ratio of subContracts per Contracts
        OpenSequenceGraph ratioSubConPlot = new OpenSequenceGraph("Evolution of the % of SubContracts over time", this, "ratio_subcontracts.txt", 0);
        ratioSubConPlot.setAxisTitles("time", "N SubCon / (N SubCon + N Con) ");
        ratioSubConPlot.addSequence("Ratio of SubContracts / tick", () -> (double) (Environment.subContracts * 100) / (Environment.subContracts + Environment.contracts));
        ratioSubConPlot.display();
        plots.add(ratioSubConPlot);

        plots.forEach(plot -> getSchedule().scheduleActionAtEnd(plot, "writeToFile"));
        plots.forEach(plot -> getSchedule().scheduleActionAtInterval(100, plot, "step", Schedule.LAST));
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
    // Utilitarian class from First Project
    //////////////////////////////////////////////////////////////////////////////////////
    public static class Environment {
        private static Environment env_instance = null;
        private ArrayList<TrafficPoint> traffic_points;
        private ArrayList<AccessPoint> drones;

        public static int contracts = 0;
        public static int subContracts = 0;

        public Environment() {
            traffic_points = new ArrayList<>();
            drones = new ArrayList<>();
        }

        public Environment(ArrayList<TrafficPoint> tps, ArrayList<AccessPoint> ap) {
            traffic_points = tps;
            drones = ap;
            env_instance = this;
        }


        public static void sumRequest() {
            Environment.contracts++;
        }

        public static void sumSubContract() {
            Environment.subContracts++;
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
