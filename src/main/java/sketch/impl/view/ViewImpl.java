package sketch.impl.view;

import sketch.api.controller.GameController;
import sketch.api.view.ModelObserver;
import sketch.api.view.ViewModel;
import sketch.impl.model.util.Points;
import sketch.impl.model.util.Vector;
import sketch.impl.view.util.BallViewInfo;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ViewImpl extends JFrame implements ModelObserver {

    private List<BallViewInfo> balls = java.util.List.of();
    private final VisualiserPanel panel;
    private final ViewModel model;
    private Points points = new Points(0, 0);

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
                    case java.awt.event.KeyEvent.VK_UP -> new Vector(0, speed);
                    case java.awt.event.KeyEvent.VK_DOWN -> new Vector(0, -speed);
                    case java.awt.event.KeyEvent.VK_LEFT -> new Vector(-speed, 0);
                    case java.awt.event.KeyEvent.VK_RIGHT -> new Vector(speed, 0);
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
        this.balls = model.getBalls();
        this.points = model.getPoints();/* synchronized is reentrant so it can be called here */
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

        private long time = System.currentTimeMillis(); //TODO just debug

        private static final BasicStroke DEFAULT_STROKE = new BasicStroke(1);
        private static final BasicStroke CPU_STROKE = new BasicStroke(2);
        private static final BasicStroke PLAYER_STROKE = new BasicStroke(3);

        public VisualiserPanel(int w, int h) {
            setSize(w, h + 25);
            ox = w / 2;
            oy = h / 2;
            delta = Math.min(ox, oy);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(DEFAULT_STROKE);
            g2.drawLine(ox, 0, ox, oy * 2);
            g2.drawLine(0, oy, ox * 2, oy);

            g2.setColor(Color.BLACK);

            List<BallViewInfo> currentBalls = balls;

            for (var b : currentBalls) {
                var p = b.position();
                int x0 = (int) (ox + p.x() * delta);
                int y0 = (int) (oy - p.y() * delta);
                int radius = (int) (b.radius() * delta);

                switch (b.ballType()) {
                    case PLAYER_BALL -> {
                        g2.setStroke(PLAYER_STROKE);
                        g2.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
                    }
                    case CPU_BALL -> {
                        g2.setStroke(CPU_STROKE);
                        g2.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
                    }
                    case HOLE -> g2.fillOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
                    default -> {
                        g2.setStroke(DEFAULT_STROKE);
                        g2.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
                    }
                }
            }

            g2.setStroke(DEFAULT_STROKE);
            g2.drawString("Num balls: " + currentBalls.size(), 20, 40);
            g2.drawString("Real frame time: "+(System.currentTimeMillis() - time), 20, 60);
            g2.drawString(points.toString(), 20, 80);

            time = System.currentTimeMillis();
        }
    }
}
