package sketch.api.model;

import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

/**
 * Represents all game balls, it also moves the ball
 * itself according to its capacity (it ignores other balls and game board)
 */
public interface Ball {

    /**
     * Updates the ball position and speed
     *
     * @param deltaTime passed from last call
     */
    void updateState(long deltaTime);

    /**
     * Set the ball position
     *
     * @param position: the new position
     */
    void setPosition(Position position);

    /**
     * Set the ball speed and notify the mover
     *
     * @param speed: the new speedVector
     */
    void setSpeed(Vector speed);

    /**
     * set the last hitter of the ball
     *
     * @param ball that hit for the last
     */
    void setHittingBall(HitBy ball);

    /**
     * @return the last hitter of the ball
     */
    HitBy getHittingBall();

    /**
     * @return the XPosition
     */
    double getPositionX();

    /**
     * @return the YPosition
     */
    double getPositionY();

    /**
     * @return the XSpeed
     */
    double getSpeedX();

    /**
     * @return YSpeed
     */
    double getSpeedY();

    /**
     * @return the ballMass
     */
    double getMass();

    /**
     * @return the Ball radius
     */
    double getRadius();
}
