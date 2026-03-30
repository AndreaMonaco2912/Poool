package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.BallFactory;
import sketch.api.model.BallMover;
import sketch.api.model.BoardConfiguration;
import sketch.impl.model.util.Boundary;
import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

import java.util.HashSet;
import java.util.Set;

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
    public Set<Ball> getSmallBalls() {
        Set<Ball> balls = new HashSet<>();

        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 150; col++) {
                var px = -1.0 + col * 0.015;
                var py = row * 0.015;
                var b = ballFactory.simpleBall();
                b.setPosition(new Position(px, py));
                balls.add(b);
            }
        }
        return balls;
    }

    private static class MassiveBallFactory implements BallFactory {

        @Override
        public Ball simpleBall() {
            final Ball simpleBall = new BallImpl(0.01, 0.25, new StillBallMover());
            simpleBall.setSpeed(Vector.zero());
            return simpleBall;
        }

        @Override
        public Ball movableBall(BallMover mover) {
            return new BallImpl(0.06, 0.75, mover);
        }

        @Override
        public Ball simpleHole(javax.swing.text.Position position) {
            return null;
        }
    }
}
