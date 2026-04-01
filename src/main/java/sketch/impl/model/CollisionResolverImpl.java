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
    private final TwoBallCollisionMonitor collisionMonitor;

    public CollisionResolverImpl(Boundary bounds) {
        this.bounds = bounds;
        this.collisionMonitor = new TwoBallCollisionMonitor();
    }

    @Override
    public void collideBalls(Set<Ball> balls) {
        for (Ball firstBall : balls) {
            for (Ball secondBall : balls) {
                resolveCollision(firstBall, secondBall, HitBy.UNKNOWN, true);
            }
        }
    }

    @Override
    public void applyBoundsCollision(Set<Ball> balls) {
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

        return centerDistance < contactDistance;
    }

    @Override
    public void collideWidth(Ball a, Set<Ball> others, HitBy hitBall, boolean permitLostUpdate) {
        for (Ball b : others) {
            resolveCollision(a, b, hitBall, permitLostUpdate);
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
    private void resolveCollision(Ball ballA, Ball ballB, HitBy hitBall, boolean permitLostUpdate) {
        if (ballA == ballB) return;

        if (permitLostUpdate) {
            doResolve(ballA, ballB, hitBall);
        } else {


        /*
        synchronized(a) ... synchronized(b) -> philosophers deadlock

        if you put an order is not that good because (assuming ball have an order):
        synchronized(a) -> a is taken
            block to get b
            nobody can use a until b is released, thread holding b is holding potentially (assuming c < a < b) also thread wanting (c a).

         implemented solution resolves both
         */
            this.collisionMonitor.acquirePair(ballA, ballB);
            doResolve(ballA, ballB, hitBall);
            this.collisionMonitor.releasePair(ballA, ballB);
        }
    }

    //TODO remove all comments
    private void doResolve(Ball ballA, Ball ballB, HitBy hitBall) {
        final double distanceX = ballB.getPositionX() - ballA.getPositionX();
        // LOST UPDATE: tra questa lettura e la precedente, un altro thread può aver modificato ballA o ballB
        final double distanceY = ballB.getPositionY() - ballA.getPositionY();
        final double centerDistance = Math.hypot(distanceX, distanceY);
        final double contactDistance = ballA.getRadius() + ballB.getRadius();

        if (!isInContact(ballA, ballB) || centerDistance <= 1e-6) return;
        // LOST UPDATE: isInContact rilegge le posizioni, che potrebbero essere diverse da quelle usate sopra

        // NO LOST UPDATE: un altro thread può sovrascrivere hitBall di ballA subito dopo, ma metterà sempre hitBall.Unknown
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
        // LOST UPDATE: getPositionX/Y potrebbe restituire valori già modificati da un altro thread,
        // e la posizione scritta sovrascrive quella che un altro thread ha appena impostato
        ballB.setPosition(new Position(ballB.getPositionX() + normalX * separationB,
                ballB.getPositionY() + normalY * separationB));
        // LOST UPDATE: stesso problema per ballB

        final double relativeNormalSpeed = (ballB.getSpeedX() - ballA.getSpeedX()) * normalX
                + (ballB.getSpeedY() - ballA.getSpeedY()) * normalY;
        // LOST UPDATE: le speed lette qui potrebbero essere state modificate da un altro thread dopo il setPosition
        if (relativeNormalSpeed > 0) return;

        final double restitution = 1.0;
        final double impulse = -(1 + restitution) * relativeNormalSpeed
                / (1.0 / ballA.getMass() + 1.0 / ballB.getMass());

        final double impulseX = impulse * normalX;
        final double impulseY = impulse * normalY;
        ballA.setSpeed(new Vector(ballA.getSpeedX() - impulseX / ballA.getMass(),
                ballA.getSpeedY() - impulseY / ballA.getMass()));
        // LOST UPDATE: getSpeedX/Y potrebbe restituire valori diversi da quelli usati per calcolare relativeNormalSpeed,
        // e la speed scritta sovrascrive quella che un altro thread ha appena impostato
        ballB.setSpeed(new Vector(ballB.getSpeedX() + impulseX / ballB.getMass(),
                ballB.getSpeedY() + impulseY / ballB.getMass()));
        // LOST UPDATE: stesso problema per ballB
    }
}