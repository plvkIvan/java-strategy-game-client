package client.main;

import client.app.ClientApplication;
import client.config.ClientConfig;

public class MainClient {

	public static void main(String[] args) {
		ClientConfig config = ClientConfig.fromArgs(args);

		if (!config.isValid()) {
			System.err.println("Not enough start parameters.");
			System.err.println("Expected: <mode> <serverBaseUrl> <gameId>");
			return;
		}

		ClientApplication application = new ClientApplication(config);
		application.run();
	}
}