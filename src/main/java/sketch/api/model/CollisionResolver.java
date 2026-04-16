package sketch.api.model;

import java.util.List;

/**
 * Collision resolver solves collision between balls and boundary modifying ball position and movement vectors
 */
public interface CollisionResolver {

    /**
     * Change Ball position solving every collision
     *
     * @param balls to be updated
     */
    void collideBalls(List<Ball> balls);

    /**
     * Apply collision with the bounds
     *
     * @param balls to collide
     */
    void applyBoundsCollision(List<Ball> balls);

    /**
     * Tels whether 2 ball are colliding or not
     *
     * @param a first ball
     * @param b second ball
     * @return true if collision happens
     */
    boolean isInContact(Ball a, Ball b);

    /**
     * test collision for others Ball width ball a
     *
     * @param a                ball to be tested
     * @param others           balls
     * @param hitBall          type of ball a
     */
    void collideWith(Ball a, List<Ball> others, HitBy hitBall);

    /**
     * resolves collisions between 2 balls
     *
     * @param ballA first Ball
     * @param ballB second Ball
     * @param hitBall type of hit
     */
    void resolveCollision(Ball ballA, Ball ballB, HitBy hitBall);
}