package sketch.impl.model;

import sketch.api.model.Ball;

import java.util.ArrayList;
import java.util.List;

public class TwoBallCollisionMonitor {
    private final List<Ball> lockedBalls = new ArrayList<>();

    public synchronized void acquirePair(Ball a, Ball b) {
        while (lockedBalls.contains(a) || lockedBalls.contains(b)) {
            try {
                wait();
            } catch (InterruptedException _) {
                throw new RuntimeException("Should not interrupt collisions");
            }
        }
        lockedBalls.add(a);
        lockedBalls.add(b);
    }

    public synchronized void releasePair(Ball a, Ball b) {
        lockedBalls.remove(a);
        lockedBalls.remove(b);
        notifyAll();
    }
}
