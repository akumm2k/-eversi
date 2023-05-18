package org.reversi.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The controller class of the MVC architecture for reversi
 */
public final class ReversiController {

    /**
     * Pattern to match input format
     */
    private final static Pattern INPUT_PATT = Pattern.compile("(\\d) (\\d)");

    /**
     * reference to the view singleton of the MVC architecture
     */
    private static final ReversiView VIEW = ReversiView.getInstance();

    /**
     * the controller singleton instance
     */
    private static final ReversiController INSTANCE = new ReversiController();

    /**
     * the key used for exiting the game
     */
    private static final String EXIT_KEY = "q";

    /**
     * private constructor for singleton behavior
     */
    private ReversiController() {}

    /**
     * getter for the singleton instance
     * @return the controller singleton instance
     */
    public static ReversiController getInstance() {
        return INSTANCE;
    }

    /**
     *
     * @param input the input string
     * @return the parsed coordinates from the input
     */
    private Coordinate getInputCoordFrom(final String input) {
        final Matcher inputMatcher = INPUT_PATT.matcher(input);

        if (!inputMatcher.find()) {
            throw new RuntimeException("Bad input");
        }

        return new Coordinate(
                Integer.parseInt(inputMatcher.group(1)),
                Integer.parseInt(inputMatcher.group(2))
        );
    }

    /**
     * method to start the game with or without an AI agent
     * @param model the game model representing the game state
     * @param withAgent a boolean, which is true iff the game is played with an AI agent
     */
    public void startGameOn(ReversiModel model, boolean withAgent) {
        final ReversiAgent agent = new ReversiAgent(model, 4, -model.getCurrentPlayer());

        VIEW.welcome(EXIT_KEY);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            while (!model.isGameOver()) {
                System.out.println();
                VIEW.printBoard(model);
                VIEW.printCurrentPlayer(model.getCurrentPlayer());

                if (withAgent && model.getCurrentPlayer() == agent.getAgentID()) {
                    Coordinate bestMove = agent.findBestMove();
                    System.out.printf("Agent: %d %d%n", bestMove.x(), bestMove.y());
                    model.makeMove(bestMove.x(), bestMove.y());
                    continue;
                }

                try {
                    final String line = input.readLine();
                    if (line.equals(EXIT_KEY)) {break;}

                    final Coordinate inputCoord = getInputCoordFrom(line);
                    if (!model.makeMove(inputCoord.x(), inputCoord.y())) {
                        System.out.println("Invalid move, try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Couldn't make move. Please retry.");
                }
            }
        } catch (IOException e) {
            System.out.println("IOException occurred");
        }

        if (model.isGameOver()) {
            VIEW.printResult(model);
        }
    }

}