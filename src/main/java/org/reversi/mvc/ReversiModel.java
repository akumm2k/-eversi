package org.reversi.mvc;

import java.util.HashSet;
import java.util.Set;

public class ReversiModel {
    private final int[][] board;
    private Set<Coordinate> possibleMoves;
    public int currentPlayer;

    public final static int PLAYER1 = 1;
    public final static int PLAYER2 = -1;
    public final static int EMPTY = 0;
    public static final int DRAW = 2;
    private int winner = EMPTY;
    final private int rows;
    final private int cols;


    public ReversiModel(final int boardSize) {
        if (boardSize % 2 != 0) {
            throw new RuntimeException("board size must be divisible by 2");
        }
        this.rows = this.cols = boardSize;
        this.board = new int[rows][cols];

        final int mid_lo = rows / 2 - 1;
        final int mid_hi = mid_lo + 1;

        this.board[mid_lo][mid_lo] = PLAYER1;
        this.board[mid_hi][mid_hi] = PLAYER1;
        this.board[mid_lo][mid_hi] = PLAYER2;
        this.board[mid_hi][mid_lo] = PLAYER2;

        this.currentPlayer = PLAYER1;
        this.possibleMoves = this.getPossibleMoveCoordsForPlayer(this.currentPlayer);
    }

    private void switchTurn() {
        this.currentPlayer = -this.currentPlayer;
    }

    private void gatherPossibleMoves(final Set<Coordinate> possibleCoords,
                                     final int opponent,
                                     final int row, final int col) {

        for (int xDir = -1; xDir <= 1; xDir++) {
            for (int yDir = -1; yDir <= 1; yDir++) {
                if (xDir == 0 && yDir == 0) {continue;}
                int x = row + xDir;
                int y = col + yDir;

                boolean separated = false;
                while (this.insideBoard(x, y) && this.board[x][y] == opponent) {
                    x += xDir;
                    y += yDir;
                    separated = true;
                }

                if (separated && insideBoard(x, y) && this.board[x][y] == EMPTY) {
                    possibleCoords.add(new Coordinate(x, y));
                }
            }
        }

    }

    private Set<Coordinate> getPossibleMoveCoordsForPlayer(final int player) {
        final Set<Coordinate> possibleCoords = new HashSet<>();
        final int opponent = (player == PLAYER1) ? PLAYER2 : PLAYER1;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (this.board[i][j] == player)
                    this.gatherPossibleMoves(possibleCoords, opponent, i, j);

        return possibleCoords;
    }

    private boolean insideBoard(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    private void stealAll(final int row, final int col,
                          final int xDir, final int yDir,
                          final int opponent) {
        int x = row + xDir;
        int y = col + yDir;

        if (this.insideBoard(x, y) && this.board[x][y] == opponent) {
            x += xDir;
            y += yDir;

            while (this.insideBoard(x, y) && this.board[x][y] == opponent) {
                x += xDir;
                y += yDir;
            }

            if (this.insideBoard(x, y) && this.board[x][y] == this.currentPlayer) {
                x -= xDir;
                y -= yDir;

                // take all the opponent's disks
                while (this.insideBoard(x, y) && this.board[x][y] != this.currentPlayer) {
                    this.board[x][y] = this.currentPlayer;
                    x -= xDir;
                    y -= yDir;
                }
            }
        }
    }

    public boolean makeMove(final int row, final int col) {
        if (!this.possibleMoves.contains(new Coordinate(row, col))) {
            return false;
        }

        // mark move
        this.board[row][col] = this.currentPlayer;

        // steal all of opponent's disks
        final int opponent = (this.currentPlayer == PLAYER1) ? PLAYER2 : PLAYER1;

        for (int xDir = -1; xDir <= 1; xDir++) {
            for (int yDir = -1; yDir <= 1; yDir++) {
                if (xDir == 0 && yDir == 0) {continue;}

                this.stealAll(row, col, xDir, yDir, opponent);

            }
        }

        Set<Coordinate> nextPossibleMoves = this.getPossibleMoveCoordsForPlayer(opponent);
        if (nextPossibleMoves.size() != 0) {
            this.switchTurn();
        } else {
            nextPossibleMoves = this.getPossibleMoveCoordsForPlayer(this.currentPlayer);
            if (nextPossibleMoves.size() == 0) {
                this.winner = this.getMajorityPlayer();
            }
        }

        this.possibleMoves = nextPossibleMoves;
        return true;
    }

    public static int getPlayerIndex(final int player) {
        assert PLAYER1 * PLAYER2 == -1;
        return (player + 1) / 2;
    }

    private int getMajorityPlayer() {
        final int[] occupiedTiles = {0, 0};

        for (int[] row: this.board) {
            for (int player: row) {
                occupiedTiles[getPlayerIndex(player)] += 1;
            }
        }

        if (occupiedTiles[0] == occupiedTiles[1]) {
            return DRAW;
        }

        return occupiedTiles[0] > occupiedTiles[1] ? PLAYER2 : PLAYER1;
    }

    public boolean isGameOver() {
        return this.winner == PLAYER1 || this.winner == PLAYER2 || this.winner == DRAW;
    }

    public int getWinner() {
        // Check if one player has won the game
        // Return 1 if player 1 has won, -1 if player 2 has won, 0 if the game is a draw
        return this.winner;
    }

    public ReversiModel getClone() {
        // TODO: remove boardSize init from model
        ReversiModel clonedModel = new ReversiModel(this.rows);
        for(int i = 0; i < clonedModel.board.length; i++)
            System.arraycopy(this.board[i], 0, clonedModel.board[i], 0, clonedModel.board.length);

        clonedModel.winner = this.winner;
        clonedModel.currentPlayer = this.currentPlayer;

        clonedModel.possibleMoves = new HashSet<>();
        clonedModel.possibleMoves.addAll(this.possibleMoves);

        return clonedModel;
    }

    public Set<Coordinate> getPossibleMoves() {
        return this.possibleMoves;
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int[][] getBoard() {
        return this.board;
    }
}