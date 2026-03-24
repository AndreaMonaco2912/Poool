package sketch01.impl.model.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionTest {
    static final double X = 4.0;
    static final int Y = 2;
    Position position;

    @BeforeEach
    void init() {
        this.position = new Position(X, Y);
    }

    @Test
    public void testVectorGet() {
        assertEquals(X, position.x());
        assertEquals(Y, position.y());
    }
}
