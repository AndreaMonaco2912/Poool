package sketch.impl.view;

import sketch.api.model.Ball;
import sketch.api.view.ModelObserver;
import sketch.api.view.ViewModel;
import sketch.api.view.util.BallType;
import sketch.impl.model.util.Position;
import sketch.impl.view.util.BallViewInfo;
import sketch.impl.view.util.frameTimePrinter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TimedViewModel implements ViewModel {

    private final boolean benchmark;
    private final frameTimePrinter printer;

    private final Set<BallViewInfo> balls;
    private final Set<ModelObserver> observers;
    private BallViewInfo playerBall;
    private BallViewInfo cpuBall;

    public TimedViewModel() {
        this(true);
    }

    public TimedViewModel(boolean benchmarkPerf) {
        this.benchmark = benchmarkPerf;
        this.balls = new HashSet<>();
        this.observers = new HashSet<>();
        this.printer = new frameTimePrinter();
    }

    @Override
    public synchronized void update(Set<Ball> normalBalls, Ball playerBall, Ball cpuBall) {
        tryRecordFrameTime();
        balls.clear();
        for (Ball ball : normalBalls) {
            this.balls.add(getBallViewInfo(BallType.NORMAL_BALL, ball));
        }
        this.playerBall = getBallViewInfo(BallType.PLAYER_BALL, playerBall);
        if (Objects.nonNull(cpuBall)) {
            this.cpuBall = getBallViewInfo(BallType.CPU_BALL, cpuBall);
        }
        notifyObservers();
    }

    private void notifyObservers() {
        for(ModelObserver observer: this.observers){
            observer.update();
        }
    }

    private static BallViewInfo getBallViewInfo(BallType type, Ball ball) {
        return new BallViewInfo(type, new Position(ball.getPositionX(), ball.getPositionY()), ball.getRadius());
    }

    private void tryRecordFrameTime() {
        if (this.benchmark) printer.recordFrameTime();
    }

    @Override
    public synchronized Set<BallViewInfo> getBalls() {
        final Set<BallViewInfo> allBalls = new HashSet<>(balls);
        allBalls.add(playerBall);
        if (cpuBall != null) {
            allBalls.add(cpuBall);
        }
        return allBalls;
    }

    @Override
    public synchronized void addObserver(ModelObserver observer) {
        this.observers.add(observer);
    }
}
