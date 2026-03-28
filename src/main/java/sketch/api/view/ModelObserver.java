package sketch.api.view;

import sketch.impl.view.util.BallViewInfo;

import java.util.Set;

/**
 * A class width a single method that get called by ViewModel
 */
public interface ModelObserver {

    /**
     * updates the observer with all the information needed to draw the model
     *
     * @param balls graphical details
     */
    void update(Set<BallViewInfo> balls);
}
