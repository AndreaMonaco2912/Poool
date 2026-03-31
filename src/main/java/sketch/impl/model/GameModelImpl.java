package sketch.impl.model;

import sketch.api.model.*;
import sketch.api.view.ViewModel;
import sketch.impl.model.util.Points;
import sketch.impl.model.util.Vector;

import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GameModelImpl implements GameModel {

    private final PlayerBallMover playerBallMover;
    private final PlayerBallMover cpuBallMover;
    private final Ball playerBall;
    private final Ball cpuBall;
    private final Set<Ball> balls;
    private final ViewModel viewModel;
    private final BoardManager boardManager;
    private final Random rand = new Random();

    private int cpuNumber = 0;
    private Thread cpuPlayer;


    private Points points;

    public GameModelImpl(ViewModel viewModel) {
        final BoardConfiguration boardConfiguration = new MinimalBoardConfiguration();
        // final BoardConfiguration boardConfiguration = new MassiveBoardConfiguration();
        this.playerBallMover = new PlayerBallMoverImpl();
        this.cpuBallMover = new CPUBallMoverImpl();
        final Set<Ball> holes = boardConfiguration.getHoles();
        this.playerBall = boardConfiguration.getPlayerBall(this.playerBallMover);
        this.cpuBall = boardConfiguration.getCpuBall(this.cpuBallMover);
        this.balls = boardConfiguration.getSmallBalls();
        final BallManager ballManager = new BallManager(this.playerBall, this.cpuBall, this.balls, holes);
        this.boardManager = new BoardManagerImpl(ballManager, boardConfiguration.getBoardBoundary());
        this.points = new Points(0, 0);
        this.viewModel = viewModel;
        viewModel.setHoles(holes);
        viewModel.update(this.balls, this.playerBall, this.cpuBall);
        if (Objects.nonNull(this.cpuBall)) playCPU();
    }

    @Override
    public void receiveInput(Vector inputVector) {
        this.playerBallMover.addNextMove(inputVector);
    }

    @Override
    public GameStatus updateBoard(long elapsed) {
        if (Objects.isNull(cpuBall) && Objects.nonNull(cpuPlayer)) cpuPlayer.interrupt();
        GameStatus gameStatus = boardManager.updateBoard(elapsed);
        points = points.add(boardManager.getNewPoints());
        viewModel.update(this.balls, this.playerBall, this.cpuBall);
        viewModel.updatePoints(points);
        return gameStatus;
    }

    @Override
    public Points getPoints() {
        return points;
    }

    private void playCPU(){
        cpuPlayer = new Thread(() -> {
            while (true){
                if (Objects.nonNull(this.cpuBall)) moveCpu();
            }
        }, "CPU player" + ++cpuNumber);

        cpuPlayer.start();
    }

    private void moveCpu() {
        var angle = rand.nextDouble() * Math.PI * 0.25;
        var v = new Vector(Math.cos(angle), Math.sin(angle)).mul(1.5);
        this.cpuBallMover.addNextMove(v);
    }
}
