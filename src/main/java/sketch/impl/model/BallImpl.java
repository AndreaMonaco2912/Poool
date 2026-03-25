package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.BallMover;
import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

import java.util.Objects;

public class BallImpl implements Ball {
    private final double radius;
    private final double mass;
    private final BallMover ballMover;

    private Position position;
    private Vector speed;

    public BallImpl(double radius, double mass, BallMover ballMover) {
        this.radius = radius;
        this.mass = mass;
        this.ballMover = ballMover;
    }

    @Override
    public void updateState(long deltaTime) {
        double timeInSeconds = deltaTime * 0.001;
        applyMovement();
        applyFriction(timeInSeconds);
        this.position = this.position.sum(this.speed.mul(timeInSeconds));
    }

    private void applyMovement() {
        if (Objects.nonNull(ballMover.getNextMove())) {
            this.speed = ballMover.getNextMove();
        }
    }

    private void applyFriction(double timeInSeconds) {
        final double frictionFactor = 0.25;
        final double minimalNonZeroSpeed = 0.001;
        double speedModule = this.speed.getNorm();

        if (speedModule > minimalNonZeroSpeed) {
            double dec = frictionFactor * timeInSeconds;
            double factor = Math.max(0, speedModule - dec) / speedModule;
            this.speed = this.speed.mul(factor);
        } else {
            this.speed = Vector.zero();
        }
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public void setSpeed(Vector speed) {
        this.speed = speed;
    }

    @Override
    public double getPositionX() {
        checkPosition();
        return this.position.x();
    }

    @Override
    public double getPositionY() {
        checkPosition();
        return this.position.y();
    }

    private void checkPosition() {
        if (Objects.isNull(this.position)) {
            throw new IllegalStateException("Required ball position is unsetted");
        }
    }

    @Override
    public double getSpeedX() {
        checkSpeed();
        return this.speed.x();
    }

    @Override
    public double getSpeedY() {
        checkSpeed();
        return this.speed.y();
    }

    private void checkSpeed() {
        if (Objects.isNull(this.speed)) {
            throw new IllegalStateException("Required ball speed is unsetted");
        }
    }

    @Override
    public double getMass() {
        return this.mass;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }
}
