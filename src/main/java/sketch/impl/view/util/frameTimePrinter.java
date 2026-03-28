package sketch.impl.view.util;

import java.util.Arrays;

public class frameTimePrinter {
    private static final int BUFFER_SIZE = 128;

    private final long[] frameTimes = new long[BUFFER_SIZE];
    private int frameCount = 0;
    private long lastFrameTimestamp = System.currentTimeMillis();

    public void recordFrameTime() {
        long now = System.currentTimeMillis();
        frameTimes[frameCount % BUFFER_SIZE] = now - lastFrameTimestamp;
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
        double averageMs = (double) sum / BUFFER_SIZE;

        long[] sorted = frameTimes.clone();
        Arrays.sort(sorted);
        long p99Ms = sorted[(int) (BUFFER_SIZE * 0.99)];

        System.out.println("avg: " + String.format("%.1f", averageMs)
                + " ms | p99: " + p99Ms + " ms");
    }
}
