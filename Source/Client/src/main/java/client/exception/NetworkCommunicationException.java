package client.exception;

public class NetworkCommunicationException extends ClientException {

    public NetworkCommunicationException(String message) {
        super(message);
    }

    public NetworkCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}