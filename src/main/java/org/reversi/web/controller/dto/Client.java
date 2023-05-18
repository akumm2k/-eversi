package org.reversi.web.controller.dto;

/**
 * Record wrapper for the Client used in the Http Requests.
 * @see org.reversi.web.controller.GameController
 */
public record Client (String login) { }
