package sketch.impl.model;

import sketch.api.model.Ball;

import java.util.ArrayList;
import java.util.List;

public record BallManager(Ball playerBall, Ball cpuBall, List<Ball> balls, List<Ball> holes) {

    public List<Ball> allBalls() {
        final List<Ball> allBalls = new ArrayList<>(balls);
        allBalls.add(playerBall);
        if (cpuBall != null) {
            allBalls.add(cpuBall);
        }
        return allBalls;
    }
}
