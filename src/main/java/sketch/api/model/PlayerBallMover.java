package sketch.api.model;

import sketch.impl.model.util.Vector;

/**
 * This class move the ball for every player and cpu,
 * it receives and enqueue inputs if the ball can move for the game rules.
 */
public interface PlayerBallMover extends BallMover {

    /**
     * Add this move as the next if from the last move 2 seconds have elapsed
     * and the ball is stopped
     *
     * @param nextMove to be done, it can become null
     */
    void addNextMove(Vector nextMove);
}
