package org.reversi;

import org.reversi.mvc.ReversiController;
import org.reversi.mvc.ReversiModel;

/**
 * The Main class to run the game
 */
public class Main {
    /**
     * Forbidden constructor as Main is a utility class
     */
    public Main() {
        throw new RuntimeException("Utility class can't be initialized.");
    }

    /**
     * runs the game with an AI agent
     * @param args CLI args
     */
    public static void main(String[] args) {
        final ReversiModel model = new ReversiModel(8);
        final ReversiController controller = ReversiController.getInstance();

        controller.startGameOn(model, true);
    }
}