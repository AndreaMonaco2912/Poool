package sketch.impl.model;

import sketch.api.model.*;
import sketch.api.view.ViewModel;
import sketch.impl.model.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class GameModelImpl implements GameModel {

    private final PlayerBallMover playerBallMover;
    private final Ball playerBall;
    private final Ball cpuBall;
    private final Set<Ball> balls;
    private final ViewModel viewModel;
    private final BoardManager boardManager;
    private long lastUpdate = System.currentTimeMillis();

    public GameModelImpl(ViewModel viewModel) {
        // final BoardConfiguration boardConfiguration = new MinimalBoardConfiguration();
        final BoardConfiguration boardConfiguration = new MassiveBoardConfiguration();
        this.playerBallMover = new PlayerBallMoverImpl();
        this.playerBall = boardConfiguration.getPlayerBall(this.playerBallMover);
        this.cpuBall = boardConfiguration.getCpuBall(null);
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
    public void startGameLoop() {
        while (true) {
            final long now = System.currentTimeMillis();
            final long elapsed = now - lastUpdate;
            this.lastUpdate = now;

            this.boardManager.updateBoard(elapsed);
            viewModel.update(this.balls, this.playerBall, this.cpuBall);
        }
    }
}
