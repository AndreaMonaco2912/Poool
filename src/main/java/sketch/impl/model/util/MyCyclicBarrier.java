package sketch.impl.model.util;

public class MyCyclicBarrier {
    private final int parties;
    private int count = 0;
    private int generation = 0;

    public MyCyclicBarrier(int parties) {
        this.parties = parties;
    }

    public synchronized void await() {
        int myGeneration = generation;
        count++;
        if (count < parties) {
            while (generation == myGeneration) { // protection against spurious wakeup
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } else {
            count = 0;
            generation++;
            notifyAll();
        }
    }
}
