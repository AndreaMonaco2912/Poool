package sketch.impl.model.util;

import org.jetbrains.annotations.NotNull;

public record Points(int player, int cpu) {
    public Points add(Points newPoints){
        return new Points(player + newPoints.player(), cpu + newPoints.cpu());
    }

    @Override
    @NotNull
    public String toString() {
        return "Points{" +
                "Player: " + player +
                ", CPU: " + cpu +
                '}';
    }
}
