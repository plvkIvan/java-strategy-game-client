package client.exception;

public class InvalidStartParameterException extends RuntimeException {

    public InvalidStartParameterException(String message) {
        super(message);
    }

    public InvalidStartParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}