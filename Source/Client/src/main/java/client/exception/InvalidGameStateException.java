package client.exception;

public class InvalidGameStateException extends RuntimeException {

    public InvalidGameStateException(String message) {
        super(message);
    }

    public InvalidGameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}