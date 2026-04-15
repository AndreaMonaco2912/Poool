package sketch.api.model;

import sketch.impl.model.util.Boundary;

import java.util.List;

/**
 * Produce BoardConfigurations for the game
 */
public interface BoardConfiguration {

    /**
     * @return the bounds of the board in the configuration
     */
    Boundary getBoardBoundary();

    /**
     * @param ballMover for the playerBall
     * @return the player Ball
     */
    Ball getPlayerBall(BallMover ballMover);

    /**
     * @param ballMover for the CPUControlledBall
     * @return CPU ball
     */
    Ball getCpuBall(BallMover ballMover);

    /**
     * @return all the Still Ball
     */
    List<Ball> getSmallBalls();

    /**
     * @return all the holes
     */
    List<Ball> getHoles();
}
