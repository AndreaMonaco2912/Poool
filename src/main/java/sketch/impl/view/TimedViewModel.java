package sketch.impl.view;

import sketch.api.model.Ball;
import sketch.api.view.ModelObserver;
import sketch.api.view.ViewModel;
import sketch.api.view.util.BallType;
import sketch.impl.model.util.Points;
import sketch.impl.model.util.Position;
import sketch.impl.view.util.BallViewInfo;
import sketch.impl.view.util.FrameTimePrinter;

import java.util.*;

public class TimedViewModel implements ViewModel {

    private final boolean benchmark;
    private final FrameTimePrinter printer;

    private final List<BallViewInfo> balls;
    private final Set<ModelObserver> observers;
    private BallViewInfo playerBall;
    private BallViewInfo cpuBall;
    private List<BallViewInfo> holes;
    private Points points;

    public TimedViewModel() {
        this(true);
    }

    public TimedViewModel(boolean benchmarkPerf) {
        this.benchmark = benchmarkPerf;
        this.balls = new ArrayList<>();
        this.observers = new HashSet<>();
        this.printer = new FrameTimePrinter();
        this.points = new Points(0, 0);
    }

    @Override
    public synchronized void update(List<Ball> normalBalls, Ball playerBall, Ball cpuBall) {
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
        for (ModelObserver observer : this.observers) {
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
    public synchronized List<BallViewInfo> getBalls() {
        final List<BallViewInfo> allBalls = new ArrayList<>(balls);
        allBalls.add(playerBall);
        if (cpuBall != null) {
            allBalls.add(cpuBall);
        }
        if (holes != null) {
            allBalls.addAll(holes);
        }
        return allBalls;
    }

    @Override
    public synchronized void addObserver(ModelObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public synchronized void setHoles(List<Ball> holes) {
        this.holes = new ArrayList<>();
        for (Ball hole : holes) {
            this.holes.add(getBallViewInfo(BallType.HOLE, hole));
        }
    }

    @Override
    public synchronized void updatePoints(Points points) {
        this.points = points;
    }

    @Override
    public synchronized Points getPoints() {
        return this.points;
    }
}
