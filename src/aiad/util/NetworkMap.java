package aiad.util;

import aiad.Coordinates;
import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.FlyingAccessPoint;

import javax.swing.*;
import java.awt.*;

public class NetworkMap extends Canvas {
    private final Environment env;

    public NetworkMap(Environment env) {
        this.env = env;
        JFrame frame = new JFrame("Network Map");
        this.setSize(600, 600);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void paint(Graphics g) {
        Color base = g.getColor();
        paintLegend(g);
        paintTrafficPoints(g);
        paintFlyingAccessPoints(g);
        g.setColor(base);
    }

    private void paintLegend(Graphics g) {
        g.setColor(Color.BLACK);
        Rectangle bounds = g.getClipBounds();
        g.drawRect(bounds.width-100,0,100,80);
        g.drawString("FAP",bounds.width-70,20);
        g.drawString("TrafficPoint",bounds.width-70,40);
        g.drawString("TP Range", bounds.width-70,60);
        g.setColor(Color.BLUE);
        g.fillOval(bounds.width-90,10,10,10);
        g.setColor(Color.RED);
        g.fillOval(bounds.width-90,30,10,10);
        g.setColor(Color.PINK);
        g.fillOval(bounds.width-92,48,15,15);
    }

    private void paintFlyingAccessPoints(Graphics g) {
        for(FlyingAccessPoint drone : env.getDrones()) {
            g.setColor(Color.BLUE);
            Coordinates coords = drone.getPos();
            g.fillOval(coords.getX()*10,coords.getY()*10,10,10);
            g.setColor(Color.MAGENTA);
            for(ClientPair client : drone.getClientPoints()) {
                Coordinates cli_coords = client.getKey().getPosition();
                g.drawLine(coords.getX()*10+5,coords.getY()*10+5,cli_coords.getX()*10+5,cli_coords.getY()*10+5);
            }
        }
    }

    private void paintTrafficPoints(Graphics g) {
        for(TrafficPoint tp : env.getTrafficPoints()) {
            Coordinates coords = tp.getPosition();
            g.setColor(Color.PINK);
            int range = (int) tp.getMaxRange() * 10;
            g.fillArc(coords.getX()*10-range/2+5,
                    coords.getY()*10-range/2+5,
                    range,
                    range,
                    0,
                    360);
            g.setColor(Color.RED);
            g.fillOval(coords.getX()*10,coords.getY()*10,10,10);
        }
    }
}
