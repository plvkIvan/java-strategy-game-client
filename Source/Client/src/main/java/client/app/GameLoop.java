package client.app;

import java.util.HashSet;
import java.util.Set;

import client.converter.GameStateConverter;
import client.converter.MoveConverter;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.MoveDirection;
import client.domain.Position;
import client.exception.NetworkCommunicationException;
import client.exception.NoReachablePathException;
import client.logic.CastleSearch;
import client.logic.MovePlanner;
import client.logic.PathFinder;
import client.logic.TreasureSearch;
import client.network.NetworkService;
import client.state.GameStateAnalyzer;
import client.ui.controller.GameUiController;
import client.ui.model.GameUiModel;
import client.ui.view.CliGameView;
import client.ui.view.LegendRenderer;
import client.ui.view.MapRenderer;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLoop {
    private static final Logger logger = LoggerFactory.getLogger(GameLoop.class);

    public static final long POLL_INTERVAL_MS = 500;
    private static final int TARGET_VISITED_FIELDS = 16;

    private final NetworkService networkService;
    private final UniquePlayerIdentifier ownClientPlayerID;
    private final GameStateAnalyzer gameStateAnalyzer;
    private final GameStateConverter gameStateConverter;
    private final MoveConverter moveConverter;
    private final PathFinder pathFinder;
    private final MovePlanner movePlanner;
    private final GameUiController gameUiController;

    private Set<Position> ownHalfPositions = null;
    private TreasureSearch treasureSearch = null;
    private CastleSearch castleSearch = null;

    private final Set<Position> visitedPositions = new HashSet<>();
    private boolean printedMilestone = false;
    private int turnCounter = 0;
    private Position lastKnownTreasurePosition = null;

    public GameLoop(NetworkService networkService,
                    UniquePlayerIdentifier ownClientPlayerID,
                    GameStateAnalyzer gameStateAnalyzer,
                    PathFinder pathFinder,
                    MovePlanner movePlanner) {
        this.networkService = networkService;
        this.ownClientPlayerID = ownClientPlayerID;
        this.gameStateAnalyzer = gameStateAnalyzer;
        this.gameStateConverter = new GameStateConverter();
        this.moveConverter = new MoveConverter();
        this.pathFinder = pathFinder;
        this.movePlanner = movePlanner;
        this.gameUiController = createGameUiController();
    }

    public void run() throws NetworkCommunicationException {
        logger.info("Game loop started.");

        while (true) {
            sleep();

            GameState gameState = networkService.getGameState(ownClientPlayerID);
            PlayerState ownPlayerState = gameStateAnalyzer.findOwnPlayerState(
                    gameState,
                    ownClientPlayerID
            );

            if (ownPlayerState == null) {
                logger.warn("Own player state was not found in current game state.");
                continue;
            }

            GameSnapshot gameSnapshot = gameStateConverter.toDomain(gameState);
            Position currentPosition = gameSnapshot.getFullGameMap().findMyPlayerPosition();

            updateLastKnownTreasurePosition(gameSnapshot, ownPlayerState, currentPosition);

            if (handleTerminalState(gameSnapshot, ownPlayerState)) {
                return;
            }

            if (!initializeOwnHalfIfNeeded(gameState)) {
                continue;
            }

            if (ownPlayerState.getState() == EPlayerGameState.MustWait) {
                logger.debug("Player must wait.");
                continue;
            }

            handleMustActTurn(gameState, gameSnapshot, ownPlayerState, currentPosition);
        }
    }

    private boolean handleTerminalState(GameSnapshot gameSnapshot, PlayerState ownPlayerState) {
        if (ownPlayerState.getState() == EPlayerGameState.Won) {
            updateUi(gameSnapshot, ownPlayerState, true, false);
            logger.info("Game finished with result WON after {} turns.", turnCounter);
            return true;
        }

        if (ownPlayerState.getState() == EPlayerGameState.Lost) {
            updateUi(gameSnapshot, ownPlayerState, false, true);
            logger.info("Game finished with result LOST after {} turns.", turnCounter);
            return true;
        }

        return false;
    }

    private boolean initializeOwnHalfIfNeeded(GameState gameState) {
        if (ownHalfPositions != null) {
            return true;
        }

        ownHalfPositions = gameStateAnalyzer.determineOwnHalf(gameState);

        if (ownHalfPositions == null) {
            logger.debug("Own half could not be determined yet.");
            return false;
        }

        logger.info("Own half determined with {} positions.", ownHalfPositions.size());
        treasureSearch = new TreasureSearch(ownHalfPositions);
        castleSearch = new CastleSearch(ownHalfPositions);
        return true;
    }

    private void handleMustActTurn(GameState gameState,
                                   GameSnapshot gameSnapshot,
                                   PlayerState ownPlayerState,
                                   Position currentPosition)
            throws NetworkCommunicationException {
        turnCounter++;
        updateUi(gameSnapshot, ownPlayerState, false, false);

        if (currentPosition == null) {
            logger.warn("Current player position could not be determined.");
            return;
        }

        updateVisitedPositions(gameState, currentPosition);

        Position targetPosition = chooseTarget(gameSnapshot, ownPlayerState, currentPosition);

        if (targetPosition == null || targetPosition.equals(currentPosition)) {
            logger.debug("No useful target found for current position {}.", currentPosition);
            return;
        }

        sendNextMove(gameSnapshot, currentPosition, targetPosition);
    }

    private void updateUi(GameSnapshot gameSnapshot,
                          PlayerState ownPlayerState,
                          boolean gameWon,
                          boolean gameLost) {
        gameUiController.update(
                gameSnapshot,
                lastKnownTreasurePosition,
                ownPlayerState.hasCollectedTreasure(),
                gameWon,
                gameLost,
                turnCounter
        );
    }

    private void updateVisitedPositions(GameState gameState, Position currentPosition) {
        visitedPositions.add(currentPosition);
        gameStateAnalyzer.expandMountainVision(gameState, currentPosition, visitedPositions);

        if (!printedMilestone && visitedPositions.size() >= TARGET_VISITED_FIELDS) {
            logger.info("Visited field milestone reached: {} different fields.", TARGET_VISITED_FIELDS);
            printedMilestone = true;
        }
    }

    private Position chooseTarget(GameSnapshot gameSnapshot,
                                  PlayerState ownPlayerState,
                                  Position currentPosition) {
        if (ownPlayerState.hasCollectedTreasure()) {
            return castleSearch.chooseTarget(
                    gameSnapshot,
                    currentPosition,
                    visitedPositions,
                    pathFinder
            );
        }

        return treasureSearch.chooseTarget(
                gameSnapshot,
                currentPosition,
                visitedPositions,
                pathFinder
        );
    }

    private void sendNextMove(GameSnapshot gameSnapshot,
                              Position currentPosition,
                              Position targetPosition)
            throws NetworkCommunicationException {
        try {
            MoveDirection moveDirection = movePlanner.planNextMove(
                    gameSnapshot,
                    currentPosition,
                    targetPosition
            );

            logger.debug("Sending move {} from {} towards target {}.",
                    moveDirection,
                    currentPosition,
                    targetPosition);

            networkService.sendMove(ownClientPlayerID, moveConverter.toDto(moveDirection));
        } catch (NoReachablePathException exception) {
            logger.warn("Move could not be planned.", exception);
        }
    }

    private void updateLastKnownTreasurePosition(GameSnapshot gameSnapshot,
                                                 PlayerState ownPlayerState,
                                                 Position currentPosition) {
        Position visibleTreasurePosition = findVisibleTreasurePosition(gameSnapshot);

        if (visibleTreasurePosition != null) {
            lastKnownTreasurePosition = visibleTreasurePosition;
            logger.debug("Visible treasure position updated to {}.", visibleTreasurePosition);
            return;
        }

        if (ownPlayerState.hasCollectedTreasure()
                && lastKnownTreasurePosition == null
                && currentPosition != null) {
            lastKnownTreasurePosition = currentPosition;
            logger.debug("Treasure already collected. Using current position {} as fallback.",
                    currentPosition);
        }
    }

    private Position findVisibleTreasurePosition(GameSnapshot gameSnapshot) {
        for (FullMapNodeInfo node : gameSnapshot.getFullGameMap().getMapNodes()) {
            if (node.isMyTreasurePresent()) {
                return node.getPosition();
            }
        }

        return null;
    }

    private GameUiController createGameUiController() {
        GameUiModel gameUiModel = new GameUiModel();
        MapRenderer mapRenderer = new MapRenderer();
        LegendRenderer legendRenderer = new LegendRenderer();
        CliGameView cliGameView = new CliGameView(mapRenderer, legendRenderer);

        gameUiModel.addGameStateObserver(cliGameView);

        return new GameUiController(gameUiModel);
    }

    private void sleep() {
        try {
            Thread.sleep(POLL_INTERVAL_MS);
        } catch (InterruptedException exception) {
            logger.warn("Game loop sleep was interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}