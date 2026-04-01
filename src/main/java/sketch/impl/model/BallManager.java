package sketch.impl.model;

import sketch.api.model.Ball;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record BallManager(Ball playerBall, Ball cpuBall, Set<Ball> balls, Set<Ball> holes) {

    //TODO removed unused method
    public Set<Ball> allBalls() {
        final Set<Ball> allBalls = new HashSet<>(balls);
        allBalls.add(playerBall);
        if (cpuBall != null) {
            allBalls.add(cpuBall);
        }
        return allBalls;
    }

    public List<Set<Ball>> splitSimpleBalls(int n) {
        List<Ball> list = new ArrayList<>(balls);
        List<Set<Ball>> result = new ArrayList<>();
        int size = list.size();
        int baseSize = size / n;
        int extra = size % n;

        int index = 0;
        for (int i = 0; i < n; i++) {
            int count = baseSize + (i < extra ? 1 : 0);
            result.add(new HashSet<>(list.subList(index, index + count)));
            index += count;
        }
        return result;
    }
}
