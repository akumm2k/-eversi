package org.reversi.web.exceptions;

/**
 * The Invalid game exception class.
 */
public class InvalidGameException extends Exception {
    /**
     * Instantiates a new Invalid game exception.
     *
     * @param msg the msg
     */
    public InvalidGameException(String msg) {
        super(msg);
    }

    /**
     * Instantiates a new Invalid game exception.
     *
     * @param msg   the msg
     * @param cause the cause
     */
    public InvalidGameException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
