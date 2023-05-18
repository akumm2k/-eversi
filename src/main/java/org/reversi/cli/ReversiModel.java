package org.reversi.cli;

import java.util.HashSet;
import java.util.Set;

/**
 * The model class of the MVC architecture for reversi
 */
public class ReversiModel {
    /**
     * 2D array to represent the board
     */
    private final int[][] board;

    /**
     * set of possible moves given the current game state
     */
    private Set<Coordinate> possibleMoves;

    /**
     * the player, whose turn it is to make a move
     */
    public int currentPlayer;

    /**
     * player 1 encoding
     */
    public final static int PLAYER1 = 1;

    /**
     * player 2 encoding
     */
    public final static int PLAYER2 = -1;

    /**
     * code for an empty / unoccupied tile in the board
     */
    public final static int EMPTY = 0;

    /**
     * code for representing the result of a draw
     */
    public static final int DRAW = 2;

    /**
     * represents the winner of the game.
     * set to EMPTY by default.
     * @see ReversiModel#EMPTY
     */
    private int winner = EMPTY;

    /**
     * represents the number of rows in the board
     */
    final private int rows;

    /**
     * represents the number of columns in the board
     */
    final private int cols;


    /**
     * game initializer
     * requires the boardSize to be even.
     * sets the current player to player1.
     * @see ReversiModel#PLAYER1
     * @param boardSize the size of the game board
     */
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

    /**
     * switches the player turn in the game
     */
    private void switchTurn() {
        this.currentPlayer = -this.currentPlayer;
    }

    /**
     * helper for adding all the possible moves from a given position in the board
     * @param possibleCoords the set to add the new detected possible moves to
     * @param opponent the opponent in the current game state
     * @param row the row position of the current player
     * @param col the column position of the current player
     */
    private void addPossibleMovesFrom(final int row, final int col, final int opponent,
                                      final Set<Coordinate> possibleCoords) {

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

    /**
     *
     * @param player assumed to be the "current" player in the context of obtaining the move possibilities
     *               not to be confused with this.currentPlayer
     * @return all possible moves for the player in the current game state
     */
    private Set<Coordinate> getPossibleMoveCoordsForPlayer(final int player) {
        final Set<Coordinate> possibleCoords = new HashSet<>();
        final int opponent = (player == PLAYER1) ? PLAYER2 : PLAYER1;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (this.board[i][j] == player)
                    this.addPossibleMovesFrom(i, j, opponent, possibleCoords);

        return possibleCoords;
    }

    /**
     *
     * @param row row coordinate
     * @param col column coordinate
     * @return true iff (row, col) is a coordinate inside the board
     */
    private boolean insideBoard(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * once a move is made, this method finds the captured disks of the opponent
     * and turns them to the current player's disks
     * @param row the 1st coordinate of the move made by the current player
     * @param col the 2nd coordinate of the move made by the current player
     * @param xDir the x-direction for detecting the potentially captured disks of the opponent
     * @param yDir the y-direction for detecting the potentially captured disks of the opponent
     * @param opponent the opponent - player1 iff current player is player2
     */
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

    /**
     * method to make a move, altering the game state
     * @param row 1st coordinate of the requested move to be made
     * @param col 2nd coordinate of the requested move to be made
     * @return true iff the move was valid
     */
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

    /**
     * given a player, converts it into an index for some list where player1 is indexed by 0 and player2 by 1
     * @param player the player whose index is requested
     * @return an index of player for some list where player1 is indexed by 0 and player2 by 1
     */
    public static int getPlayerIndex(final int player) {
        assert PLAYER1 * PLAYER2 == -1;
        return (player + 1) / 2;
    }

    /**
     *
     * @return the player who owns the majority of the disks on the current board
     */
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

    /**
     * method to check if the game is over
     * @return true iff the game is over
     */
    public boolean isGameOver() {
        return this.winner == PLAYER1 || this.winner == PLAYER2 || this.winner == DRAW;
    }

    /**
     * getter for winner
     * @see ReversiModel#winner
     * @return PLAYER1 or PLAYER2 if a player won else DRAW
     */
    public int getWinner() {
        // Check if one player has won the game
        // Return 1 if player 1 has won, -1 if player 2 has won, 0 if the game is a draw
        return this.winner;
    }

    /**
     * clones the current game state into a new model
     * @return the clone of "this" model
     */
    public ReversiModel getClone() {
        // TODO: remove boardSize init from model
        // TODO: reevaluate the necessity of a variadic board size
        ReversiModel clonedModel = new ReversiModel(this.rows);
        for(int i = 0; i < clonedModel.board.length; i++)
            System.arraycopy(this.board[i], 0, clonedModel.board[i], 0, clonedModel.board.length);

        clonedModel.winner = this.winner;
        clonedModel.currentPlayer = this.currentPlayer;

        clonedModel.possibleMoves = new HashSet<>();
        clonedModel.possibleMoves.addAll(this.possibleMoves);

        return clonedModel;
    }

    /**
     * a getter for possibleMoves
     * @see ReversiModel#possibleMoves
     * @return the possibleMoves
     */
    public Set<Coordinate> getPossibleMoves() {
        return this.possibleMoves;
    }

    /**
     * a getter for possibleMoves
     * @return the current player
     */
    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * a getter for the board
     * @return the board in its current state
     */
    public int[][] getBoard() {
        return this.board;
    }
}