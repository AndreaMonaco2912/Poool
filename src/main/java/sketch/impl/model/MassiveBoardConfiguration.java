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

public class MassiveBoardConfiguration implements BoardConfiguration {

    BallFactory ballFactory = new MassiveBallFactory();

    @Override
    public Boundary getBoardBoundary() {
        return new Boundary(-1.5, -1.0, 1.5, 1.0);
    }

    @Override
    public Ball getPlayerBall(BallMover ballMover) {
        final Ball playerBall = ballFactory.movableBall(ballMover);
        playerBall.setSpeed(Vector.zero());
        playerBall.setPosition(new Position(0, -0.75));
        return playerBall;
    }

    @Override
    public Ball getCpuBall(BallMover ballMover) {
        return null;
    }

    @Override
    public List<Ball> getSmallBalls() {
        List<Ball> balls = new ArrayList<>();
        var b = ballFactory.simpleBall();
        b.setPosition(new Position(0, -0.5));
        balls.add(b);

        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 150; col++) {
                var px = -1.0 + col * 0.015;
                var py = row * 0.015;
                b = ballFactory.simpleBall();
                b.setPosition(new Position(px, py));
                balls.add(b);
            }
        }
        return balls;
    }

    @Override
    public List<Ball> getHoles() {
        final Ball firstHole = ballFactory.simpleHole(new Position(-1.5, 1.0));
        final Ball secondHole = ballFactory.simpleHole(new Position(1.5, 1.0));
        final Ball thirdHole = ballFactory.simpleHole(Position.origin());
        final List<Ball> output = new ArrayList<>(2);
        output.add(firstHole);
        output.add(secondHole);
        output.add(thirdHole);
        return output;
    }

    private static class MassiveBallFactory implements BallFactory {

        int id = 0;

        @Override
        public Ball simpleBall() {
            final Ball simpleBall = new BallImpl(0.01, 0.25, new StillBallMover(), id++);
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
