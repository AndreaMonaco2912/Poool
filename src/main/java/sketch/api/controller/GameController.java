package sketch.api.controller;

import sketch.impl.model.util.Vector;

/**
 * get player input and gives them to the Model
 */
public interface GameController {

    /**
     * Receives from the view the direction
     *
     * @param direction in input
     */
    void onPlayerInput(Vector direction);

    /**
     * gives input to the model
     *
     * @return the input
     */
    Vector getInput();
}
