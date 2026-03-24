package sketch.impl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sketch.api.model.BallMover;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StillBallMoverTest {

    BallMover ballMover;

    @BeforeEach
    void init() {
        ballMover = new StillBallMover();
    }

    @Test
    void shouldReturnNullOnGet() {
        assertTrue(Objects.isNull(ballMover.getNextMove()));
    }
}
