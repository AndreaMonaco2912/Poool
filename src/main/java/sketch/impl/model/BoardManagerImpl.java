package sketch.impl.model;

import sketch.api.model.*;
import sketch.impl.model.util.Boundary;
import sketch.impl.model.util.Points;

import java.util.List;
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
        moveBalls(deltaTime);

        collisionResolver.collideBalls(ballManager.balls());
        manageCPUCollision();
        collisionResolver.collideWith(ballManager.playerBall(), ballManager.balls(), HitBy.PLAYER);
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

    private void manageCPUCollision() {
        if (Objects.nonNull(ballManager.cpuBall())){
            collisionResolver.collideBalls(List.of(ballManager.cpuBall(), ballManager.playerBall()));
            collisionResolver.collideWith(ballManager.cpuBall(), ballManager.balls(), HitBy.CPU);
        }
    }

    private void moveBalls(long deltaTime) {
        for (Ball ball : ballManager.allBalls()) {
            ball.updateState(deltaTime);
        }
    }

    @Override
    public Points getNewPoints() {
        return new Points(newPlayerPoints, newCPUPoints);
    }

    private void removeBalls(Ball hole) {
        var iterator = ballManager.balls().iterator();
        while (iterator.hasNext()) {
            Ball simpleBall = iterator.next();
            if (collisionResolver.isInContact(simpleBall, hole)) {
                switch (simpleBall.getHittingBall()) {
                    case CPU -> newCPUPoints++;
                    case PLAYER -> newPlayerPoints++;
                }
                iterator.remove();
            }
        }
    }

}
