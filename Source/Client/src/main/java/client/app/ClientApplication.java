package client.app;

import client.config.ClientConfig;
import client.converter.HalfMapConverter;
import client.domain.GameHalfMap;
import client.logic.HalfMapGenerator;
import client.logic.MovePlanner;
import client.logic.PathFinder;
import client.network.NetworkService;
import client.state.GameStateAnalyzer;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class ClientApplication {
    private final ClientConfig config;
    private final GameStateAnalyzer gameStateAnalyzer;

    public ClientApplication(ClientConfig config) {
        this.config = config;
        this.gameStateAnalyzer = new GameStateAnalyzer();
    }

    public void run() {
        NetworkService networkService = new NetworkService(
                config.getServerBaseUrl(),
                config.getGameId()
        );

        UniquePlayerIdentifier ownClientPlayerID = registerPlayer(networkService);

        waitUntilMustAct(networkService, ownClientPlayerID);

        generateAndSendHalfMap(networkService, ownClientPlayerID);

        PathFinder pathFinder = new PathFinder();
        MovePlanner movePlanner = new MovePlanner(pathFinder);

        GameLoop gameLoop = new GameLoop(
                networkService,
                ownClientPlayerID,
                gameStateAnalyzer,
                pathFinder,
                movePlanner
        );

        gameLoop.run();
    }

    private UniquePlayerIdentifier registerPlayer(NetworkService networkService) {
        UniquePlayerIdentifier ownClientPlayerID = networkService.registerPlayer(
                "Ivan",
                "Polovko",
                "polovkoi98"
        );

        System.out.println("Registration successful. Player ID: " + ownClientPlayerID);
        return ownClientPlayerID;
    }

    private void waitUntilMustAct(NetworkService networkService,
                                  UniquePlayerIdentifier ownClientPlayerID) {
        while (true) {
            GameState currentGameState = networkService.getGameState(ownClientPlayerID);
            PlayerState ownPlayerState = gameStateAnalyzer.findOwnPlayerState(
                    currentGameState,
                    ownClientPlayerID
            );

            if (ownPlayerState != null && ownPlayerState.getState() == EPlayerGameState.MustAct) {
                return;
            }

            if (ownPlayerState != null && ownPlayerState.getState() == EPlayerGameState.Won) {
                return;
            }

            if (ownPlayerState != null && ownPlayerState.getState() == EPlayerGameState.Lost) {
                return;
            }

            sleep();
        }
    }

    private void generateAndSendHalfMap(NetworkService networkService,
                                        UniquePlayerIdentifier ownClientPlayerID) {
        HalfMapGenerator halfMapGenerator = new HalfMapGenerator();
        GameHalfMap gameHalfMap = halfMapGenerator.generate();

        HalfMapConverter halfMapConverter = new HalfMapConverter();
        PlayerHalfMap playerHalfMap = halfMapConverter.toDto(gameHalfMap, ownClientPlayerID);

        networkService.sendHalfMap(playerHalfMap);
        System.out.println("Half map sent successfully");
    }

    private void sleep() {
        try {
            Thread.sleep(GameLoop.POLL_INTERVAL_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}