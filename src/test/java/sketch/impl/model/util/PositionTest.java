package sketch.impl.model.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionTest {
    private static final double X = 4.0;
    private static final int Y = 2;
    private Position position;

    @BeforeEach
    public void init() {
        this.position = new Position(X, Y);
    }

    @Test
    public void testVectorGet() {
        assertEquals(X, position.x());
        assertEquals(Y, position.y());
    }

    @Test
    public void testSum() {
        final int x = 1;
        final int y = 1;
        final Vector toSum = new Vector(x, y);
        assertEquals(new Position(X + x, Y + y), this.position.sum(toSum));
    }
}
