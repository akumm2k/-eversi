package org.reversi.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ReversiController {

    private final static Pattern INPUT_PATT = Pattern.compile("(\\d) (\\d)");

    private static final ReversiController INSTANCE = new ReversiController();
    private static final String EXIT_KEY = "q";

    private ReversiController() {}

    public static ReversiController getInstance() {
        return INSTANCE;
    }

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