package sketch.impl.view;

import sketch.api.controller.GameController;
import sketch.api.view.ModelObserver;
import sketch.api.view.ViewModel;
import sketch.impl.model.util.Vector;
import sketch.impl.view.util.BallViewInfo;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ViewImpl extends JFrame implements ModelObserver {

    private Set<BallViewInfo> balls = Set.of();
    private final VisualiserPanel panel;
    private final ViewModel model;

    public ViewImpl(int w, int h, ViewModel model, GameController controller) {
        this.model = model;
        setTitle("Sketch");
        setSize(w, h + 25);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new VisualiserPanel(w, h);
        getContentPane().add(panel);
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                double speed = 0.8;
                Vector dir = switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP    -> new Vector(0, speed);
                    case java.awt.event.KeyEvent.VK_DOWN   -> new Vector(0, -speed);
                    case java.awt.event.KeyEvent.VK_LEFT   -> new Vector(-speed, 0);
                    case java.awt.event.KeyEvent.VK_RIGHT  -> new Vector(speed, 0);
                    default -> null;
                };
                if (dir != null) {
                    controller.onPlayerInput(dir);
                }
            }
        });
        requestFocusInWindow();
        this.setVisible(true);
    }

    @Override
    public void update() {
        this.balls = Set.copyOf(model.getBalls()); /* synchronized is reentrant so it can be called here */
        try { /* this is not the best possible way for benchmark, deltaT is render + model calculations */
            SwingUtilities.invokeAndWait(() -> // can't call invoke and wait and inside model.getBalls -> deadLock, no deadlock on invoke later
                    panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight())
            );
        } catch (InvocationTargetException | InterruptedException e) {
            System.err.println("Exception on creation of JPanel in view");
        }
        /* SwingUtilities.invokeLater(panel::repaint); wouldn't work (model too fast) View should update independently width a timer */
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
                    case HOLE -> {
                        g2.setColor(Color.BLACK);
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
