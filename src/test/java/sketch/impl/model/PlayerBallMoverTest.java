package sketch.impl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sketch.api.model.PlayerBallMover;
import sketch.impl.model.util.Vector;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerBallMoverTest {
    private PlayerBallMover playerBallMover;
    private final Vector nextMove = Vector.zero();

    @BeforeEach
    public void init() {
        playerBallMover = new PlayerBallMoverImpl();
        playerBallMover.addNextMove(this.nextMove.copy());
    }

    @Test
    public void shouldGetNextMove() {
        assertEquals(Vector.zero(), playerBallMover.getNextMove());
    }

    @Test
    public void shouldNotGetTwoMove() {
        playerBallMover.getNextMove();
        assertTrue(Objects.isNull(playerBallMover.getNextMove()));
    }

    @Test
    public void shouldNotGetNextMoveWhenMoving() {
        playerBallMover = new PlayerBallMoverImpl();
        playerBallMover.signalMovement();
        playerBallMover.addNextMove(this.nextMove.copy());
    }

    @Test
    public void shouldGetNextMoveWhenStopped() {
        playerBallMover.signalMovement();
        playerBallMover.signalStop();
        assertEquals(Vector.zero(), playerBallMover.getNextMove());
    }

    @Test
    public void shouldNotGetTwoMoveInASmallAmountOfTime() {
        long now = System.currentTimeMillis();
        playerBallMover.getNextMove();
        if (System.currentTimeMillis() - now < 1000) { // Should always enter this test and always take minus than a second
            playerBallMover.addNextMove(Vector.zero());
        } else {
            fail("Unable to produce a valid test due to performance");
        }
        try {
            Thread.sleep(2000);//2000 is the time before next move for game rules
        } catch (InterruptedException e) {
            fail("Thread interrupted, test uncompleted");
        }
        assertTrue(Objects.isNull(playerBallMover.getNextMove()));
    }

    @Test
    public void shouldGetTwoMoveInEnoughTime() {
        playerBallMover.getNextMove();
        try {
            Thread.sleep(2000);//2000 is the time before next move for game rules
        } catch (InterruptedException e) {
            fail("Thread interrupted, test uncompleted");
        }
        playerBallMover.addNextMove(Vector.zero());
        assertEquals(Vector.zero(), playerBallMover.getNextMove());
    }
}
