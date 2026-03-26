package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.CollisionResolver;
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

        for(Ball firstBall: balls){
            for(Ball secondBall: balls){
                resolveCollision(firstBall, secondBall);
            }
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

    private void resolveCollision(Ball firstBall, Ball secondBall) {
        if(firstBall == secondBall){
            return;
        }

        final int restitutionFactor = 1;

        double dx   = secondBall.getPositionX() - firstBall.getPositionX();
        double dy   = secondBall.getPositionY() - firstBall.getPositionY();
        double dist = Math.hypot(dx, dy);
        double minD = firstBall.getRadius() + secondBall.getRadius();

        if (dist < minD && dist > 1e-6)  {

            double nx = dx / dist;
            double ny = dy / dist;

            double overlap = minD - dist;
            double totalM  = firstBall.getMass() + secondBall.getMass();

            double a_factor = overlap * (secondBall.getMass() / totalM);
            double a_deltax = nx * a_factor;
            double a_deltay = ny * a_factor;

            firstBall.pos = new P2d(firstBall.getPositionX() - a_deltax, firstBall.getPos().y() - a_deltay);

            double b_factor = overlap * (firstBall.getMass() / totalM);
            double b_deltax = nx * b_factor;
            double b_deltay = ny * b_factor;

            secondBall.pos = new P2d(secondBall.getPositionX() + b_deltax, secondBall.getPos().y() + b_deltay);

            /* Update velocities  */

            /* relative speed along the normal vector*/

            double dvx = secondBall.vel.x() - firstBall.vel.x();
            double dvy = secondBall.getSpeedY() - firstBall.getSpeedY();
            double dvn = dvx * nx + dvy * ny;

            if (dvn <= 0) {

                double imp = -(1 + restitutionFactor) * dvn / (1.0/firstBall.getMass() + 1.0/secondBall.getMass());
                firstBall.vel = new V2d(firstBall.getSpeedX() - (imp / firstBall.getMass()) * nx, firstBall.getSpeedY() - (imp / firstBall.getMass()) * ny);
                secondBall.vel = new V2d(secondBall.getSpeedX() + (imp / secondBall.getMass()) * nx, secondBall.getSpeedY() + (imp / secondBall.getMass()) * ny);
            }
        }
    }
}
