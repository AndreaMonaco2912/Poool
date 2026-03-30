package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.CollisionResolver;
import sketch.api.model.HitBy;
import sketch.impl.model.util.Boundary;
import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

import java.util.Set;

public class CollisionResolverImpl implements CollisionResolver {
    private final Boundary bounds;

    public CollisionResolverImpl(Boundary bounds) {
        this.bounds = bounds;
    }

    @Override
    public void updateBalls(Set<Ball> balls) {
        for (Ball ball : balls) {
            applyBoundaryConstraints(ball);
        }

        for (Ball firstBall : balls) {
            for (Ball secondBall : balls) {
                resolveCollision(firstBall, secondBall, HitBy.UNKNOWN);
            }
        }
    }

    @Override
    public boolean isInContact(Ball ballA, Ball ballB) {
        final double distanceX = ballB.getPositionX() - ballA.getPositionX();
        final double distanceY = ballB.getPositionY() - ballA.getPositionY();
        final double centerDistance = Math.hypot(distanceX, distanceY);
        final double contactDistance = ballA.getRadius() + ballB.getRadius();

        return centerDistance < contactDistance;
    }

    @Override
    public void collideWidth(Ball a, Set<Ball> others, HitBy hitBall) {
        for (Ball b : others) {
            resolveCollision(a, b, hitBall);
        }
    }

    private void applyBoundaryConstraints(Ball ball) {
        if (ball.getPositionX() + ball.getRadius() > bounds.x1()) {
            ball.setPosition(new Position(bounds.x1() - ball.getRadius(), ball.getPositionY()));
            ball.setSpeed(getSwappedX(ball.getSpeedX(), ball.getSpeedY()));
        } else if (ball.getPositionX() - ball.getRadius() < bounds.x0()) {
            ball.setPosition(new Position(bounds.x0() + ball.getRadius(), ball.getPositionY()));
            ball.setSpeed(getSwappedX(ball.getSpeedX(), ball.getSpeedY()));
        } else if (ball.getPositionY() + ball.getRadius() > bounds.y1()) {
            ball.setPosition(new Position(ball.getPositionX(), bounds.y1() - ball.getRadius()));
            ball.setSpeed(getSwappedY(ball.getSpeedX(), ball.getSpeedY()));
        } else if (ball.getPositionY() - ball.getRadius() < bounds.y0()) {
            ball.setPosition(new Position(ball.getPositionX(), bounds.y0() + ball.getRadius()));
            ball.setSpeed(getSwappedY(ball.getSpeedX(), ball.getSpeedY()));
        }
    }

    public Vector getSwappedX(double x, double y) {
        return new Vector(-x, y);
    }

    public Vector getSwappedY(double x, double y) {
        return new Vector(x, -y);
    }

    private void resolveCollision(Ball ballA, Ball ballB, HitBy hitBall) {
        if (ballA == ballB) return;

        final double distanceX = ballB.getPositionX() - ballA.getPositionX();
        final double distanceY = ballB.getPositionY() - ballA.getPositionY();
        final double centerDistance = Math.hypot(distanceX, distanceY);
        final double contactDistance = ballA.getRadius() + ballB.getRadius();

        if (!isInContact(ballA, ballB) || centerDistance <= 1e-6) return;

        ballA.setHittingBall(hitBall);
        ballB.setHittingBall(hitBall);

        final double normalX = distanceX / centerDistance;
        final double normalY = distanceY / centerDistance;

        final double overlap = contactDistance - centerDistance;
        final double totalMass = ballA.getMass() + ballB.getMass();

        final double separationA = overlap * ballB.getMass() / totalMass;
        final double separationB = overlap * ballA.getMass() / totalMass;
        ballA.setPosition(new Position(ballA.getPositionX() - normalX * separationA,
                ballA.getPositionY() - normalY * separationA));
        ballB.setPosition(new Position(ballB.getPositionX() + normalX * separationB,
                ballB.getPositionY() + normalY * separationB));

        final double relativeNormalSpeed = (ballB.getSpeedX() - ballA.getSpeedX()) * normalX
                + (ballB.getSpeedY() - ballA.getSpeedY()) * normalY;
        if (relativeNormalSpeed > 0) return;

        final double restitution = 1.0;
        final double impulse = -(1 + restitution) * relativeNormalSpeed
                / (1.0 / ballA.getMass() + 1.0 / ballB.getMass());

        final double impulseX = impulse * normalX;
        final double impulseY = impulse * normalY;
        ballA.setSpeed(new Vector(ballA.getSpeedX() - impulseX / ballA.getMass(),
                ballA.getSpeedY() - impulseY / ballA.getMass()));
        ballB.setSpeed(new Vector(ballB.getSpeedX() + impulseX / ballB.getMass(),
                ballB.getSpeedY() + impulseY / ballB.getMass()));
    }
}
