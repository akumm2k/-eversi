package org.reversi.web.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Reversi game model
 */
@Data
public class ReversiGame {
    private String gameId;
    private GamePlayer gamePlayer1;
    private GamePlayer gamePlayer2;
    private GameStatus status;
    private int[][] board;
    private GamePlayer winner;
    private int size;

    /**
     * set of possible moves given the current game state
     */
    private Set<Coordinate> possibleMoves;

    /**
     * the player, whose turn it is to make a move
     */
    public GamePlayer currentGamePlayer;

    /**
     * code for an empty / unoccupied tile in the board
     */
    public final static int EMPTY = 0;

    /**
     * code for representing the result of a draw
     */
    public static final GamePlayer DRAW = null;

    /**
     * Sets board.
     *
     * @param size the size
     */
    public void setBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
        final int mid_lo = size / 2 - 1;
        final int mid_hi = mid_lo + 1;

        this.board[mid_lo][mid_lo] = Disk.WHITE.getValue();
        this.board[mid_hi][mid_hi] = Disk.WHITE.getValue();
        this.board[mid_lo][mid_hi] = Disk.BLACK.getValue();
        this.board[mid_hi][mid_lo] = Disk.BLACK.getValue();
    }

    /**
     * switches the player turn in the game
     */
    private void switchTurn() {
        this.currentGamePlayer = this.currentGamePlayer.equals(gamePlayer1) ? gamePlayer2 : gamePlayer1;
    }

    /**
     * helper for adding all the possible moves from a given position in the board
     * @param possibleCoords the set to add the new detected possible moves to
     * @param opponentDisk the opponent in the current game state
     * @param row the row position of the current player
     * @param col the column position of the current player
     */
    private void addPossibleMovesFrom(final int row, final int col, final Disk opponentDisk,
                                      final Set<Coordinate> possibleCoords) {

        for (int xDir = -1; xDir <= 1; xDir++) {
            for (int yDir = -1; yDir <= 1; yDir++) {
                if (xDir == 0 && yDir == 0) {continue;}
                int x = row + xDir;
                int y = col + yDir;

                boolean separated = false;
                while (this.insideBoard(x, y) && this.board[x][y] == opponentDisk.getValue()) {
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
     * @param gamePlayer assumed to be the "current" player in the context of obtaining the move possibilities
     *               not to be confused with this.currentPlayer
     * @return all possible moves for the player in the current game state
     */
    private Set<Coordinate> getPossibleMoveCoordsForPlayer(final GamePlayer gamePlayer) {
        final Set<Coordinate> possibleCoords = new HashSet<>();
        final Disk opponentDisk = (gamePlayer.disk().equals(Disk.WHITE)) ? Disk.BLACK : Disk.WHITE;

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (this.board[i][j] == gamePlayer.disk().getValue())
                    this.addPossibleMovesFrom(i, j, opponentDisk, possibleCoords);

        return possibleCoords;
    }

    /**
     * Sets possible moves for the given gamePlayer.
     *
     * @param gamePlayer the game player
     */
    public void setPossibleMovesFor(final GamePlayer gamePlayer) {
        this.possibleMoves = this.getPossibleMoveCoordsForPlayer(gamePlayer);
    }

    /**
     *
     * @param row row coordinate
     * @param col column coordinate
     * @return true iff (row, col) is a coordinate inside the board
     */
    private boolean insideBoard(final int row, final int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
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
                          final GamePlayer opponent) {
        int x = row + xDir;
        int y = col + yDir;

        if (this.insideBoard(x, y) && this.board[x][y] == opponent.disk().getValue()) {
            x += xDir;
            y += yDir;

            while (this.insideBoard(x, y) && this.board[x][y] == opponent.disk().getValue()) {
                x += xDir;
                y += yDir;
            }

            if (this.insideBoard(x, y) && this.board[x][y] == this.currentGamePlayer.disk().getValue()) {
                x -= xDir;
                y -= yDir;

                // take all the opponent's disks
                while (this.insideBoard(x, y) && this.board[x][y] != this.currentGamePlayer.disk().getValue()) {
                    this.board[x][y] = this.currentGamePlayer.disk().getValue();
                    x -= xDir;
                    y -= yDir;
                }
            }
        }
    }

    /**
     * method to make a move, altering the game state
     *
     * @param row 1st coordinate of the requested move to be made
     * @param col 2nd coordinate of the requested move to be made
     * @return true iff the move was valid
     */
    public boolean makeMove(final int row, final int col) {
        if (!this.possibleMoves.contains(new Coordinate(row, col))) {
            return false;
        }

        // mark move
        this.board[row][col] = this.currentGamePlayer.disk().getValue();

        // steal all of opponent's disks
        final GamePlayer opponent = (this.currentGamePlayer == gamePlayer1) ? gamePlayer2 : gamePlayer1;

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
            nextPossibleMoves = this.getPossibleMoveCoordsForPlayer(this.currentGamePlayer);
            if (nextPossibleMoves.size() == 0) {
                this.winner = this.getMajorityPlayer();
                this.status = GameStatus.FINISHED;
            }
        }

        this.possibleMoves = nextPossibleMoves;
        return true;
    }

    /**
     * given a player, converts it into an index for some list where player1 is indexed by 0 and player2 by 1
     *
     * @param player the player whose index is requested
     * @return an index of player for some list where player1 is indexed by 0 and player2 by 1
     */
    public static int getPlayerIndex(final int player) {
        // player -> index :: 1 -> 0, 2-> 1
        return player - 1;
    }

    /**
     *
     * @return the player who owns the majority of the disks on the current board
     */
    private GamePlayer getMajorityPlayer() {
        final int[] occupiedTiles = {0, 0};

        for (int[] row: this.board) {
            for (int player: row) {
                occupiedTiles[getPlayerIndex(player)] += 1;
            }
        }

        if (occupiedTiles[0] == occupiedTiles[1]) {
            return DRAW;
        }

        return occupiedTiles[0] > occupiedTiles[1] ? gamePlayer1 : gamePlayer2;
    }

    /**
     * method to check if the game is over
     *
     * @return true iff the game is over
     */
    public boolean isGameOver() {
        return this.status.equals(GameStatus.FINISHED);
    }

    /**
     * getter for winner
     *
     * @return PLAYER1 or PLAYER2 if a player won else DRAW
     * @see ReversiGame#winner ReversiGame#winner
     */
    public GamePlayer getWinner() {
        // Check if one player has won the game
        // Return 1 if player 1 has won, -1 if player 2 has won, 0 if the game is a draw
        return this.winner;
    }
}
