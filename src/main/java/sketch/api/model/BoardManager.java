package sketch.api.model;

public interface BoardManager {

    /**
     * Updates Ball position and game in relation to the deltaTime
     *
     * @param deltaTime time passed after it was called.
     */
    void updateBoard(long deltaTime);
}
