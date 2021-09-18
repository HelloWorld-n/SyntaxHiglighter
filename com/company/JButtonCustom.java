package com.company;

import javax.swing.*;
import java.awt.*;

class JButtonCustom extends JButton {

    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;

    public JButtonCustom() {
        this(null);
    }

    public JButtonCustom(String text) {
        super(text);
        super.setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed()) {
            g.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g.setColor(hoverBackgroundColor);
        } else {
            g.setColor(getBackground());
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    @Override
    public void setContentAreaFilled(boolean b) {
    }

    public Color getHoverBackground() {
        return hoverBackgroundColor;
    }

    public void setHoverBackground(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public Color getPressedBackground() {
        return pressedBackgroundColor;
    }

    public void setPressedBackground(Color pressedBackgroundColor) {
        this.pressedBackgroundColor = pressedBackgroundColor;
    }
}