package org.reversi.cli;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;
import java.util.stream.IntStream;


/**
 * A class for the AI agent for Reversi
 */
public class ReversiAgent {
    /**
     * the max depth for minimax search
     */
    private final int depth;

    /**
     * the game model the agent will play on
     */
    private final ReversiModel model;

    /**
     * the player ID the agent takes
     * @see ReversiModel#PLAYER1
     * @see ReversiModel#PLAYER2
     */
    private final int agentID;


    /**
     * Constructor for the agent
     * @see ReversiAgent#agentID
     * @param model the game model the agent will play on
     * @param depth the max depth for minimax search
     * @param agentID the player ID the agent takes
     */
    public ReversiAgent(ReversiModel model, int depth, int agentID) {
        assert agentID == 1 || agentID == -1;

        this.agentID = agentID;
        this.model = model;
        this.depth = depth;
    }

    /**
     * finds the best move using minimax
     * @return the best move coordinate
     */
    public Coordinate findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        Coordinate bestMove = null;
        Set<Coordinate> moves = this.model.getPossibleMoves();

        final int alpha = Integer.MIN_VALUE;
        final int beta = Integer.MAX_VALUE;

        ReversiModel clonedModel = this.model.getClone();
        for (Coordinate move : moves) {
            clonedModel.makeMove(move.x(), move.y());
            int score = miniMax(clonedModel, this.depth - 1, false, alpha, beta);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        assert Objects.nonNull(bestMove);
        return bestMove;
    }

    /**
     * The minimax implementation
     * @param gameState Reversi model representing the game state
     * @param currDepth current depth in the minimax search
     * @param maximizingPlayer boolean to represent if it's the maximizing player's turn
     * @param alpha the alpha threshold for alpha-beta pruning
     * @param beta the beta threshold for alpha-beta pruning
     * @return the score associated with the best branch in the minimax search
     */
    private int miniMax(ReversiModel gameState, int currDepth, boolean maximizingPlayer, int alpha, int beta) {
        if (currDepth == 0 || gameState.isGameOver()) {
            return evaluate(gameState);
        }

        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Set<Coordinate> moves = gameState.getPossibleMoves();

        for (Coordinate move : moves) {
            gameState.makeMove(move.x(), move.y());
            int score = miniMax(gameState, currDepth - 1, !maximizingPlayer, alpha, beta);
            bestScore = maximizingPlayer ? Math.max(bestScore, score): Math.min(bestScore, score);

            if (maximizingPlayer) {
                alpha = Math.max(alpha, bestScore);
            } else {
                beta = Math.min(beta, bestScore);
            }

            if (maximizingPlayer && beta <= alpha || !maximizingPlayer && alpha <= beta) {
                break;
            }
        }

        return bestScore;
    }

    /**
     * heuristics evaluation of the game state
     * @param gameState Reversi model representing the game state
     * @return the heuristic evaluation
     */
    private int evaluate(final ReversiModel gameState) {
        return countMyCorners(gameState) + countMyPieces(gameState) + endToEndStableOccupation(gameState);
    }


    /**
     * counts the rows and cols stably - cannot be recaptured - occupied end to end by the agent
     * @param gameState Reversi model representing the game state
     * @return the heuristic evaluation
     */
    private int endToEndStableOccupation(final ReversiModel gameState) {
        final int startVal = 100;
        final int[][] board = gameState.getBoard();

        // count occupied rows
        final CompletableFuture<Integer> numFullRowsFuture = CompletableFuture.supplyAsync(() ->
                (int) Arrays.stream(board)
                .filter(row ->
                        IntStream.of(row).allMatch(playa -> playa == this.agentID)
                ).count());

        // count occupied columns
        final IntFunction<IntStream> toColumnStream = col -> Arrays.stream(board).mapToInt(row -> row[col]);
        final CompletableFuture<Integer> numFullColsFuture = CompletableFuture.supplyAsync(() ->
                (int) IntStream.range(0, board.length)
                .mapToObj(toColumnStream)
                .filter(col ->
                        col.allMatch(playa -> playa == this.agentID)
                ).count());


        final CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                numFullRowsFuture, numFullColsFuture
        );
        allFutures.join();

        return startVal * (numFullRowsFuture.join() + numFullColsFuture.join());
    }


    /**
     * counts the number of corners captured by the agent as the corners are valuable.
     * @param gameState Reversi model representing the game state
     * @return the heuristic evaluation
     */
    private int countMyCorners(final ReversiModel gameState) {
        final int stratVal = 50;

        int cnt = 0;
        for (int i: new int[] {0, gameState.getBoard().length - 1}) {
            for (int j : new int[] {0, gameState.getBoard().length - 1}) {
                if (gameState.getBoard()[i][j] == agentID) {
                    cnt++;
                }
            }
        }

        return stratVal * cnt;
    }

    /**
     * counts the number of agent disks.
     * @param gameState Reversi model representing the game state
     * @return the heuristic evaluation
     */
    private int countMyPieces(final ReversiModel gameState) {
        final int stratVal = 5;
        int cnt = 0;

        for (int[] row: gameState.getBoard()) {
            for (int playa: row) {
                if (playa == agentID) {
                    cnt++;
                }
            }
        }

        return stratVal * cnt;
    }

    /**
     * getter for agentID
     * @see ReversiAgent#agentID
     * @return agentID
     */
    public int getAgentID() {
        return this.agentID;
    }
}
