package org.reversi.web.services;

import lombok.AllArgsConstructor;
import org.reversi.web.controller.dto.Client;
import org.reversi.web.controller.dto.Move;
import org.reversi.web.exceptions.InvalidGameException;
import org.reversi.web.exceptions.InvalidParamException;
import org.reversi.web.model.*;
import org.reversi.web.storage.GameStorage;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Game service class effectuating the requests made by {@link org.reversi.web.controller.GameController}.
 */
@Service
@AllArgsConstructor
public class GameService {
    /**
     * Create game reversi game.
     *
     * @param client the client
     * @return the reversi game
     */
    public ReversiGame createGame(Client client) {
        ReversiGame game = new ReversiGame();
        game.setStatus(GameStatus.NEW);
        game.setGameId(UUID.randomUUID().toString());

        // feature: allow client to pick size
        GamePlayer gamePlayer = new GamePlayer(client.login(), Disk.WHITE);
        game.setBoard(4);
        game.setGamePlayer1(gamePlayer);
        game.setPossibleMovesFor(gamePlayer);
        game.setCurrentGamePlayer(gamePlayer);


        GameStorage.getInstance().addGame(game);

        return game;
    }

    /**
     * Connect to a reversi game specified by the gameId.
     *
     * @param client2 the client 2
     * @param gameId  the game id
     * @return the reversi game
     * @throws InvalidParamException the invalid param exception
     * @throws InvalidGameException  the invalid game exception
     */
    public ReversiGame connectToGame(Client client2, String gameId) throws InvalidParamException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("");
        }
        ReversiGame game = GameStorage.getInstance().getGames().get(gameId);
        if (game.getGamePlayer2() != null) {
            throw new InvalidGameException("Game is busy");
        }
        GamePlayer gamePlayer2 = new GamePlayer(client2.login(), Disk.BLACK);
        game.setGamePlayer2(gamePlayer2);
        game.setStatus(GameStatus.IN_PROGRESS);

        GameStorage.getInstance().addGame(game);
        return game;
    }

    /**
     * Connect to random reversi game.
     *
     * @param client2 the client 2
     * @return the reversi game
     * @throws InvalidGameException the invalid game exception
     */
    public ReversiGame connectToRandomGame(Client client2) throws InvalidGameException {
        Optional<ReversiGame> optionalGame = GameStorage.getInstance().getGames().values().stream()
                .filter(game -> game.getStatus().equals(GameStatus.NEW)).findFirst();
        if (optionalGame.isEmpty()) {
            throw new InvalidGameException("No game available");
        }
        ReversiGame game = optionalGame.get();

        GamePlayer gamePlayer2 = new GamePlayer(client2.login(), Disk.BLACK);
        game.setGamePlayer2(gamePlayer2);
        game.setStatus(GameStatus.IN_PROGRESS);

        GameStorage.getInstance().addGame(game);

        return game;
    }

    /**
     * Make the given move in the game.
     *
     * @param move the move
     * @return the reversi game
     * @throws InvalidGameException the invalid game exception
     */
    public ReversiGame move(Move move) throws InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(move.getGameId())) {
            throw new InvalidGameException("The game for the given move doesn't exist");
        }


        ReversiGame game = GameStorage.getInstance().getGames().get(move.getGameId());
        switch (game.getStatus()) {
            case NEW ->
                throw new InvalidGameException("Game hasn't started yet");
            case IN_PROGRESS -> {
                // also checks and sets winner when game over
                if (!game.makeMove(move.getCoord().x(), move.getCoord().y())) {
                    throw new InvalidGameException("invalid move request");
                }
            }
            case FINISHED ->
                throw new InvalidGameException("Game is already over");
        }

        GameStorage.getInstance().addGame(game);
        return game;
    }
}
