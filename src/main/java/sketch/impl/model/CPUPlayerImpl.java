package sketch.impl.model;

import sketch.api.model.CPUPlayer;
import sketch.api.model.PlayerBallMover;
import sketch.impl.model.util.Vector;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CPUPlayerImpl implements CPUPlayer {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Random rand = new Random();
    private final PlayerBallMover cpuBallMover;

    public CPUPlayerImpl(PlayerBallMover cpuBallMover){
        this.cpuBallMover = cpuBallMover;
    }

    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            var angle = rand.nextDouble() * Math.PI * 0.25;
            var v = new Vector(Math.cos(angle), Math.sin(angle)).mul(1.5);
            cpuBallMover.addNextMove(v);
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void stop(){
        scheduler.shutdown();
    }
}
