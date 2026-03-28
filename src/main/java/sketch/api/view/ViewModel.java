package sketch.api.view;

import sketch.api.model.Ball;
import sketch.impl.view.util.BallViewInfo;

import java.util.Set;

/**
 * Extracts model information to draw the view
 */
public interface ViewModel {

    /**
     * Update position of Balls
     *
     * @param normalBalls Ball with normal design
     * @param playerBall Ball used by the player
     * @param cpuBall Ball used by the cpu
     */
    void update(Set<Ball> normalBalls, Ball playerBall, Ball cpuBall);

    /**
     * gives to the view the information about the balls
     *
     * @return all the balls
     */
    Set<BallViewInfo> getBalls();

    void addObserver(ModelObserver observer);
}
