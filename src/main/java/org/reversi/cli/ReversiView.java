package org.reversi.cli;

import java.util.Set;

/**
 * The view class of the MVC architecture for reversi
 */
public final class ReversiView {

    /**
     * a colored string to represent a possible / feasible move given a game state
     */
    private final static String POSSIBLE_MOVE_TILE = String.format("%s%s%s", Color.YELLOW, "*", Color.RESET);

    /**
     * a string to represent an empty tile on the board
     */
    private final static String EMPTY_TILE = "_";

    /**
     * strings to represent the player disks on the board
     */
    public final static String[] PLAYER_TILES = {"x", "o"};

    /**
     * singleton view instance
     */
    private final static ReversiView INSTANCE = new ReversiView();

    /**
     * private constructor for singleton behavior
     */
    private ReversiView() {}

    /**
     * method to retrieve the singleton instance
     * @return the singleton view instance
     */
    public static ReversiView getInstance() {
        return INSTANCE;
    }

    /**
     * An enum to hold all the colors for displaying strings printed by the View
     * using ANSI strings
     * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code">ANSI wikipedia</a>
     */
    private enum Color {
        /**
         * turns off all ANSI attributes set so far, which should return the console to its defaults
         */
        RESET("\033[0m"),

        /**
         * ANSI red
         */
        @SuppressWarnings("unused")
        RED("\033[0;31m"),

        /**
         * ANSI yellow
         */
        YELLOW("\033[0;33m"),

        /**
         * ANSI blue
         */
        BLUE("\033[0;34m");

        /**
         * string holding the ANSI color code
         */
        private final String colorCode;

        /**
         * The enum constructor
         * @param colorCode string holding the ANSI color code
         */
        Color(final String colorCode) {
            this.colorCode = colorCode;
        }

        /**
         * Overridden toString to directly access color code
         * @return the enum's colorCode
         */
        @Override
        public String toString() {
            return colorCode;
        }
    }

    /**
     * prints the current state of the board to the console
     * @param gameState the game board to be printed
     */
    public void printBoard(final ReversiModel gameState) {
        final int[][] board = gameState.getBoard();
        final Set<Coordinate> possibleMoves = gameState.getPossibleMoves();

        // print row ids
        System.out.print("  ");
        for (int i = 0; i < board.length; i++)
            System.out.printf("%s%d%s ", Color.BLUE, i, Color.RESET);
        System.out.println();

        for (int i = 0; i < board.length; i++) {
            System.out.printf("%s%d%s ", Color.BLUE, i, Color.RESET);
            for (int j = 0; j < board.length; j++) {
                // print the appropriate tile

                if (possibleMoves.contains(new Coordinate(i, j))) {
                    System.out.print(POSSIBLE_MOVE_TILE);
                } else if (board[i][j] == ReversiModel.EMPTY) {
                    System.out.print(EMPTY_TILE);
                } else {
                    final int playerID = ReversiModel.getPlayerIndex(board[i][j]);
                    System.out.print(PLAYER_TILES[playerID]);
                }

                System.out.print(" ");

            }
            System.out.println();
        }

    }

    /**
     * prints out some introductory text
     * @param exitKey key required for exiting the game, should the player want to
     */
    public void welcome(final String exitKey) {
        System.out.println("Welcome to Яeversi");
        System.out.printf("Enter %s to exit%n", exitKey);
    }

    /**
     * prints the current player to the console
     * @param currentPlayer the player whose turn it is to play
     */
    public void printCurrentPlayer(final int currentPlayer) {
        final int playerID = ReversiModel.getPlayerIndex(currentPlayer);
        System.out.println("Turn: " + PLAYER_TILES[playerID]);
    }

    /**
     * prints out the current result of the game
     * @param model carries the game state
     */
    public void printResult(final ReversiModel model) {
        if (model.getWinner() == ReversiModel.DRAW) {
            System.out.println("DRAW");
            return;
        }

        final int playerID = ReversiModel.getPlayerIndex(model.getWinner());
        printBoard(model);

        System.out.println("Winner: " + PLAYER_TILES[playerID]);

    }
}
