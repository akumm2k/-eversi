package org.reversi.mvc;

import java.util.Objects;
import java.util.Set;


public class ReversiAgent {
    private final int depth;

    private final ReversiModel model;

    private final int agentID;

    public ReversiAgent(ReversiModel model, int depth, int agentID) {
        assert agentID == 1 || agentID == -1;

        this.agentID = agentID;
        this.model = model;
        this.depth = depth;
    }

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

    private int evaluate(final ReversiModel gameState) {
        return countMyPieces(gameState) + countMyCorners(gameState);
    }


    private int countMyCorners(final ReversiModel gameState) {
        final int stratVal = 1;

        int cnt = 0;
        for (int i: new int[] {0, gameState.getBoard().length - 1}) {
            for (int j : new int[]{0, gameState.getBoard().length - 1}) {
                if (gameState.getBoard()[i][j] == agentID) {
                    cnt++;
                }
            }
        }

        return stratVal * cnt;
    }

    private int countMyPieces(final ReversiModel gameState) {
        final int stratVal = 1;
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

    public int getAgentID() {
        return this.agentID;
    }
}
