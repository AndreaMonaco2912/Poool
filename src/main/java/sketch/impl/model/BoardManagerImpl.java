package sketch.impl.model;

import sketch.api.model.*;
import sketch.impl.model.util.Boundary;
import sketch.impl.model.util.Points;

import java.util.Objects;

public class BoardManagerImpl implements BoardManager {

    private final BallManager ballManager;
    private final CollisionResolver collisionResolver;
    private int newPlayerPoints;
    private int newCPUPoints;

    public BoardManagerImpl(BallManager ballManager, Boundary bounds) {
        this.ballManager = ballManager;
        this.collisionResolver = new CollisionResolverImpl(bounds);
    }

    @Override
    public GameStatus updateBoard(long deltaTime) {
        for (Ball ball : ballManager.allBalls()) {
            ball.updateState(deltaTime);
        }
        collisionResolver.collideBalls(ballManager.balls());
        if (Objects.nonNull(ballManager.cpuBall()))
            collisionResolver.collideWidth(ballManager.cpuBall(), ballManager.balls(), HitBy.CPU);
        collisionResolver.collideWidth(ballManager.playerBall(), ballManager.balls(), HitBy.PLAYER);
        collisionResolver.applyBoundsCollision(ballManager.allBalls());

        newPlayerPoints = 0;
        newCPUPoints = 0;

        for (Ball hole : ballManager.holes()) {
            if (Objects.nonNull(ballManager.cpuBall()))
                if (collisionResolver.isInContact(ballManager.cpuBall(), hole)) return GameStatus.PLAYER_WINS;
            if (collisionResolver.isInContact(ballManager.playerBall(), hole)) return GameStatus.CPU_WINS;
            removeBalls(hole);
        }

        return GameStatus.GAME_CONTINUES;
    }

    @Override
    public Points getNewPoints() {
        return new Points(newPlayerPoints, newCPUPoints);
    }

    private void removeBalls(Ball hole) {
        for (Ball simpleBall : ballManager.balls()) {
            if (collisionResolver.isInContact(simpleBall, hole)) {
                switch (simpleBall.getHittingBall()) {
                    case CPU -> newCPUPoints++;
                    case PLAYER -> newPlayerPoints++;
                }
                ballManager.balls().remove(simpleBall);
            }
        }
    }

}
