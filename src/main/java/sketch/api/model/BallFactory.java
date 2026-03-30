package sketch.api.model;


import sketch.impl.model.util.Position;

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

    /**
     * Creates a Hole
     *
     * @param position where the Hole spawns
     * @return the Hole
     */
    Ball simpleHole(Position position);
}
