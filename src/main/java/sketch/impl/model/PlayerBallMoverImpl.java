package sketch.impl.model;

import sketch.api.model.PlayerBallMover;
import sketch.impl.model.util.Vector;

import java.util.Objects;

public class PlayerBallMoverImpl implements PlayerBallMover {
    public static final int MINIMUM_TIME_BEFORE_NEXT_MOVE = 2000;
    private Vector nextMove;
    private boolean isMoving;
    private long lastMoveTime;

    public PlayerBallMoverImpl() {
        isMoving = false;
        lastMoveTime = System.currentTimeMillis() - MINIMUM_TIME_BEFORE_NEXT_MOVE;
        nextMove = null;
    }

    @Override
    public void addNextMove(Vector nextMove) {
        long currentTime = System.currentTimeMillis();
        if (!isMoving && currentTime - lastMoveTime >= MINIMUM_TIME_BEFORE_NEXT_MOVE) {
            this.nextMove = nextMove;
            lastMoveTime = currentTime;
        } else {
            this.nextMove = null;
        }
    }

    @Override
    public Vector getNextMove() {
        Vector nextMove = Objects.isNull(this.nextMove) ? null : this.nextMove.copy();
        this.nextMove = null;
        return nextMove;
    }

    @Override
    public void signalMovement() {
        isMoving = true;
    }

    @Override
    public void signalStop() {
        isMoving = false;
    }
}
