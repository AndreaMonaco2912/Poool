package sketch.impl.model;

import sketch.api.model.*;
import sketch.impl.model.util.Boundary;
import sketch.impl.model.util.MyCyclicBarrier;
import sketch.impl.model.util.Points;

import java.util.*;

public class BoardManagerImpl implements BoardManager {

    private final BallManager ballManager;
    private final CollisionResolver collisionResolver;
    private final List<Worker> threads;
    private final MyCyclicBarrier updateBarrier;
    private Points newPoints = Points.zero();

    public BoardManagerImpl(BallManager ballManager, Boundary bounds) {
        this.ballManager = ballManager;
        this.collisionResolver = new CollisionResolverImpl(bounds);
        int cores = Runtime.getRuntime().availableProcessors();
        this.threads = new ArrayList<>(cores + 1);
        MyCyclicBarrier ballBarrier = new MyCyclicBarrier(cores + 1);
        this.updateBarrier = new MyCyclicBarrier(cores + 2);

        for (int i = 0; i <= cores; i++) {
            this.threads.add(new Worker(ballManager, ballBarrier, collisionResolver, updateBarrier));
        }
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    public GameStatus updateBoard(long deltaTime) {
        final List<Set<Ball>> smallBallsDivided = ballManager.splitSimpleBalls(threads.size());

        ballManager.playerBall().updateState(deltaTime);
        if (Objects.nonNull(ballManager.cpuBall())) {
            ballManager.cpuBall().updateState(deltaTime);
        }

        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).updateState(deltaTime, smallBallsDivided.get(i));
        }
        updateBarrier.await();

        //bounds collision for player and cpu.
        if (Objects.nonNull(ballManager.cpuBall())) {
            collisionResolver.collideWidth(
                    ballManager.playerBall(),
                    Set.of(ballManager.cpuBall()),
                    HitBy.UNKNOWN
            );
            collisionResolver.applyBoundsCollision(
                    Set.of(ballManager.playerBall(), ballManager.cpuBall()));
        } else {
            collisionResolver.applyBoundsCollision(
                    Set.of(ballManager.playerBall()));
        }
        return calculatePoints();
    }

    private GameStatus calculatePoints() {

        GameStatus output = GameStatus.GAME_CONTINUES;

        for (Ball hole : ballManager.holes()) {
            if (Objects.nonNull(ballManager.cpuBall()))
                if (collisionResolver.isInContact(ballManager.cpuBall(), hole)) output = GameStatus.PLAYER_WINS;
            if (collisionResolver.isInContact(ballManager.playerBall(), hole)) output = GameStatus.CPU_WINS;
        }

        if (output != GameStatus.GAME_CONTINUES) {
            for (Thread thread : threads) {
                thread.interrupt();
            }
            return output;
        }

        newPoints = Points.zero();

        for (Worker worker : threads) {
            newPoints = newPoints.add(worker.getPoints());
        }

        return output;
    }


    @Override
    public Points getNewPoints() {
        return this.newPoints;
    }

    private static class Worker extends Thread {

        private final BallManager ballManager;
        private final MyCyclicBarrier ballBarrier;
        private final CollisionResolver collisionResolver;
        private final MyCyclicBarrier updateBarrier;
        private Set<Ball> balls;
        private Long deltaTime = null;

        private int newPlayerPoints = 0;
        private int newCPUPoints = 0;

        public Worker(BallManager ballManager, MyCyclicBarrier ballBarrier, CollisionResolver collisionResolver, MyCyclicBarrier updateBarrier) {
            this.ballManager = ballManager;
            this.ballBarrier = ballBarrier;
            this.collisionResolver = collisionResolver;
            this.updateBarrier = updateBarrier;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                awaitWork();
                if (Thread.currentThread().isInterrupted()) break;
                long delta = deltaTime;
                moveBalls(delta);
                ballBarrier.await();
                collideBalls();
                collideWithCPU();
                collideWithPlayer();
                ballBarrier.await();
                applyBounds();
                ballBarrier.await();
                updatePoints();
                updateBarrier.await();
            }

        }

        private void moveBalls(long deltaTime) {
            for (Ball ball : balls) {
                ball.updateState(deltaTime);
            }
            this.deltaTime = null;
        }

        private void collideBalls() {
            for (Ball myBall : balls) {
                collisionResolver.collideWidth(myBall, ballManager.balls(), HitBy.UNKNOWN);
            }
        }

        private void collideWithCPU() {
            if (Objects.nonNull(ballManager.cpuBall()))
                collisionResolver.collideWidth(ballManager.cpuBall(), balls, HitBy.CPU);
        }

        private void collideWithPlayer() {
            collisionResolver.collideWidth(ballManager.playerBall(), balls, HitBy.PLAYER);
        }

        private void applyBounds() {
            collisionResolver.applyBoundsCollision(balls);
        }

        private void updatePoints() {
            newPlayerPoints = 0;
            newCPUPoints = 0;

            for (Ball hole : ballManager.holes()) {
                removeBalls(hole);
            }
        }

        private void removeBalls(Ball hole) {
            for (Ball ball : balls) {
                if (collisionResolver.isInContact(hole, ball)) {
                    switch (ball.getHittingBall()) {
                        case CPU -> newCPUPoints++;
                        case PLAYER -> newPlayerPoints++;
                    }
                    synchronized (ballManager.balls()) {
                        ballManager.balls().remove(ball);
                    }
                }
            }
        }

        private synchronized void awaitWork() {
            while (Objects.isNull(deltaTime)) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Thread killed game finished");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        public synchronized void updateState(long deltaTime, Set<Ball> balls) {
            this.deltaTime = deltaTime;
            this.balls = balls;
            notify();
        }

        public Points getPoints() {
            return new Points(newPlayerPoints, newCPUPoints);
        }
    }
}
