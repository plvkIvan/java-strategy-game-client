package client.exception;

import java.io.Serial;

public class InvalidStartParameterException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidStartParameterException(String message) {
        super(message);
    }

    public InvalidStartParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}