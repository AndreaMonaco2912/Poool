package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.BallFactory;
import sketch.api.model.BallMover;
import sketch.api.model.BoardConfiguration;
import sketch.impl.model.util.Boundary;
import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MinimalBoardConfiguration implements BoardConfiguration {
    private final BallFactory ballFactory = new MinimalBallFactory();

    @Override
    public Boundary getBoardBoundary() {
        return new Boundary(-1.5, -1.0, 1.5, 1.0);
    }

    @Override
    public Ball getPlayerBall(BallMover ballMover) {
        final Ball playerBall = ballFactory.movableBall(ballMover);
        playerBall.setSpeed(new Vector(0, 0.5));
        playerBall.setPosition(Position.origin());
        return playerBall;
    }

    @Override
    public Ball getCpuBall(BallMover ballMover) {
        final Ball cpuBall = ballFactory.movableBall(ballMover);
        cpuBall.setSpeed(Vector.zero());
        cpuBall.setPosition(new Position(-0.5, 0));
        return cpuBall;
    }

    @Override
    public List<Ball> getSmallBalls() {
        final Ball a = ballFactory.simpleBall();
        final Ball b = ballFactory.simpleBall();
        a.setPosition(new Position(0, 0.5));
        b.setPosition(new Position(0.05, 0.55));
        final List<Ball> output = new ArrayList<>(2);
        output.add(a);
        output.add(b);
        return output;
    }

    @Override
    public List<Ball> getHoles() {
        final Ball firstHole = ballFactory.simpleHole(new Position(-1.5, 1.0));
        final Ball secondHole = ballFactory.simpleHole(new Position(1.5, 1.0));
        final List<Ball> output = new ArrayList<>(2);
        output.add(firstHole);
        output.add(secondHole);
        return output;
    }

    private static class MinimalBallFactory implements BallFactory {

        int id = 0;

        @Override
        public Ball simpleBall() {
            final Ball simpleBall = new BallImpl(0.05, 0.75, new StillBallMover(), id++);
            simpleBall.setSpeed(Vector.zero());
            return simpleBall;
        }

        @Override
        public Ball movableBall(BallMover mover) {
            return new BallImpl(0.06, 0.75, mover, id++);
        }

        @Override
        public Ball simpleHole(Position position) {
            Ball hole = new BallImpl(0.2, 100, new StillBallMover(), id++);
            hole.setSpeed(Vector.zero());
            hole.setPosition(position);
            return hole;
        }
    }
}
