package client.exception;

import java.io.Serial;

public class InvalidGameStateException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidGameStateException(String message) {
        super(message);
    }

    public InvalidGameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}