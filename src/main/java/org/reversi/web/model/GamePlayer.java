package org.reversi.web.model;

/**
 * Wrapper for the Game player in {@link ReversiGame}.
 */
public record GamePlayer(String login, Disk disk) {
    /**
     * Gets the GamePlayer clone.
     *
     * @return the clone
     */
    GamePlayer getClone() {
        return new GamePlayer(login, disk);
    }
}
