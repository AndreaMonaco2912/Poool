package sketch.impl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sketch.api.model.Ball;
import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BallTest {
    static final double RADIUS = 3.0;
    static final double MASS = 5.0;
    static final int POS_X = 0;
    static final int POS_Y = 0;
    static final int SPEED_X = 1;
    static final int SPEED_Y = 0;
    final Position position = new Position(POS_X, POS_Y);
    final Vector speed = new Vector(SPEED_X, SPEED_Y);
    Ball ball;

    @BeforeEach
    void init() {
        this.ball = new BallImpl(RADIUS, MASS, null);
        this.ball.setPosition(this.position);
        this.ball.setSpeed(this.speed);
    }

    @Test
    void shouldReturnMass() {
        assertEquals(MASS, this.ball.getMass());
    }

    @Test
    void shouldReturnRadius() {
        assertEquals(RADIUS, this.ball.getRadius());
    }

    @Test
    void getUnsettedDataShouldThrowException() {
        this.ball = new BallImpl(RADIUS, MASS, null); // Change the ball to a new one
        assertThrows(IllegalStateException.class, () -> ball.getPositionX());
        assertThrows(IllegalStateException.class, () -> ball.getPositionY());
        assertThrows(IllegalStateException.class, () -> ball.getSpeedX());
        assertThrows(IllegalStateException.class, () -> ball.getSpeedY());
    }

    @Test
    void getPositionShouldReturnCorrectData() {
        assertEquals(POS_X, this.ball.getPositionX());
        assertEquals(POS_Y, this.ball.getPositionY());
    }

    @Test
    void getSpeedShouldReturnCorrectData() {
        assertEquals(SPEED_X, this.ball.getSpeedX());
        assertEquals(SPEED_Y, this.ball.getSpeedY());
    }

    @Test
    void shouldUpdateState(){ //TODO: fix formula
        final int deltaTime = 1000;
        this.ball.updateState(deltaTime);
        assertEquals(POS_X + SPEED_X, this.ball.getPositionX());
        assertEquals(POS_Y + SPEED_Y, this.ball.getPositionY());

    }
}
