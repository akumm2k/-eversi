package org.reversi;

import org.reversi.cli.Coordinate;
import org.reversi.cli.ReversiModel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing class for the MVC model
 */
public class TestModel {
    private ReversiModel model;
    private static final int PLAYER_X = ReversiModel.PLAYER2;
    private static final int PLAYER_O = ReversiModel.PLAYER1;

    /**
     * empty constructor
     */
    TestModel() {}

    @BeforeEach
    void initModel() {
        model = new ReversiModel(4);
    }

    @AfterEach
    void destroyModel() {
        model = null;
    }
    private static void printBoard(int[][] board, Set<Coordinate> poss) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (poss.contains(new Coordinate(i, j)))
                    System.out.print("* ");
                else System.out.printf("%s ", board[i][j] == PLAYER_X ? "x" : board[i][j] == PLAYER_O ? "o" : "_");
            }
            System.out.println();
        }
    }

    private static void testMove(final ReversiModel model,
                                 final int i, final int j,
                                 @SuppressWarnings("SameParameterValue") final boolean print) {
        model.makeMove(i, j);
        if (print) {
            printBoard(model.getBoard(), model.getPossibleMoves());
            System.out.println(model.currentPlayer == -1 ? "x" : "o");
            System.out.println(model.getPossibleMoves());
        }
    }

    /**
     * tests the model on a small hardwired game
     */
    @Test
    public void testModel() {
        final boolean print = false;

        //noinspection ConstantConditions
        if (print) {
            printBoard(model.getBoard(), model.getPossibleMoves());
            System.out.println(model.getPossibleMoves());
        }

        testMove(model, 0, 2, print);
        testMove(model, 0, 3, print);
        testMove(model, 3, 1, print);
        testMove(model, 1, 0, print);
        testMove(model, 0, 0, print);
        testMove(model, 0, 1, print);
        testMove(model, 1, 3, print);
        testMove(model, 3, 0, print);
        testMove(model, 2, 0, print);
        testMove(model, 3, 2, print);
        testMove(model, 2, 3, print);
        testMove(model, 3, 3, print);

        assertEquals(model.getWinner(), PLAYER_X);
    }
}
