package sketch.api.model;

import sketch.impl.model.util.Vector;

/**
 * Main class of the model, it receives input from the controller and updates the View
 */
public interface GameModel {

    /**
     * receive an input from the Controller for the player ball
     *
     * @param inputVector received
     */
    void receiveInput(Vector inputVector);
}
