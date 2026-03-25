package sketch.impl.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import sketch.api.model.BallMover;
import sketch.impl.model.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StillBallTest extends BallTest {

    @ParameterizedTest
    @CsvSource({
            // vx0,    vy0, deltaTime, expVx,  expVy, expPx, expPy
            "1.0,      0.0, 1000,      0.75,   0.0,   0.75,  0.0",    // partial friction
            "1.0,      0.0, 4000,      0.0,    0.0,   0.0,   0.0",    // should clamp to 0
            "0.0005,   0.0, 1000,      0.0,    0.0,   0.0,   0.0"     // should immediately be 0
    })
    public void shouldUpdateState(double vx0, double vy0, long deltaTime,
                                  double expVx, double expVy, double expPx, double expPy) {
        ball.setSpeed(new Vector(vx0, vy0));
        ball.updateState(deltaTime);
        assertEquals(expVx, ball.getSpeedX());
        assertEquals(expVy, ball.getSpeedY());
        assertEquals(expPx, ball.getPositionX());
        assertEquals(expPy, ball.getPositionY());
    }

    @Override
    protected BallMover getMover() {
        return new StillBallMover();
    }
}
