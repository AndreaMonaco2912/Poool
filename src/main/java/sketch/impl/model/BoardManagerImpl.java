package sketch.impl.model;

import sketch.api.model.*;
import sketch.impl.model.util.Boundary;
import sketch.impl.model.util.Points;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class BoardManagerImpl implements BoardManager {

    private final BallManager ballManager;
    private final CollisionResolver collisionResolver;
    private final Executor executor;

    private final AtomicInteger newPlayerPoints = new AtomicInteger(0);
    private final AtomicInteger newCPUPoints = new AtomicInteger(0);
    private List<List<Ball>> dividedBalls;

    public BoardManagerImpl(BallManager ballManager, Boundary bounds) {
        this.ballManager = ballManager;
        int cores = Runtime.getRuntime().availableProcessors();
        int threadPoolSize = cores + 1;
        executor = Executors.newFixedThreadPool(threadPoolSize);
        this.collisionResolver = new CollisionResolverImpl(bounds);
    }

    @Override
    public GameStatus updateBoard(long deltaTime) {
        dividedBalls = ballManager.splitSimpleBalls(ballManager.balls().size());

        moveBalls(deltaTime);
        collideAllBalls();
        applyBounds();

        newPlayerPoints.set(0);
        newCPUPoints.set(0);

        for (Ball hole : ballManager.holes()) {
            if (Objects.nonNull(ballManager.cpuBall()))
                if (collisionResolver.isInContact(ballManager.cpuBall(), hole)) return GameStatus.PLAYER_WINS;
            if (collisionResolver.isInContact(ballManager.playerBall(), hole)) return GameStatus.CPU_WINS;
            removeBalls(hole);
        }

        return GameStatus.GAME_CONTINUES;
    }

    private void collideAllBalls() {
        final CountDownLatch latch = new CountDownLatch(ballManager.balls().size());

        collideSimpleBalls(latch);
        manageCPUCollision();
        managePlayerCollision();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void collideSimpleBalls(CountDownLatch latch) {
        for (List<Ball> ballList : dividedBalls) {
            for (Ball ball : ballList) {
                executor.execute(() -> {
                    collisionResolver.collideWith(ball, ballManager.balls(), HitBy.UNKNOWN);
                    latch.countDown();
                });
            }
        }
    }

    private void applyBounds() {
        final CountDownLatch latch = new CountDownLatch(dividedBalls.size());
        for (List<Ball> ballList : dividedBalls) {
            executor.execute(() -> {
                collisionResolver.applyBoundsCollision(ballList);
                latch.countDown();
            });
        }


        List<Ball> balls = new ArrayList<>();
        balls.add(ballManager.playerBall());
        if (Objects.nonNull(ballManager.cpuBall())) {
            balls.add(ballManager.cpuBall());
        }
        collisionResolver.applyBoundsCollision(balls);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void managePlayerCollision() {
        collisionResolver.collideWith(ballManager.playerBall(), ballManager.balls(), HitBy.PLAYER);
    }

    private void manageCPUCollision() {
        if (Objects.nonNull(ballManager.cpuBall())) {
            collisionResolver.collideBalls(List.of(ballManager.cpuBall(), ballManager.playerBall()));
            collisionResolver.collideWith(ballManager.cpuBall(), ballManager.balls(), HitBy.CPU);
        }
    }

    private void moveBalls(long deltaTime) {
        CountDownLatch latch = new CountDownLatch(ballManager.balls().size());

        for (List<Ball> ballGroup : dividedBalls) {
            executor.execute(() -> {
                for (Ball ball : ballGroup) {
                    ball.updateState(deltaTime);
                    latch.countDown();
                }
            });
        }

        if (Objects.nonNull(ballManager.cpuBall())) ballManager.cpuBall().updateState(deltaTime);
        ballManager.playerBall().updateState(deltaTime);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Points getNewPoints() {
        return new Points(newPlayerPoints.get(), newCPUPoints.get());
    }

    private void removeBalls(Ball hole) {
        CountDownLatch latch = new CountDownLatch(dividedBalls.size());

        for (List<Ball> ballList : dividedBalls) {
            executor.execute(() -> {
                for (Ball ball : ballList) {
                    if (collisionResolver.isInContact(hole, ball)) {
                        switch (ball.getHittingBall()) {
                            case CPU -> newCPUPoints.incrementAndGet();
                            case PLAYER -> newPlayerPoints.incrementAndGet();
                        }
                        synchronized (ballManager.balls()) {
                            ballManager.balls().remove(ball);
                        }
                    }
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
