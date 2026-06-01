package client.config;

public class ClientConfig {
    private static final int MIN_REQUIRED_ARGUMENTS = 3;
    private static final String TOURNAMENT_MODE = "TR";

    private final String mode;
    private final String serverBaseUrl;
    private final String gameId;
    private final boolean valid;

    private ClientConfig(String mode, String serverBaseUrl, String gameId, boolean valid) {
        this.mode = mode;
        this.serverBaseUrl = serverBaseUrl;
        this.gameId = gameId;
        this.valid = valid;
    }

    public static ClientConfig fromArgs(String[] args) {
        if (args == null || args.length < MIN_REQUIRED_ARGUMENTS) {
            return new ClientConfig(null, null, null, false);
        }

        String mode = args[0];
        String serverBaseUrl = args[1];
        String gameId = args[2];

        boolean valid = TOURNAMENT_MODE.equals(mode)
                && serverBaseUrl != null
                && !serverBaseUrl.isBlank()
                && gameId != null
                && !gameId.isBlank();

        return new ClientConfig(mode, serverBaseUrl, gameId, valid);
    }

    public String getMode() {
        return mode;
    }

    public String getServerBaseUrl() {
        return serverBaseUrl;
    }

    public String getGameId() {
        return gameId;
    }

    public boolean isValid() {
        return valid;
    }
}