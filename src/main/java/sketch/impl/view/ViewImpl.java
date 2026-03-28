package sketch.impl.view;

import sketch.api.view.ModelObserver;
import sketch.impl.view.util.BallViewInfo;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ViewImpl extends JFrame implements ModelObserver {

    private Set<BallViewInfo> balls;
    private final VisualiserPanel panel;

    public ViewImpl(int w, int h) {
        setTitle("Sketch");
        setSize(w, h + 25);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new VisualiserPanel(w, h);
        getContentPane().add(panel);
    }

    @Override
    public void update(Set<BallViewInfo> balls) {
        this.balls = Set.copyOf(balls);
        try {
            SwingUtilities.invokeAndWait(() ->
                    panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight())
            );
        } catch (InvocationTargetException | InterruptedException e) {
            System.err.println("Exception on creation of JPanel");
        }
    }

    private class VisualiserPanel extends JPanel {
        private final int ox;
        private final int oy;
        private final int delta;

        public VisualiserPanel(int w, int h) {
            setSize(w, h + 25);
            ox = w / 2;
            oy = h / 2;
            delta = Math.min(ox, oy);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(ox, 0, ox, oy * 2);
            g2.drawLine(0, oy, ox * 2, oy);

            g2.setColor(Color.BLACK);
            Set<BallViewInfo> currentBalls = balls;
            for (var b : currentBalls) {
                var p = b.position();
                int x0 = (int) (ox + p.x() * delta);
                int y0 = (int) (oy - p.y() * delta);
                int radius = (int) (b.radius() * delta);

                switch (b.ballType()) {
                    case PLAYER_BALL -> {
                        g2.setStroke(new BasicStroke(3));
                        g2.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
                    }
                    case CPU_BALL -> {
                        g2.setStroke(new BasicStroke(2));
                        g2.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
                    }
                    default -> {
                        g2.setStroke(new BasicStroke(1));
                        g2.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
                    }
                }
            }

            g2.setStroke(new BasicStroke(1));
            g2.drawString("Num balls: " + currentBalls.size(), 20, 40);
        }
    }
}
