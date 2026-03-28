package sketch.api.model;

import sketch.impl.model.util.Boundary;

import java.util.Set;

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
    Set<Ball> getSmallBalls();
}
