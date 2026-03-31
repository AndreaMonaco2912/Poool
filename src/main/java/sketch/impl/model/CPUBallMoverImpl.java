package sketch.impl.model;

import sketch.api.model.PlayerBallMover;
import sketch.impl.model.util.Vector;

import java.util.Objects;

public class CPUBallMoverImpl implements PlayerBallMover {
    private static final int MINIMUM_TIME_BEFORE_NEXT_MOVE = 2000;
    private boolean isMoving;
    private long lastMoveTime;
    private Vector nextMove;

    public CPUBallMoverImpl() {
        isMoving = false;
        lastMoveTime = System.currentTimeMillis() - MINIMUM_TIME_BEFORE_NEXT_MOVE;
        nextMove = null;
    }

    @Override
    public synchronized void addNextMove(Vector nextMove) {
        while (isMoving){
            simpleWait();
        }
        waitRemainingTime();
        isMoving = true;
        this.nextMove = nextMove;
        lastMoveTime = System.currentTimeMillis();
    }

    private void waitRemainingTime() {
        long remaining = MINIMUM_TIME_BEFORE_NEXT_MOVE
                - (System.currentTimeMillis() - lastMoveTime);
        while (remaining > 0) {
            timedWait(remaining);
            remaining = MINIMUM_TIME_BEFORE_NEXT_MOVE
                    - (System.currentTimeMillis() - lastMoveTime);
        }
    }

    private void timedWait(long remaining) {
        try {
            wait(remaining);
        } catch (InterruptedException e) {
            System.err.println("CPU died thread killed");
            Thread.currentThread().interrupt();
        }
    }

    private void simpleWait() {
        try {
            wait();
        } catch (InterruptedException e) {
            System.err.println("CPU died thread killed");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public synchronized Vector getNextMove() {
        Vector nextMove = Objects.isNull(this.nextMove) ? null : this.nextMove.copy();
        this.nextMove = null;
        return nextMove;
    }

    @Override
    public synchronized void signalMovement() {
        isMoving = true;
    }

    @Override
    public synchronized void signalStop() {
        isMoving = false;
        notify();
    }
}
