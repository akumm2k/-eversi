package org.reversi.web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reversi.web.controller.dto.ConnectRequest;
import org.reversi.web.exceptions.InvalidGameException;
import org.reversi.web.exceptions.InvalidParamException;
import org.reversi.web.controller.dto.Client;
import org.reversi.web.model.ReversiGame;
import org.reversi.web.controller.dto.Move;
import org.reversi.web.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * The type Game controller.
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Start response entity.
     *
     * @param client the client
     * @return the response entity
     */
    @PostMapping("/start")
    public ResponseEntity<ReversiGame> start(@RequestBody Client client) {
        log.info("Start game request: {}", client);
        ReversiGame newGame = gameService.createGame(client);
        return ResponseEntity.ok(newGame);
    }

    /**
     * Connect response entity.
     *
     * @param connectRequest the connect request
     * @return the response entity
     * @throws InvalidParamException the invalid param exception
     * @throws InvalidGameException  the invalid game exception
     */
    @PostMapping("/connect")
    public ResponseEntity<ReversiGame> connect(@RequestBody ConnectRequest connectRequest) throws InvalidParamException, InvalidGameException {
        log.info("Connect request: {}", connectRequest);
        ReversiGame connectedGame = gameService.connectToGame(connectRequest.client(), connectRequest.gameId());

        String payloadDestination = "/topic/game-progress/" + connectedGame.getGameId();
        simpMessagingTemplate.convertAndSend(payloadDestination, connectedGame);

        return ResponseEntity.ok(connectedGame);
    }

    /**
     * Connect random response entity.
     *
     * @param client the client
     * @return the response entity
     * @throws InvalidGameException the invalid game exception
     */
    @PostMapping("/connect/random")
    public ResponseEntity<ReversiGame> connectRandom(@RequestBody Client client) throws InvalidGameException {
        log.info("random connect request {}", client);
        ReversiGame connectedGame = gameService.connectToRandomGame(client);

        String payloadDestination = "/topic/game-progress/" + connectedGame.getGameId();
        simpMessagingTemplate.convertAndSend(payloadDestination, connectedGame);

        return ResponseEntity.ok(connectedGame);
    }

    /**
     * Move response entity.
     *
     * @param moveRequest the move request
     * @return the response entity
     * @throws InvalidGameException the invalid game exception
     */
    @PostMapping("/move")
    public ResponseEntity<ReversiGame> move(@RequestBody Move moveRequest) throws InvalidGameException {
        log.info("move: {}", moveRequest);
        ReversiGame gamePayload = gameService.move(moveRequest);

        // we use web sockets to respond to the player making move, and notifying the opponent.
        String payloadDestination = "/topic/game-progress/" + gamePayload.getGameId();
        simpMessagingTemplate.convertAndSend(payloadDestination, gamePayload);

        return ResponseEntity.ok(gamePayload);
    }
}

