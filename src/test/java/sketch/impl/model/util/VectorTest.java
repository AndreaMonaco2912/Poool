package sketch.impl.model.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VectorTest {
    private static final double X = 4.0;
    private static final int Y = 3;
    private Vector vector;

    @BeforeEach
    public void init() {
        this.vector = new Vector(X, Y);
    }

    @Test
    public void testVectorGet() {
        assertEquals(X, vector.x());
        assertEquals(Y, vector.y());
    }

    @Test
    public void testNorm() {
        final int expectedNorm = 5; // euclidean norm in 2 dimension
        assertEquals(expectedNorm, this.vector.getNorm());
    }

    @Test
    public void testScale() {
        final double scaleFactor = 0.5;
        assertEquals(new Vector(X * scaleFactor, Y * scaleFactor), this.vector.mul(scaleFactor));
    }

    @Test
    public void testZeroVector() {
        assertEquals(new Vector(0, 0), Vector.zero());
    }
}
