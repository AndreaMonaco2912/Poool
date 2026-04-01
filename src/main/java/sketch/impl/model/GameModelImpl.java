package sketch.impl.model;

import sketch.api.model.*;
import sketch.api.view.ViewModel;
import sketch.impl.model.util.Points;
import sketch.impl.model.util.Vector;

import java.util.Objects;
import java.util.Set;

public class GameModelImpl implements GameModel {

    private final PlayerBallMover playerBallMover;
    private final Ball playerBall;
    private final Ball cpuBall;
    private final Set<Ball> balls;
    private final ViewModel viewModel;
    private final BoardManager boardManager;

    private Points points;
    private final CPUPlayer cpuPlayer;

    public GameModelImpl(ViewModel viewModel) {
        final BoardConfiguration boardConfiguration = new MinimalBoardConfiguration();
        //final BoardConfiguration boardConfiguration = new MassiveBoardConfiguration();
        this.playerBallMover = new PlayerBallMoverImpl();
        PlayerBallMover cpuBallMover = new CPUBallMoverImpl();
        final Set<Ball> holes = boardConfiguration.getHoles();
        this.playerBall = boardConfiguration.getPlayerBall(this.playerBallMover);
        this.cpuBall = boardConfiguration.getCpuBall(cpuBallMover);
        this.balls = boardConfiguration.getSmallBalls();
        final BallManager ballManager = new BallManager(this.playerBall, this.cpuBall, this.balls, holes);
        this.boardManager = new BoardManagerImpl(ballManager, boardConfiguration.getBoardBoundary());
        this.points = new Points(0, 0);
        this.viewModel = viewModel;
        viewModel.setHoles(holes);
        viewModel.update(this.balls, this.playerBall, this.cpuBall);
        if (Objects.nonNull(this.cpuBall)) {
            this.cpuPlayer = new CPUPlayerImpl(cpuBallMover);
            this.cpuPlayer.start();
        } else {
            this.cpuPlayer = null;
        }
    }

    @Override
    public void receiveInput(Vector inputVector) {
        this.playerBallMover.addNextMove(inputVector);
    }

    @Override
    public GameStatus updateBoard(long elapsed) {
        GameStatus gameStatus = boardManager.updateBoard(elapsed);
        if (gameStatus != GameStatus.GAME_CONTINUES && Objects.nonNull(cpuPlayer)) {
            cpuPlayer.stop();
        }
        points = points.add(boardManager.getNewPoints());
        viewModel.update(this.balls, this.playerBall, this.cpuBall);
        viewModel.updatePoints(points);
        return gameStatus;
    }

    @Override
    public Points getPoints() {
        return points;
    }
}
