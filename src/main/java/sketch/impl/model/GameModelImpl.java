package sketch.impl.model;

import sketch.api.model.*;
import sketch.api.view.ViewModel;
import sketch.impl.model.util.Vector;

import java.util.HashSet;
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

    public GameModelImpl(ViewModel viewModel) {
        final BoardConfiguration boardConfiguration = new MinimalBoardConfiguration();
        // final BoardConfiguration boardConfiguration = new MassiveBoardConfiguration();
        this.playerBallMover = new PlayerBallMoverImpl();
        this.cpuBallMover = new PlayerBallMoverImpl();
        this.playerBall = boardConfiguration.getPlayerBall(this.playerBallMover);
        this.cpuBall = boardConfiguration.getCpuBall(this.cpuBallMover);
        this.balls = boardConfiguration.getSmallBalls();
        this.boardManager = new BoardManagerImpl(this.allBalls(), boardConfiguration.getBoardBoundary());
        this.viewModel = viewModel;
        viewModel.update(this.balls, this.playerBall, this.cpuBall);
    }

    private Set<Ball> allBalls() {
        final Set<Ball> allBalls = new HashSet<>(balls);
        allBalls.add(playerBall);
        if (cpuBall != null) {
            allBalls.add(cpuBall);
        }
        return allBalls;
    }

    @Override
    public void receiveInput(Vector inputVector) {
        this.playerBallMover.addNextMove(inputVector);
    }

    @Override
    public void updateBoard(long elapsed) {
        if (Objects.nonNull(this.cpuBall)) moveCpu();
        this.boardManager.updateBoard(elapsed);
        viewModel.update(this.balls, this.playerBall, this.cpuBall);
    }

    private void moveCpu() {
        if (this.cpuBall.getSpeedX() + this.cpuBall.getSpeedY() < 0.01) {
            var angle = rand.nextDouble() * Math.PI * 0.25;
            var v = new Vector(Math.cos(angle), Math.sin(angle)).mul(1.5);
            this.cpuBallMover.addNextMove(v);
        }
    }
}
