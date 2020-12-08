package aiad.util;

import uchicago.src.sim.gui.NetworkDrawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.gui.TextDisplay;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.awt.*;

public class TextDrawableNode extends DefaultDrawableNode {
    int origX;
    int origY;
    private final TextDisplay textBox;
    public TextDrawableNode(String label, String value, NetworkDrawable drawable) {
        super(label,drawable);
        origX = (int) drawable.getX();
        origY = (int) drawable.getY();
        textBox = new TextDisplay( origX,origY,Color.WHITE);
        textBox.addLine(value);
        textBox.setBoxVisible(false);
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        super.draw(simGraphics);
        int newX = simGraphics.getDisplayWidth()/500 * (int)(origX*1.18);
        int newY = simGraphics.getDisplayHeight()/500* (int)(origY*1.18);
        textBox.setX(newX);
        textBox.setY(newY);
        System.out.println("Box x: " + textBox.getX());
        System.out.println("\tBox y: " + textBox.getY());
        System.out.println("\tSim wid: " + simGraphics.getDisplayWidth());
        System.out.println("\tSim hei: " + simGraphics.getDisplayHeight());
        textBox.drawDisplay(simGraphics);
        //simGraphics.drawOval(Color.CYAN);
    }
}
