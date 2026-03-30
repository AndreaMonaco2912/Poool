package sketch.impl.model;

import sketch.api.model.Ball;

import java.util.HashSet;
import java.util.Set;

public record BallManager(Ball playerBall, Ball cpuBall, Set<Ball> balls, Set<Ball> holes) {

    public Set<Ball> allBalls() {
        final Set<Ball> allBalls = new HashSet<>(balls);
        allBalls.add(playerBall);
        if (cpuBall != null) {
            allBalls.add(cpuBall);
        }
        return allBalls;
    }
}
