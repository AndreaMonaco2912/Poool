package sketch.impl.model.util;

public record Position(double x, double y) {
    public static Position origin() {
        return new Position(0, 0);
    }

    public Position sum(Vector toSum) {
        return new Position(x + toSum.x(), y + toSum.y());
    }
}