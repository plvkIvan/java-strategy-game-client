package client.exception;

public class NoReachablePathException extends RuntimeException {

    public NoReachablePathException(String message) {
        super(message);
    }

    public NoReachablePathException(String message, Throwable cause) {
        super(message, cause);
    }
}