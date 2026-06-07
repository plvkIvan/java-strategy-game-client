package client.exception;

import java.io.Serial;

public class NoReachablePathException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NoReachablePathException(String message) {
        super(message);
    }

    public NoReachablePathException(String message, Throwable cause) {
        super(message, cause);
    }
}