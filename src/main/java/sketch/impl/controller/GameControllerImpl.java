package sketch.impl.controller;

import sketch.api.controller.GameController;
import sketch.impl.model.util.Vector;

import java.util.Objects;

public class GameControllerImpl implements GameController {

    private Vector lastDirection;

    @Override
    public synchronized void onPlayerInput(Vector direction) {
        this.lastDirection = direction;
    }

    @Override
    public synchronized Vector getInput() {
        Vector direction = Objects.nonNull(lastDirection) ? lastDirection.copy() : null;
        lastDirection = null;
        return direction;
    }
}
