package sketch;

import sketch.api.model.GameModel;
import sketch.api.view.ModelObserver;
import sketch.api.view.ViewModel;
import sketch.impl.model.GameModelImpl;
import sketch.impl.view.TimedViewModel;
import sketch.impl.view.ViewImpl;

import javax.swing.*;

public class Sketch01 {
    static void main() {
        ViewModel viewModel = new TimedViewModel();
        GameModel model = new GameModelImpl(viewModel);
        SwingUtilities.invokeLater(() -> {
            ModelObserver view = new ViewImpl(1200, 800);
            viewModel.addObserver(view);
        });

        model.startGameLoop();
    }
}
