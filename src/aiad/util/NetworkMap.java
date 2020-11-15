package aiad.util;

import aiad.Coordinates;
import aiad.Environment;
import aiad.agents.TrafficPoint;
import aiad.agents.AccessPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class NetworkMap extends Canvas {
    private final Environment env;
    public int time = 0;

    protected ArrayList<JRadioButton> radioButtons = new ArrayList<>();
    protected JTextArea area;

    private static final Color FAPColor = Color.BLUE;
    private static final Color TPColor = Color.RED;
    private static final Color ConnectionColor = Color.MAGENTA;
    private static final Color RangeColor = Color.PINK;

    private static final int pointRadius = 5;

    private CsvFileWriter csvMetricsFile;

    public NetworkMap(Environment env, CsvFileWriter csvMetricsFile) {
        this.env = env;
        JFrame frame = new JFrame("Network Map");
        paintTextArea(frame);
        paintButton(frame);
        paintTrafficPointsAvailable(frame);
        this.setSize(600, 600);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        this.csvMetricsFile = csvMetricsFile;
    }

    private void paintTrafficPointsAvailable(JFrame frame) {
        ButtonGroup bg = new ButtonGroup();
        int i = 0;
        for (TrafficPoint tp : this.env.getTrafficPoints()) {
            JRadioButton r1 = new JRadioButton(tp.getLocalName());
            r1.setName(tp.getName());
            r1.setBounds(300, 480 + i, 100, 30);
            bg.add(r1);
            frame.add(r1);
            i += 30;
            radioButtons.add(r1);
        }

    }

    private void paintTextArea(JFrame frame) {
        JTextArea area = new JTextArea("0");
        area.setBounds(400, 500, 50, 20);
        frame.add(area);
        this.area = area;
    }

    public void paintButton(JFrame frame) {
        JButton b = new JButton("Submit");
        b.setBounds(500, 500, 95, 30);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JRadioButton radio : radioButtons)
                    if (radio.isSelected()) {
                        TrafficPoint point = env.getTrafficPointByName(radio.getName());
                        point.setTraffic((double) Integer.parseInt(area.getText()));
                    }
            }
        });
        frame.add(b);
    }

    private void paintLegend(Graphics g) {
        g.setColor(Color.BLACK);
        Rectangle bounds = g.getClipBounds();
        g.drawRect(bounds.width - 100, 0, 100, 80);
        g.drawString("FAP", bounds.width - 70, 20);
        g.drawString("TrafficPoint", bounds.width - 70, 40);
        g.drawString("TP Range", bounds.width - 70, 60);
        g.setColor(FAPColor);
        g.fillOval(bounds.width - 90, 10, pointRadius*2, pointRadius*2);
        g.setColor(TPColor);
        g.fillOval(bounds.width - 90, 30, pointRadius*2, pointRadius*2);
        g.setColor(RangeColor);
        g.fillOval(bounds.width - 92, 48, 15, 15);
    }

    private void paintFlyingAccessPoints(Graphics g) {
        for (AccessPoint drone : env.getDrones()) {
            g.setColor(FAPColor);
            Coordinates coords = drone.getPos();
            g.fillOval(coords.getX(), coords.getY(), pointRadius*2, pointRadius*2);
            g.setColor(ConnectionColor);
            for (ClientPair client : drone.getClientPoints()) {
                Coordinates cli_coords = client.getKey().getPosition();
                g.drawLine(coords.getX() + pointRadius, coords.getY() + pointRadius, cli_coords.getX() + pointRadius, cli_coords.getY() + pointRadius);
            }
        }
    }

    private void paintTPRangeAreas(Graphics g, ArrayList<Coordinates> tps,int range) {
        g.setColor(RangeColor);
        for( Coordinates tp:tps) {
            g.fillOval(tp.getX() - range / 2 + pointRadius,
                    tp.getY() - range / 2 + pointRadius,
                    range,
                    range
            );
        }
    }

    private void paintTP(Graphics g, ArrayList<Coordinates> tps, int range) {
        for(Coordinates coords : tps) {
            g.setColor(Color.BLACK);
            g.drawOval(coords.getX() - (range+2) / 2 + pointRadius,
                    coords.getY() - (range+2) / 2 + pointRadius,
                    range + 1,
                    range + 1
            );
            g.setColor(TPColor);
            g.fillOval(coords.getX(), coords.getY(), pointRadius*2, pointRadius*2);
        }
    }
    private void paintTrafficPoints(Graphics g) {
        int range = (int) TrafficPoint.MAX_RANGE * 2;
        ArrayList<Coordinates> tps = env.getTrafficPoints().stream().map(TrafficPoint::getPosition).collect(Collectors.toCollection(ArrayList::new));
        paintTPRangeAreas(g,tps,range);
        paintTP(g,tps,range);
    }

    @Override
    public void paint(Graphics g) {
        Color base = g.getColor();
        paintLegend(g);
        paintTrafficPoints(g);
        paintFlyingAccessPoints(g);
        g.setColor(base);
        g.drawString("Time Passed: " + time + "s", 20, 500);
        g.drawString("Percentage of traffic covered: " + env.getPercentageOfTrafficCovered() + " %", 20, 520);

        this.csvMetricsFile.write( time + "," +  env.getPercentageOfTrafficCovered());
        time++;
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repaint();
    }

}
