package org.reversi;

import org.reversi.mvc.ReversiController;
import org.reversi.mvc.ReversiModel;

public class Main {
    public static void main(String[] args) {
        final ReversiModel model = new ReversiModel(8);
        final ReversiController controller = ReversiController.getInstance();

        controller.startGameOn(model, true);
    }
}