package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.BallFactory;
import sketch.api.model.BallMover;
import sketch.impl.model.util.Vector;

public class MinimalBallFactoryImpl implements BallFactory {

    @Override
    public Ball simpleBall() {
        final Ball simpleBall = new BallImpl(0.05, 0.75, new StillBallMover());
        simpleBall.setSpeed(Vector.zero());
        return simpleBall;
    }

    @Override
    public Ball movableBall(BallMover mover) {
        return new BallImpl(0.06, 0.75, mover);
    }
}
