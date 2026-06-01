package client.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClientConfigTest {

    @Test
    void fromArgs_withValidArguments_createsValidConfig() {
        String[] args = {
                "TR",
                "http://swe1.wst.univie.ac.at:18235",
                "game123"
        };

        ClientConfig config = ClientConfig.fromArgs(args);

        assertTrue(config.isValid());
        assertEquals("TR", config.getMode());
        assertEquals("http://swe1.wst.univie.ac.at:18235", config.getServerBaseUrl());
        assertEquals("game123", config.getGameId());
    }

    @Test
    void fromArgs_withMissingArguments_createsInvalidConfig() {
        String[] args = {
                "TR",
                "http://swe1.wst.univie.ac.at:18235"
        };

        ClientConfig config = ClientConfig.fromArgs(args);

        assertFalse(config.isValid());
    }

    @Test
    void fromArgs_withNullArguments_createsInvalidConfig() {
        ClientConfig config = ClientConfig.fromArgs(null);

        assertFalse(config.isValid());
    }

    @Test
    void fromArgs_withInvalidMode_createsInvalidConfig() {
        String[] args = {
                "DEMO",
                "http://swe1.wst.univie.ac.at:18235",
                "game123"
        };

        ClientConfig config = ClientConfig.fromArgs(args);

        assertFalse(config.isValid());
    }

    @Test
    void fromArgs_withBlankServerBaseUrl_createsInvalidConfig() {
        String[] args = {
                "TR",
                " ",
                "game123"
        };

        ClientConfig config = ClientConfig.fromArgs(args);

        assertFalse(config.isValid());
    }

    @Test
    void fromArgs_withBlankGameId_createsInvalidConfig() {
        String[] args = {
                "TR",
                "http://swe1.wst.univie.ac.at:18235",
                " "
        };

        ClientConfig config = ClientConfig.fromArgs(args);

        assertFalse(config.isValid());
    }
}