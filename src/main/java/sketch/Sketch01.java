package sketch;

import sketch.api.GameLoop;
import sketch.api.controller.GameController;
import sketch.api.model.GameModel;
import sketch.api.view.ModelObserver;
import sketch.api.view.ViewModel;
import sketch.impl.GameLoopImpl;
import sketch.impl.controller.GameControllerImpl;
import sketch.impl.model.GameModelImpl;
import sketch.impl.view.TimedViewModel;
import sketch.impl.view.ViewImpl;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Sketch01 {
    static void main() {
        final GameController controller = new GameControllerImpl();
        final ViewModel viewModel = new TimedViewModel();
        final GameModel model = new GameModelImpl(viewModel);
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ModelObserver view = new ViewImpl(1200, 800, viewModel, controller);
                viewModel.addObserver(view);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            System.err.println("Exception on creation of JPanel");
        }

        final GameLoop gameLoop = new GameLoopImpl(model, controller);
        gameLoop.start();
    }
}
