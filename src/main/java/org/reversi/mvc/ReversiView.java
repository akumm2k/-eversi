package org.reversi.mvc;

import java.util.Set;

public class ReversiView {
    private final static String POSSIBLE_MOVE_CHAR = String.format("%s%s%s", Color.YELLOW, "*", Color.RESET);
    private final static String EMPTY_TILE = "_";
    public final static String[] PLAYER_CHARS = {"x", "o"};

    public final static ReversiView INSTANCE = new ReversiView();

    private ReversiView() {}
    public static ReversiView getInstance() {
        return INSTANCE;
    }

    private enum Color {
        RESET("\033[0m"),  // Text Reset

        @SuppressWarnings("unused")
        RED("\033[0;31m"),
        YELLOW("\033[0;33m"),
        BLUE("\033[0;34m");

        private final String colorCode;
        Color(final String colorCode) {
            this.colorCode = colorCode;
        }

        @Override
        public String toString() {
            return colorCode;
        }
    }

    public void printBoard(final ReversiModel model) {
        // Print the current state of the board to the console
        final int[][] board = model.getBoard();
        final Set<Coordinate> possibleMoves = model.getPossibleMoves();

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
                    System.out.print(POSSIBLE_MOVE_CHAR);
                } else if (board[i][j] == ReversiModel.EMPTY) {
                    System.out.print(EMPTY_TILE);
                } else {
                    final int playerID = ReversiModel.getPlayerIndex(board[i][j]);
                    System.out.print(PLAYER_CHARS[playerID]);
                }

                System.out.print(" ");

            }
            System.out.println();
        }

    }

    public void welcome(final String exitKey) {
        System.out.println("Welcome to Яeversi");
        System.out.printf("Enter %s to exit%n", exitKey);
    }

    public void printCurrentPlayer(final int currentPlayer) {
        // Print the current player to the console
        final int playerID = ReversiModel.getPlayerIndex(currentPlayer);
        System.out.println("Turn: " + PLAYER_CHARS[playerID]);
    }

    public void printResult(final ReversiModel model) {
        // Print the winner of the game to the console
        if (model.getWinner() == ReversiModel.DRAW) {
            System.out.println("DRAW");
            return;
        }

        final int playerID = ReversiModel.getPlayerIndex(model.getWinner());
        printBoard(model);

        System.out.println("Winner: " + PLAYER_CHARS[playerID]);

    }
}
