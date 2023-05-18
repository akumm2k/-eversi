package org.reversi.web.controller.dto;


/**
 * Record wrapper the Connect request by player2.
 * @see org.reversi.web.controller.GameController
 */
public record ConnectRequest(Client client, String gameId) { }
