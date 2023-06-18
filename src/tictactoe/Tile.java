package tictactoe;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Tile extends JButton {
    private static final int RADIUS = 20;
    private static final Color ENABLED_COLOR = new Color(255, 200, 0);
    private static final Color DISABLED_COLOR = Color.GRAY;
    private static final int BORDER_THICKNESS = 3;
    private boolean enabled = true;
    int row, col;

    Tile(String label) {
        super(label);
        setFocusPainted(false);
        setFont(new Font("Arial", Font.BOLD, 30));
        setOpaque(false);
        setContentAreaFilled(false);
        setBorder();
        toggle(false);
    }

    Tile(int row, int col) {
        this(" ");
        this.row = row;
        this.col = col;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape background = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);

        if (enabled) {
            if (getModel().isPressed()) {
                graphics.setColor(ENABLED_COLOR.darker());
            } else if (getModel().isRollover()) {
                graphics.setColor(ENABLED_COLOR.brighter());
            } else {
                graphics.setColor(ENABLED_COLOR);
            }
        } else {
            graphics.setColor(DISABLED_COLOR);
        }
        graphics.fill(background);

        super.paintComponent(graphics);
        graphics.dispose();
    }

    private void setBorder() {
        setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D graphics = (Graphics2D) g.create();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setStroke(new BasicStroke(BORDER_THICKNESS));
                graphics.setColor(c.getForeground());
                graphics.drawRoundRect(x + BORDER_THICKNESS/2, y + BORDER_THICKNESS/2, width - BORDER_THICKNESS, height - BORDER_THICKNESS, RADIUS, RADIUS);
                graphics.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS);
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                insets.left = insets.right = insets.bottom = insets.top = BORDER_THICKNESS;
                return insets;
            }
        });
    }


    void toggle(boolean enable) {
        this.enabled = enable;
        Color bgColor = enable ? ENABLED_COLOR : DISABLED_COLOR;
        setBackground(bgColor);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JButton)
            return this.getText().equals(((JButton) obj).getText()) && !this.getText().isBlank();
        return false;
    }
}
