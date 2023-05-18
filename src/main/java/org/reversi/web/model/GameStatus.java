package org.reversi.web.model;

/**
 * Wrapper for the Game status used in {@link ReversiGame}.
 */
public enum GameStatus {
    /**
     * New game status.
     */
    NEW,
    /**
     * In progress game status.
     */
    IN_PROGRESS,
    /**
     * Finished game status.
     */
    FINISHED;
}
