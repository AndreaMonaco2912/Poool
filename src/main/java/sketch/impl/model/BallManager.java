package sketch.impl.model;

import sketch.api.model.Ball;

import java.util.ArrayList;
import java.util.List;

public record BallManager(Ball playerBall, Ball cpuBall, List<Ball> balls, List<Ball> holes) {

    public List<List<Ball>> splitSimpleBalls(int n) {
        List<Ball> list = new ArrayList<>(balls);
        List<List<Ball>> result = new ArrayList<>();
        int size = list.size();
        int baseSize = size / n;
        int extra = size % n;

        int index = 0;
        for (int i = 0; i < n; i++) {
            int count = baseSize + (i < extra ? 1 : 0);
            result.add(new ArrayList<>(list.subList(index, index + count)));
            index += count;
        }
        return result;
    }
}
