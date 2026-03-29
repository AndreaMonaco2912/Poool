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

    public GameLoopImpl(GameModel model, GameController controller){
        this.model = model;
        this.controller = controller;
    }

    @Override
    public void start() {
        while (true) {
            Vector lastInput = this.controller.getInput();
            if(Objects.nonNull(lastInput)){
                this.model.receiveInput(lastInput);
            }
            final long now = System.currentTimeMillis();
            final long elapsed = now - lastUpdate;
            this.lastUpdate = now;

            this.model.updateBoard(elapsed);
        }
    }
}
