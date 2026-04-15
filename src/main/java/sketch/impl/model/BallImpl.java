package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.BallMover;
import sketch.api.model.HitBy;
import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

import java.util.Objects;

public class BallImpl implements Ball {

    private final double radius;
    private final double mass;
    private final BallMover ballMover;
    private HitBy ballHit;

    private Position position;
    private Vector speed;

    private final int id;

    private static final double STOP_SPEED = 0.05;

    public BallImpl(double radius, double mass, BallMover ballMover, int id) {
        this.radius = radius;
        this.mass = mass;
        this.ballMover = ballMover;
        this.ballHit = HitBy.UNKNOWN;
        this.id = id;
    }

    @Override
    public void updateState(long deltaTime) {
        double timeInSeconds = deltaTime * 0.001;
        applyMovement();
        applyFriction(timeInSeconds);
        this.position = this.position.sum(this.speed.mul(timeInSeconds));
    }

    private void applyMovement() {
        final Vector nextMove = ballMover.getNextMove();
        if (Objects.nonNull(nextMove)) {
            this.setSpeed(nextMove);
        }
    }

    private void applyFriction(double timeInSeconds) {
        final double frictionFactor = 0.25;
        final double minimalNonZeroSpeed = 0.001;
        double speedModule = this.speed.getNorm();

        if (speedModule > minimalNonZeroSpeed) {
            double dec = frictionFactor * timeInSeconds;
            double factor = Math.max(0, speedModule - dec) / speedModule;
            this.setSpeed(this.speed.mul(factor));
        } else {
            this.setSpeed(Vector.zero());
        }
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public void setSpeed(Vector speed) {
        this.speed = speed;
        checkAndSignalToMover();
    }

    @Override
    public void setHittingBall(HitBy ball) {
        this.ballHit = ball;
    }

    @Override
    public HitBy getHittingBall() {
        return this.ballHit;
    }

    @Override
    public double getPositionX() {
        return this.position.x();
    }

    @Override
    public double getPositionY() {
        return this.position.y();
    }

    @Override
    public double getSpeedX() {
        return this.speed.x();
    }

    @Override
    public double getSpeedY() {
        return this.speed.y();
    }

    @Override
    public double getMass() {
        return this.mass;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    @Override
    public int getId() {
        return this.id;
    }

    private void checkAndSignalToMover() {
        if (this.speed.getNorm() < STOP_SPEED) {
            this.ballMover.signalStop();
        } else {
            this.ballMover.signalMovement();
        }
    }
}
