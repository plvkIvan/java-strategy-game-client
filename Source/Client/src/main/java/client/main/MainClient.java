package client.main;

import java.util.HashSet;
import java.util.Set;

import client.converter.HalfMapConverter;
import client.domain.GameHalfMap;
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
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.FullMapNode;
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

		Set<Position> ownHalfPositions = waitForFullMap(networkService, ownClientPlayerID);

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
				expandMountainVision(gameState, currentPosition, visitedPositions);
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
		for (FullMapNode node : gameState.getMap().getMapNodes()) {
			if (node.getPlayerPositionState().representsMyPlayer()) {
				return new Position(node.getX(), node.getY());
			}
		}
		return null;
	}

	private static Set<Position> waitForFullMapAndDetermineOwnHalf(
			NetworkService networkService, UniquePlayerIdentifier ownClientPlayerID) {
		while (true) {
			sleep();
			GameState gameState = networkService.getGameState(ownClientPlayerID);

			PlayerState ownPlayerState = findOwnPlayerState(gameState, ownClientPlayerID);
			if (ownPlayerState != null) {
				if (ownPlayerState.getState() == EPlayerGameState.Won
						|| ownPlayerState.getState() == EPlayerGameState.Lost) {
					return new HashSet<>();
				}
			}

			Collection<FullMapNode> mapNodes = gameState.getMap().getMapNodes();
			if (mapNodes.size() < 100) {
				continue;
			}

			Position myFort = null;
			int maxX = 0;
			int maxY = 0;

			for (FullMapNode node : mapNodes) {
				if (node.getX() > maxX) maxX = node.getX();
				if (node.getY() > maxY) maxY = node.getY();
				if (node.getFortState() == EFortState.MyFortPresent) {
					myFort = new Position(node.getX(), node.getY());
				}
			}

			if (myFort == null) {
				continue;
			}

			int mapWidth = maxX + 1;
			int mapHeight = maxY + 1;

			Set<Position> ownHalf = new HashSet<>();

			if (mapWidth > 10) {
				boolean ownIsLeft = myFort.getX() <= 9;
				for (FullMapNode node : mapNodes) {
					if (ownIsLeft && node.getX() <= 9) {
						ownHalf.add(new Position(node.getX(), node.getY()));
					}
					if (!ownIsLeft && node.getX() >= 10) {
						ownHalf.add(new Position(node.getX(), node.getY()));
					}
				}
			} else {
				boolean ownIsTop = myFort.getY() <= 4;
				for (FullMapNode node : mapNodes) {
					if (ownIsTop && node.getY() <= 4) {
						ownHalf.add(new Position(node.getX(), node.getY()));
					}
					if (!ownIsTop && node.getY() >= 5) {
						ownHalf.add(new Position(node.getX(), node.getY()));
					}
				}
			}

			return ownHalf;
		}
	}

	private static void expandMountainVision(GameState gameState, Position currentPosition,
											 Set<Position> visitedPositions) {
		for (FullMapNode node : gameState.getMap().getMapNodes()) {
			if (node.getX() == currentPosition.getX() && node.getY() == currentPosition.getY()) {
				if (node.getTerrain() == ETerrain.Mountain) {
					for (int dx = -1; dx <= 1; dx++) {
						for (int dy = -1; dy <= 1; dy++) {
							if (dx == 0 && dy == 0) continue;
							visitedPositions.add(new Position(
									currentPosition.getX() + dx,
									currentPosition.getY() + dy));
						}
					}
				}
				break;
			}
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(POLL_INTERVAL_MS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}