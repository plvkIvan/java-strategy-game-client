package client.app;

import client.config.ClientConfig;
import client.converter.HalfMapConverter;
import client.domain.GameHalfMap;
import client.logic.HalfMapGenerator;
import client.logic.MovePlanner;
import client.logic.PathFinder;
import client.logic.mapgeneration.CastlePlacementTarget;
import client.network.NetworkService;
import client.state.GameStateAnalyzer;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientApplication {
    private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);

    private final ClientConfig config;
    private final GameStateAnalyzer gameStateAnalyzer;

    public ClientApplication(ClientConfig config) {
        this.config = config;
        this.gameStateAnalyzer = new GameStateAnalyzer();
    }

    public void run() {
        logger.info("Client application started for game id {}", config.getGameId());

        NetworkService networkService = new NetworkService(
                config.getServerBaseUrl(),
                config.getGameId()
        );

        UniquePlayerIdentifier ownClientPlayerID = registerPlayer(networkService);

        GameState gameStateBeforeMapUpload = waitUntilMustAct(
                networkService,
                ownClientPlayerID
        );

        generateAndSendHalfMap(
                networkService,
                ownClientPlayerID,
                gameStateBeforeMapUpload
        );

        PathFinder pathFinder = new PathFinder();
        MovePlanner movePlanner = new MovePlanner(pathFinder);

        GameLoop gameLoop = new GameLoop(
                networkService,
                ownClientPlayerID,
                gameStateAnalyzer,
                pathFinder,
                movePlanner
        );

        logger.info("Starting game loop.");
        gameLoop.run();
    }

    private UniquePlayerIdentifier registerPlayer(NetworkService networkService) {
        logger.info("Registering player.");

        UniquePlayerIdentifier ownClientPlayerID = networkService.registerPlayer(
                "Ivan",
                "Polovko",
                "polovkoi98"
        );

        logger.info("Player registered successfully with id {}.", ownClientPlayerID);
        System.out.println("Registration successful. Player ID: " + ownClientPlayerID);

        return ownClientPlayerID;
    }

    private GameState waitUntilMustAct(NetworkService networkService,
                                       UniquePlayerIdentifier ownClientPlayerID) {
        logger.info("Waiting until player must act before half map upload.");

        while (true) {
            GameState currentGameState = networkService.getGameState(ownClientPlayerID);
            PlayerState ownPlayerState = gameStateAnalyzer.findOwnPlayerState(
                    currentGameState,
                    ownClientPlayerID
            );

            if (ownPlayerState != null && ownPlayerState.getState() == EPlayerGameState.MustAct) {
                logger.info("Player is allowed to act. Half map upload can start.");
                return currentGameState;
            }

            if (ownPlayerState != null && ownPlayerState.getState() == EPlayerGameState.Won) {
                logger.warn("Player already won before half map upload.");
                return currentGameState;
            }

            if (ownPlayerState != null && ownPlayerState.getState() == EPlayerGameState.Lost) {
                logger.warn("Player already lost before half map upload.");
                return currentGameState;
            }

            sleep();
        }
    }

    private void generateAndSendHalfMap(NetworkService networkService,
                                        UniquePlayerIdentifier ownClientPlayerID,
                                        GameState gameStateBeforeMapUpload) {
        CastlePlacementTarget castlePlacementTarget =
                gameStateAnalyzer.determineCastlePlacementTarget(gameStateBeforeMapUpload);

        logger.info("Generating half map with castle placement target {}.", castlePlacementTarget);

        HalfMapGenerator halfMapGenerator = new HalfMapGenerator();
        GameHalfMap gameHalfMap = halfMapGenerator.generate(castlePlacementTarget);

        HalfMapConverter halfMapConverter = new HalfMapConverter();
        PlayerHalfMap playerHalfMap = halfMapConverter.toDto(gameHalfMap, ownClientPlayerID);

        logger.info("Sending generated half map to server.");
        networkService.sendHalfMap(playerHalfMap);

        logger.info("Half map sent successfully.");
        System.out.println("Half map sent successfully");
    }

    private void sleep() {
        try {
            Thread.sleep(GameLoop.POLL_INTERVAL_MS);
        } catch (InterruptedException e) {
            logger.warn("Sleep interrupted while waiting before half map upload.");
            Thread.currentThread().interrupt();
        }
    }
}