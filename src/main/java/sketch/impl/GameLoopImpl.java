package sketch.impl;

import sketch.api.GameLoop;
import sketch.api.controller.GameController;
import sketch.api.model.GameModel;
import sketch.impl.model.util.Vector;

import java.util.Objects;

public class GameLoopImpl implements GameLoop {

    private final GameModel model;
    private final GameController controller;
    private long lastUpdate = System.currentTimeMillis();

    public GameLoopImpl(GameModel model, GameController controller) {
        this.model = model;
        this.controller = controller;
    }

    @Override
    public void start() {
        boolean gameLoop = true;
        while (gameLoop) {
            Vector lastInput = this.controller.getInput();
            if (Objects.nonNull(lastInput)) {
                this.model.receiveInput(lastInput);
            }
            final long now = System.currentTimeMillis();
            final long elapsed = now - lastUpdate;
            this.lastUpdate = now;

            gameLoop = false;

            switch (model.updateBoard(elapsed)) {
                case PLAYER_WINS -> System.out.println("Player won");
                case CPU_WINS -> System.out.println("CPU won");
                case GAME_STOP -> System.out.println(this.model.getPoints());
                case GAME_CONTINUES -> gameLoop = true;
            }
        }
    }
}
