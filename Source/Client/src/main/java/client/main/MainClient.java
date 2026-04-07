package client.main;

import java.util.HashSet;
import java.util.Set;

import client.converter.HalfMapConverter;
import client.domain.GameHalfMap;
import client.domain.MapNode;
import client.domain.Position;
import client.logic.CastleSearch;
import client.logic.HalfMapGenerator;
import client.logic.MovePlanner;
import client.logic.PathFinder;
import client.logic.TreasureSearch;
import client.network.NetworkService;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class MainClient {
	private static final long POLL_INTERVAL_MS = 400;
	private static final int TARGET_VISITED_FIELDS = 16;


	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.err.println("Not enough start parameters.");
			System.err.println("Expected: <mode> <serverBaseUrl> <gameId>");
			return;
		}

		String serverBaseUrl = args[1];
		String gameId = args[2];

		NetworkService networkService = new NetworkService(serverBaseUrl, gameId);

		UniquePlayerIdentifier ownClientPlayerID = networkService.registerPlayer(
				"Ivan",
				"Polovko",
				"polovkoi98"
		);

		System.out.println("Registration successful. Player ID: " + ownClientPlayerID);

		waitUntilMustAct(networkService, ownClientPlayerID);

		HalfMapGenerator halfMapGenerator = new HalfMapGenerator();
		GameHalfMap gameHalfMap = halfMapGenerator.generate();

		HalfMapConverter halfMapConverter = new HalfMapConverter();
		PlayerHalfMap playerHalfMap = halfMapConverter.toDto(gameHalfMap, ownClientPlayerID);

		networkService.sendHalfMap(playerHalfMap);
		System.out.println("Half map sent successfully");

		Set<Position> ownHalfPositions = new HashSet<>();
		for (MapNode mapNode : gameHalfMap.getMapNodes()) {
			ownHalfPositions.add(mapNode.getPosition());
		}

		PathFinder pathFinder = new PathFinder();
		MovePlanner movePlanner = new MovePlanner(pathFinder);
		TreasureSearch treasureSearch = new TreasureSearch(ownHalfPositions);
		CastleSearch castleSearch = new CastleSearch(ownHalfPositions);

		Set<Position> visitedPositions = new HashSet<>();
		boolean printedMilestone = false;

		while (true) {
			GameState gameState = networkService.getGameState(ownClientPlayerID);
			PlayerState ownPlayerState = findOwnPlayerState(gameState, ownClientPlayerID);

			if (ownPlayerState == null) {
				sleep();
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

			Position currentPosition = findCurrentPosition(gameState);

			if (currentPosition != null) {
				visitedPositions.add(currentPosition);
			}

			if (!printedMilestone && visitedPositions.size() >= TARGET_VISITED_FIELDS) {
				System.out.println("Milestone reached: 16 different fields visited.");
				printedMilestone = true;
			}

			if (ownPlayerState.getState() == EPlayerGameState.MustWait) {
				sleep();
				continue;
			}

			if (currentPosition == null) {
				sleep();
				continue;
			}

			Position targetPosition;

			if (ownPlayerState.hasCollectedTreasure()) {
				targetPosition = castleSearch.chooseTarget(
						gameState, currentPosition, visitedPositions, pathFinder);
			} else {
				targetPosition = treasureSearch.chooseTarget(
						gameState, currentPosition, visitedPositions, pathFinder);
			}

			if (targetPosition == null || targetPosition.equals(currentPosition)) {
				sleep();
				continue;
			}

			EMove move = movePlanner.planNextMove(gameState, currentPosition, targetPosition);
			if (move == null) {
				sleep();
				continue;
			}

			networkService.sendMove(ownClientPlayerID, move);
			sleep();
		}
	}

	private static void waitUntilMustAct(NetworkService networkService,
										 UniquePlayerIdentifier ownClientPlayerID) {
		while (true) {
			GameState currentGameState = networkService.getGameState(ownClientPlayerID);
			PlayerState ownPlayerState = findOwnPlayerState(currentGameState, ownClientPlayerID);

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

	private static PlayerState findOwnPlayerState(GameState gameState,
												  UniquePlayerIdentifier ownClientPlayerID) {
		for (PlayerState playerState : gameState.getPlayers()) {
			if (playerState.getUniquePlayerID().equals(ownClientPlayerID.getUniquePlayerID())) {
				return playerState;
			}
		}
		return null;
	}

	private static Position findCurrentPosition(GameState gameState) {
		for (var node : gameState.getMap().getMapNodes()) {
			if (node.getPlayerPositionState().representsMyPlayer()) {
				return new Position(node.getX(), node.getY());
			}
		}
		return null;


	private static void sleep() {
		try {
			Thread.sleep(POLL_INTERVAL_MS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}