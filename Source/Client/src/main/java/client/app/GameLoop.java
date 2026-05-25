package client.app;

import java.util.HashSet;
import java.util.Set;

import client.converter.GameStateConverter;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.exception.NetworkCommunicationException;
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
import messagesbase.messagesfromclient.EMove;
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

            if (ownPlayerState.getState() == EPlayerGameState.Won) {
                gameUiController.update(
                        gameSnapshot,
                        lastKnownTreasurePosition,
                        ownPlayerState.hasCollectedTreasure(),
                        true,
                        false,
                        turnCounter
                );

                logger.info("Game finished with result WON after {} turns.", turnCounter);
                System.out.println("Game won.");
                return;
            }

            if (ownPlayerState.getState() == EPlayerGameState.Lost) {
                gameUiController.update(
                        gameSnapshot,
                        lastKnownTreasurePosition,
                        ownPlayerState.hasCollectedTreasure(),
                        false,
                        true,
                        turnCounter
                );

                logger.info("Game finished with result LOST after {} turns.", turnCounter);
                System.out.println("Game lost.");
                return;
            }

            if (ownHalfPositions == null) {
                ownHalfPositions = gameStateAnalyzer.determineOwnHalf(gameState);

                if (ownHalfPositions == null) {
                    logger.debug("Own half could not be determined yet.");
                    continue;
                }

                logger.info("Own half determined with {} positions.", ownHalfPositions.size());
                treasureSearch = new TreasureSearch(ownHalfPositions);
                castleSearch = new CastleSearch(ownHalfPositions);
            }

            if (ownPlayerState.getState() == EPlayerGameState.MustWait) {
                logger.debug("Player must wait.");
                continue;
            }

            turnCounter++;

            gameUiController.update(
                    gameSnapshot,
                    lastKnownTreasurePosition,
                    ownPlayerState.hasCollectedTreasure(),
                    false,
                    false,
                    turnCounter
            );

            if (currentPosition == null) {
                logger.warn("Current player position could not be determined.");
                continue;
            }

            visitedPositions.add(currentPosition);
            gameStateAnalyzer.expandMountainVision(gameState, currentPosition, visitedPositions);

            if (!printedMilestone && visitedPositions.size() >= TARGET_VISITED_FIELDS) {
                logger.info("Visited field milestone reached: {} different fields.", TARGET_VISITED_FIELDS);
                System.out.println("Milestone reached: 16 different fields visited.");
                printedMilestone = true;
            }

            Position targetPosition;

            if (ownPlayerState.hasCollectedTreasure()) {
                targetPosition = castleSearch.chooseTarget(
                        gameSnapshot,
                        currentPosition,
                        visitedPositions,
                        pathFinder
                );
            } else {
                targetPosition = treasureSearch.chooseTarget(
                        gameSnapshot,
                        currentPosition,
                        visitedPositions,
                        pathFinder
                );
            }

            if (targetPosition == null || targetPosition.equals(currentPosition)) {
                logger.debug("No useful target found for current position {}.", currentPosition);
                continue;
            }

            EMove move = movePlanner.planNextMove(gameSnapshot, currentPosition, targetPosition);

            if (move == null) {
                logger.warn("Move planner could not determine a move from {} to {}.",
                        currentPosition,
                        targetPosition);
                continue;
            }

            logger.debug("Sending move {} from {} towards target {}.",
                    move,
                    currentPosition,
                    targetPosition);

            networkService.sendMove(ownClientPlayerID, move);
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
        } catch (InterruptedException e) {
            logger.warn("Game loop sleep was interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}