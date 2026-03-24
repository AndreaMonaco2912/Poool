package sketch01.impl.model;

import sketch01.api.model.Ball;
import sketch01.impl.model.util.Position;
import sketch01.impl.model.util.Vector;

import java.util.Objects;

public class BallImpl implements Ball {
    private final double radius;
    private final double mass;
    private final Object ballMover;

    private Position position;
    private Vector speed;

    public BallImpl(double radius, double mass, Object o) {
        this.radius = radius;
        this.mass = mass;
        this.ballMover = o;
    }

    @Override
    public void updateState(long deltaTime) {
        if (Objects.nonNull(ballMover)) {
            IO.println("Hallo"); //TODO: apply movement
        }
        position = new Position(position.x() + speed.x(), position.y() + speed.y()); //TODO: fix formula
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
