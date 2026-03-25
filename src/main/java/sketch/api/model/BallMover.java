package sketch.api.model;

import sketch.impl.model.util.Vector;

public interface BallMover {

    /**
     * Gives the next speed vector that the ball must follow
     *
     * @return the next vector to follow or null if it doesn't need to change
     */
    Vector getNextMove();

    /**
     * Tells the mover that the ball is moving.
     */
    void signalMovement();

    /**
     * Tells the mover that the ball has stopped.
     */
    void signalStop();
}
