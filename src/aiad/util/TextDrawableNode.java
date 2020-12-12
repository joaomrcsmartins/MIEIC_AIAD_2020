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
        int newX = (simGraphics.getCurWidth() / 5)*(origX) + simGraphics.getDisplayWidth() - (100*simGraphics.getCurWidth());
        int newY = (simGraphics.getCurHeight() / 5)*(origY) + simGraphics.getDisplayHeight() - (100*simGraphics.getCurHeight());
        textBox.setTextCoordinates(newX,newY);
        textBox.drawDisplay(simGraphics);
    }
}
