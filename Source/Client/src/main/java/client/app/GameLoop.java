package client.app;

import java.util.HashSet;
import java.util.Set;

import client.converter.GameStateConverter;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.logic.CastleSearch;
import client.logic.MovePlanner;
import client.logic.PathFinder;
import client.logic.TreasureSearch;
import client.network.NetworkService;
import client.state.GameStateAnalyzer;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class GameLoop {
    public static final long POLL_INTERVAL_MS = 500;
    private static final int TARGET_VISITED_FIELDS = 16;

    private final NetworkService networkService;
    private final UniquePlayerIdentifier ownClientPlayerID;
    private final GameStateAnalyzer gameStateAnalyzer;
    private final GameStateConverter gameStateConverter;
    private final PathFinder pathFinder;
    private final MovePlanner movePlanner;

    private Set<Position> ownHalfPositions = null;
    private TreasureSearch treasureSearch = null;
    private CastleSearch castleSearch = null;

    private final Set<Position> visitedPositions = new HashSet<>();
    private boolean printedMilestone = false;

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
    }

    public void run() {
        while (true) {
            sleep();

            GameState gameState = networkService.getGameState(ownClientPlayerID);
            PlayerState ownPlayerState = gameStateAnalyzer.findOwnPlayerState(
                    gameState,
                    ownClientPlayerID
            );

            if (ownPlayerState == null) {
                continue;
            }

            if (ownPlayerState.getState() == EPlayerGameState.Won) {
                System.out.println("Game won.");
                return;
            }

            if (ownPlayerState.getState() == EPlayerGameState.Lost) {
                System.out.println("Game lost.");
                return;
            }

            if (ownHalfPositions == null) {
                ownHalfPositions = gameStateAnalyzer.determineOwnHalf(gameState);

                if (ownHalfPositions == null) {
                    continue;
                }

                treasureSearch = new TreasureSearch(ownHalfPositions);
                castleSearch = new CastleSearch(ownHalfPositions);
            }

            if (ownPlayerState.getState() == EPlayerGameState.MustWait) {
                continue;
            }

            GameSnapshot gameSnapshot = gameStateConverter.toDomain(gameState);

            Position currentPosition = gameSnapshot.getFullGameMap().findMyPlayerPosition();

            if (currentPosition == null) {
                continue;
            }

            visitedPositions.add(currentPosition);
            gameStateAnalyzer.expandMountainVision(gameState, currentPosition, visitedPositions);

            if (!printedMilestone && visitedPositions.size() >= TARGET_VISITED_FIELDS) {
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
                continue;
            }

            EMove move = movePlanner.planNextMove(gameSnapshot, currentPosition, targetPosition);

            if (move == null) {
                continue;
            }

            networkService.sendMove(ownClientPlayerID, move);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(POLL_INTERVAL_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}