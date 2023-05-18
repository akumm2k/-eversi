package org.reversi.web.exceptions;

/**
 * The Invalid param exception for Http requests.
 */
public class InvalidParamException extends Exception {
    /**
     * Instantiates a new Invalid param exception.
     *
     * @param msg the msg
     */
    public InvalidParamException(String msg) {
        super(msg);
    }

    /**
     * Instantiates a new Invalid param exception.
     *
     * @param msg   the msg
     * @param cause the cause
     */
    public InvalidParamException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
