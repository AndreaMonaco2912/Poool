package sketch.impl.model;

import sketch.api.model.Ball;
import sketch.api.model.BallFactory;
import sketch.api.model.BallMover;

public class BallFactoryImpl implements BallFactory {

    @Override
    public Ball simpleBall() {
        return new BallImpl(0.05, 1, new StillBallMover());
    }

    @Override
    public Ball movableBall(BallMover mover) {
        return new BallImpl(0.06, 0.75, mover);
    }

    //TODO: maybe needed to add more configurations
}
