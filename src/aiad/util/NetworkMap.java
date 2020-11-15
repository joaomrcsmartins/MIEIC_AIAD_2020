package aiad.util;

import aiad.Coordinates;
import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class NetworkMap extends Canvas {
    private final Environment env;
    public int time = 0;

    protected ArrayList<JRadioButton> radioButtons = new ArrayList<>();
    protected JTextArea area;

    public NetworkMap(Environment env) {
        this.env = env;
        JFrame frame = new JFrame("Network Map");
        paintTextArea(frame);
        paintButton(frame);
        paintTrafficPointsAvailable(frame);
        this.setSize(600, 600);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    private void paintTrafficPointsAvailable(JFrame frame) {
        ButtonGroup bg = new ButtonGroup();
        int i = 0;
        for( TrafficPoint tp : this.env.getTrafficPoints())
        {
            JRadioButton r1= new JRadioButton(tp.getLocalName());
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
                for(JRadioButton radio: radioButtons)
                    if(radio.isSelected()) {
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
        g.setColor(Color.BLUE);
        g.fillOval(bounds.width - 90, 10, 10, 10);
        g.setColor(Color.RED);
        g.fillOval(bounds.width - 90, 30, 10, 10);
        g.setColor(Color.PINK);
        g.fillOval(bounds.width - 92, 48, 15, 15);
    }

    private void paintFlyingAccessPoints(Graphics g) {
        for (AccessPoint drone : env.getDrones()) {
            g.setColor(Color.BLUE);
            Coordinates coords = drone.getPos();
            g.fillOval(coords.getX() , coords.getY() , 10, 10);
            g.setColor(Color.MAGENTA);
            for (ClientPair client : drone.getClientPoints()) {
                Coordinates cli_coords = client.getKey().getPosition();
                g.drawLine(coords.getX() + 5, coords.getY() + 5, cli_coords.getX()  + 5, cli_coords.getY()  + 5);
            }
        }
    }

    private void paintTrafficPoints(Graphics g) {
        for (TrafficPoint tp : env.getTrafficPoints()) {
            Coordinates coords = tp.getPosition();
            g.setColor(Color.PINK);
            int range = (int) tp.getMaxRange() ;
            g.fillOval(coords.getX()  - range / 2 + 5,
                    coords.getY()  - range / 2 + 5,
                    range,
                    range

                    );
            g.setColor(Color.RED);
            g.fillOval(coords.getX(), coords.getY() , 10, 10);
        }
    }

    @Override
    public void paint(Graphics g) {
        Color base = g.getColor();
        paintLegend(g);
        paintTrafficPoints(g);
        paintFlyingAccessPoints(g);
        g.setColor(base);
        g.drawString("Time Passed: " + time + "s", 20, 20);
        time++;
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repaint();
    }

}
