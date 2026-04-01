package sketch.impl.model;

import sketch.api.model.PlayerBallMover;
import sketch.impl.model.util.Vector;

import java.util.Objects;

public class CPUBallMoverImpl implements PlayerBallMover {
    private boolean isMoving;
    private Vector nextMove;

    public CPUBallMoverImpl() {
        isMoving = false;
        nextMove = null;
    }

    @Override
    public synchronized void addNextMove(Vector nextMove) {
        while (isMoving){
            simpleWait();
        }
        isMoving = true;
        this.nextMove = nextMove;
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