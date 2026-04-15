package sketch.impl.model;

import sketch.api.model.Ball;

import java.util.ArrayList;
import java.util.List;

public record BallManager(Ball playerBall, Ball cpuBall, List<Ball> balls, List<Ball> holes) {

    public List<List<Ball>> splitSimpleBalls(int n) {
        List<Ball> allBalls = new ArrayList<>(balls);
        List<List<Ball>> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < allBalls.size(); i++) {
            result.get(i % n).add(allBalls.get(i));
        }
        return result;
    }
}
