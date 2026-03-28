package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.BoardManager;
import sketch.api.model.CollisionResolver;
import sketch.impl.model.util.Boundary;

import java.util.Set;

public class BoardManagerImpl implements BoardManager {

    private final Set<Ball> balls; // maybe can divide balls from playerBall to give e priority
    private final CollisionResolver collisionResolver;


    public BoardManagerImpl(Set<Ball> balls, Boundary bounds) {
        this.balls = balls;
        this.collisionResolver = new CollisionResolverImpl(bounds);
    }

    @Override
    public void updateBoard(long deltaTime) {
        for (Ball ball : this.balls) {
            ball.updateState(deltaTime);
        }
        this.collisionResolver.updateBalls(this.balls);
    }
}
