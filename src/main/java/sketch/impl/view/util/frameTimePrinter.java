package sketch.impl.view.util;

import java.util.Arrays;

public class frameTimePrinter {
    private static final int BUFFER_SIZE = 128;

    private final long[] frameTimes = new long[BUFFER_SIZE];
    private int frameCount = 0;
    private long lastFrameTime = 0;
    private long lastFrameTimestamp = System.currentTimeMillis();

    public void recordFrameTime() {
        long now = System.currentTimeMillis();
        this.lastFrameTime = now - lastFrameTimestamp;
        frameTimes[frameCount % BUFFER_SIZE] = this.lastFrameTime;
        lastFrameTimestamp = now;
        frameCount++;

        if (frameCount % BUFFER_SIZE == 0) {
            printStats();
        }
    }

    private void printStats() {
        long sum = 0;
        for (long ft : frameTimes) {
            sum += ft;
        }
        final double averageMs = (double) sum / BUFFER_SIZE;

        final long[] sorted = frameTimes.clone();
        Arrays.sort(sorted);
        final long p99Ms = sorted[(int) (BUFFER_SIZE * 0.99)];

        System.out.println("frameTime: " + this.lastFrameTime
                + " ms | avg: " + String.format("%.1f", averageMs)
                + " ms | p99: " + p99Ms + " ms");
    }
}
