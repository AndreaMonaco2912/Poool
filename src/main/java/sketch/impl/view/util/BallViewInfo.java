package sketch.impl.view.util;

import sketch.api.view.util.BallType;
import sketch.impl.model.util.Position;

public record BallViewInfo(BallType ballType, Position position, double radius) {
}
