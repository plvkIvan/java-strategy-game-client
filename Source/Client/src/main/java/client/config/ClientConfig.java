package client.config;

public class ClientConfig {
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
        if (args == null || args.length < 3) {
            return new ClientConfig(null, null, null, false);
        }

        return new ClientConfig(args[0], args[1], args[2], true);
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