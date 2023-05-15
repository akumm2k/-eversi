package org.reversi.netcli;

public class GameException extends Exception {
    public GameException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
    @SuppressWarnings("unused")
    public GameException(Throwable cause) {
        super(cause);
    }
}
