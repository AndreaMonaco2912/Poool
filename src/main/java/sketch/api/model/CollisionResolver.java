package sketch.api.model;

import java.util.Set;

/**
 * Collision resolver solves collision between balls and boundary modifying ball position and movement vectors
 */
public interface CollisionResolver {

    /**
     * Change Ball position solving every collision
     *
     * @param balls to be updated
     */
    void collideBalls(Set<Ball> balls);

    /**
     * Apply collision with the bounds
     *
     * @param balls to collide
     */
    void applyBoundsCollision(Set<Ball> balls);

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
     * @param permitLostUpdate tels if every part and update on the collision on both the balls are not so important
     */
    void collideWith(Ball a, Set<Ball> others, HitBy hitBall, boolean permitLostUpdate);
}