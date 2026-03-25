package sketch.impl.model.util;

public record Vector(double x, double y) {
    public double getNorm() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector mul(double scaleFactor) {
        return new Vector(x * scaleFactor, y * scaleFactor);
    }
}
