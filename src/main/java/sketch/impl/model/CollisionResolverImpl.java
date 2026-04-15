package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.CollisionResolver;
import sketch.api.model.HitBy;
import sketch.impl.model.util.Boundary;
import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

import java.util.List;

public class CollisionResolverImpl implements CollisionResolver {
    private final Boundary bounds;
    private final TwoBallCollisionMonitor collisionMonitor;

    public CollisionResolverImpl(Boundary bounds) {
        this.bounds = bounds;
        this.collisionMonitor = new TwoBallCollisionMonitor();
    }

    @Override
    public void applyBoundsCollision(List<Ball> balls) {
        for (Ball ball : balls) {
            applyBoundaryConstraints(ball);
        }
    }

    @Override
    public boolean isInContact(Ball ballA, Ball ballB) {
        final double distanceX = ballB.getPositionX() - ballA.getPositionX();
        final double distanceY = ballB.getPositionY() - ballA.getPositionY();
        final double centerDistance = Math.hypot(distanceX, distanceY);
        final double contactDistance = ballA.getRadius() + ballB.getRadius();

        return centerDistance < contactDistance && !(centerDistance <= 1e-6);
    }

    @Override
    public void collideWith(Ball a, List<Ball> others, HitBy hitBall) {
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

    /**
     * this method Should not be executed in parallel, but lost update problems
     * are considered minimals because they actually happens only in very specific situations
     * and are resolved in next frames
     */
    private void resolveCollision(Ball ballA, Ball ballB, HitBy hitBall) {
        if (ballA.getId() >= ballB.getId()) return;

        if (!(isInContact(ballA, ballB))) return;

        this.collisionMonitor.acquirePair(ballA, ballB);

        final double distanceX = ballB.getPositionX() - ballA.getPositionX();
        final double distanceY = ballB.getPositionY() - ballA.getPositionY();
        final double centerDistance = Math.hypot(distanceX, distanceY);
        final double contactDistance = ballA.getRadius() + ballB.getRadius();

        doResolve(ballA, ballB, hitBall, distanceX, centerDistance, distanceY, contactDistance);
        this.collisionMonitor.releasePair(ballA, ballB);
    }

    private static void doResolve(Ball ballA, Ball ballB, HitBy hitBall, double distanceX, double centerDistance, double distanceY, double contactDistance) {
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
