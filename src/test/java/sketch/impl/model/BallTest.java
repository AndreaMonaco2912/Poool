package sketch.impl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sketch.api.model.Ball;
import sketch.api.model.BallMover;
import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BallTest {
    protected static final double RADIUS = 3.0;
    protected static final double MASS = 5.0;
    protected static final int POS_X = 0;
    protected static final int POS_Y = 0;
    protected static final int SPEED_X = 1;
    protected static final int SPEED_Y = 0;
    protected final Position position = new Position(POS_X, POS_Y);
    protected final Vector speed = new Vector(SPEED_X, SPEED_Y);
    protected Ball ball;

    @BeforeEach
    public void init() {
        this.ball = new BallImpl(RADIUS, MASS, getMover());
        this.ball.setPosition(this.position);
        this.ball.setSpeed(this.speed);
    }

    @Test
    public void shouldReturnMass() {
        assertEquals(MASS, this.ball.getMass());
    }

    @Test
    public void shouldReturnRadius() {
        assertEquals(RADIUS, this.ball.getRadius());
    }

//    @Test
//    public void getUnsettedDataShouldThrowException() {
//        this.ball = new BallImpl(RADIUS, MASS, null); // Change the ball to a new one
//        assertThrows(IllegalStateException.class, () -> ball.getPositionX());
//        assertThrows(IllegalStateException.class, () -> ball.getPositionY());
//        assertThrows(IllegalStateException.class, () -> ball.getSpeedX());
//        assertThrows(IllegalStateException.class, () -> ball.getSpeedY());
//    }

    @Test
    public void getPositionShouldReturnCorrectData() {
        assertEquals(POS_X, this.ball.getPositionX());
        assertEquals(POS_Y, this.ball.getPositionY());
    }

    @Test
    public void getSpeedShouldReturnCorrectData() {
        assertEquals(SPEED_X, this.ball.getSpeedX());
        assertEquals(SPEED_Y, this.ball.getSpeedY());
    }

    protected abstract BallMover getMover();
}
