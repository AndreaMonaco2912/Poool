package sketch01.impl.model.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VectorTest {
    static final double X = 4.0;
    static final int Y = 2;
    Vector vector;

    @BeforeEach
    void init() {
        this.vector = new Vector(X, Y);
    }

    @Test
    public void testVectorGet() {
        assertEquals(X, vector.x());
        assertEquals(Y, vector.y());
    }
}
