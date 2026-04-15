package sketch.api.view;

import sketch.api.model.Ball;
import sketch.impl.model.util.Points;
import sketch.impl.view.util.BallViewInfo;

import java.util.List;

/**
 * Extracts model information to draw the view
 */
public interface ViewModel {

    /**
     * Update position of Balls
     *
     * @param normalBalls Ball with normal design
     * @param playerBall  Ball used by the player
     * @param cpuBall     Ball used by the cpu
     */
    void update(List<Ball> normalBalls, Ball playerBall, Ball cpuBall);

    /**
     * gives to the view the information about the balls and holes
     *
     * @return all the balls
     */
    List<BallViewInfo> getBalls();

    /**
     * add a new object to update on new frame
     *
     * @param observer to be updated
     */
    void addObserver(ModelObserver observer);

    /**
     * sets Position of every Hole
     *
     * @param holes of the game
     */
    void setHoles(List<Ball> holes);

    /**
     * sets the points
     *
     * @param points updated
     */
    void updatePoints(Points points);

    /**
     * gives the points
     *
     * @return the real time points
     */
    Points getPoints();
}
