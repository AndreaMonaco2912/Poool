package sketch.api.model;

public interface BallFactory {

    /**
     * Creates a simple Ball
     *
     * @return the default ball
     */
    Ball simpleBall();

    /**
     * Creates a ball that can be Used by player and cpu
     *
     * @param mover: movement of the ball
     * @return the ball
     */
    Ball movableBall(BallMover mover);
}
