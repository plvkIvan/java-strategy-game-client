package client.main;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import client.app.ClientApplication;
import client.config.ClientConfig;
import client.exception.ClientException;
import client.exception.InvalidStartParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClient {
	private static final Logger logger = LoggerFactory.getLogger(MainClient.class);

	public static void main(String[] args) {
		configureUtf8ConsoleOutput();

		logger.trace("MainClient received {} start parameter(s).",
				args == null ? 0 : args.length);

		try {
			runClient(args);
		} catch (InvalidStartParameterException exception) {
			logger.error("Client could not start because of invalid start parameters.", exception);
			System.err.println(exception.getMessage());
		} catch (ClientException exception) {
			logger.error("Client stopped because of a recoverable client error.", exception);
			System.err.println("Client error: " + exception.getMessage());
		} catch (RuntimeException exception) {
			logger.error("Client stopped because of an unexpected error.", exception);
			System.err.println("Unexpected client error: " + exception.getMessage());
		}
	}

	private static void runClient(String[] args) throws ClientException {
		ClientConfig config = ClientConfig.fromArgs(args);

		logger.trace("Start parameters parsed. Mode: {}, serverBaseUrl: {}, gameId: {}.",
				config.getMode(),
				config.getServerBaseUrl(),
				config.getGameId());

		if (!config.isValid()) {
			throw new InvalidStartParameterException(
					"Invalid start parameters. Expected: TR <serverBaseUrl> <gameId>."
			);
		}

		ClientApplication application = new ClientApplication(config);
		application.run();
	}

	private static void configureUtf8ConsoleOutput() {
		try {
			System.setOut(new PrintStream(
					new FileOutputStream(FileDescriptor.out),
					true,
					StandardCharsets.UTF_8
			));

			System.setErr(new PrintStream(
					new FileOutputStream(FileDescriptor.err),
					true,
					StandardCharsets.UTF_8
			));
		} catch (RuntimeException exception) {
			logger.warn("Could not configure UTF-8 console output.", exception);
		}
	}
}