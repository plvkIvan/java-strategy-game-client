package client.exception;

import java.io.Serial;

public class NetworkCommunicationException extends ClientException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NetworkCommunicationException(String message) {
        super(message);
    }

    public NetworkCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}