package client.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ClientExceptionTest {

    @Test
    void clientException_withMessage_storesMessage() {
        ClientException exception = new ClientException("Client failed.");

        assertEquals("Client failed.", exception.getMessage());
    }

    @Test
    void clientException_withMessageAndCause_storesMessageAndCause() {
        RuntimeException cause = new RuntimeException("Original cause.");

        ClientException exception = new ClientException("Client failed.", cause);

        assertEquals("Client failed.", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void networkCommunicationException_isClientException() {
        NetworkCommunicationException exception =
                new NetworkCommunicationException("Network failed.");

        assertInstanceOf(ClientException.class, exception);
        assertEquals("Network failed.", exception.getMessage());
    }

    @Test
    void networkCommunicationException_withCause_storesCause() {
        RuntimeException cause = new RuntimeException("Server not reachable.");

        NetworkCommunicationException exception =
                new NetworkCommunicationException("Network failed.", cause);

        assertEquals("Network failed.", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void invalidStartParameterException_isRuntimeException() {
        InvalidStartParameterException exception =
                new InvalidStartParameterException("Invalid start parameters.");

        assertInstanceOf(RuntimeException.class, exception);
        assertEquals("Invalid start parameters.", exception.getMessage());
    }

    @Test
    void invalidGameStateException_isRuntimeException() {
        InvalidGameStateException exception =
                new InvalidGameStateException("Invalid game state.");

        assertInstanceOf(RuntimeException.class, exception);
        assertEquals("Invalid game state.", exception.getMessage());
    }

    @Test
    void noReachablePathException_isRuntimeException() {
        NoReachablePathException exception =
                new NoReachablePathException("No reachable path.");

        assertInstanceOf(RuntimeException.class, exception);
        assertEquals("No reachable path.", exception.getMessage());
    }

    @Test
    void uncheckedExceptions_withCause_storeCause() {
        RuntimeException cause = new RuntimeException("Original cause.");

        InvalidGameStateException exception =
                new InvalidGameStateException("Invalid game state.", cause);

        assertEquals("Invalid game state.", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}