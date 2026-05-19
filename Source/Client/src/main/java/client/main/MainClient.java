package client.main;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import client.app.ClientApplication;
import client.config.ClientConfig;

public class MainClient {

	public static void main(String[] args) {
		System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
		System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

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