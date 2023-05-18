package org.reversi.web.storage;

import org.reversi.web.model.ReversiGame;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton wrapper for the game database.
 */
public class GameStorage {
    private final static Map<String, ReversiGame> GAMES = new HashMap<>();
    private final static GameStorage INSTANCE = new GameStorage();
    private GameStorage() {}

    /**
     * getter for the singleton storage.
     *
     * @return the instance
     */
    public static synchronized GameStorage getInstance() {
        return INSTANCE;
    }

    /**
     * getter for the stored games
     *
     * @return the games
     */
    public Map<String, ReversiGame> getGames() {
        return GAMES;
    }

    /**
     * adds game to the storage
     *
     * @param game the game
     */
    public void addGame(ReversiGame game) {
        GAMES.put(game.getGameId(), game);
    }
}
