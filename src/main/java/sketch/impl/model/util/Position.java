package sketch.impl.model.util;

public record Position(double x, double y) {
    public Position sum(Vector toSum) {
        return new Position(x + toSum.x(), y + toSum.y());
    }
}