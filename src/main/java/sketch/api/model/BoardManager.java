package sketch.api.model;

import sketch.impl.model.util.Points;

public interface BoardManager {

    /**
     * Updates Ball position and game in relation to the deltaTime
     *
     * @param deltaTime time passed after it was called.
     */
    GameStatus updateBoard(long deltaTime);

    /**
     * Gives calculated points during last update
     *
     * @return new points
     */
    Points getNewPoints();
}
