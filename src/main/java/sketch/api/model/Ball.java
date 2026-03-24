package sketch.api.model;

import sketch.impl.model.util.Position;
import sketch.impl.model.util.Vector;

public interface Ball {

    void updateState(long deltaTime);

    void setPosition(Position position);

    void setSpeed(Vector speed);

    double getPositionX();

    double getPositionY();

    double getSpeedX();

    double getSpeedY();

    double getMass();

    double getRadius();
}
