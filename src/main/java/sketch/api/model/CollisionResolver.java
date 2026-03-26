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
    void updateBalls(Set<Ball> balls);
}
