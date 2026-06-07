package client.state;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import client.domain.GameMapConstants;
import client.domain.Position;
import client.logic.mapgeneration.CastlePlacementTarget;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class GameStateAnalyzer {

    public PlayerState findOwnPlayerState(GameState gameState,
                                          UniquePlayerIdentifier ownClientPlayerID) {
        for (PlayerState playerState : gameState.getPlayers()) {
            if (playerState.getUniquePlayerID().equals(ownClientPlayerID.getUniquePlayerID())) {
                return playerState;
            }
        }

        return null;
    }

    public Position findCurrentPosition(GameState gameState) {
        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            if (node.getPlayerPositionState().representsMyPlayer()) {
                return new Position(node.getX(), node.getY());
            }
        }

        return null;
    }

    public Set<Position> determineOwnHalf(GameState gameState) {
        Set<FullMapNode> mapNodes = new HashSet<>(gameState.getMap().getMapNodes());

        if (mapNodes.size() < GameMapConstants.FULL_MAP_SIZE) {
            return null;
        }

        Position myFort = null;
        int maxX = 0;
        int maxY = 0;

        for (FullMapNode node : mapNodes) {
            if (node.getX() > maxX) {
                maxX = node.getX();
            }

            if (node.getY() > maxY) {
                maxY = node.getY();
            }

            if (node.getFortState() == EFortState.MyFortPresent) {
                myFort = new Position(node.getX(), node.getY());
            }
        }

        if (myFort == null) {
            return null;
        }

        int mapWidth = maxX + 1;

        Set<Position> ownHalf = new HashSet<>();

        if (mapWidth > GameMapConstants.HALF_MAP_WIDTH) {
            boolean ownIsLeft = myFort.getX() <= GameMapConstants.LAST_LEFT_HALF_X;

            for (FullMapNode node : mapNodes) {
                if (ownIsLeft && node.getX() <= GameMapConstants.LAST_LEFT_HALF_X) {
                    ownHalf.add(new Position(node.getX(), node.getY()));
                }

                if (!ownIsLeft && node.getX() >= GameMapConstants.FIRST_RIGHT_HALF_X) {
                    ownHalf.add(new Position(node.getX(), node.getY()));
                }
            }
        } else {
            boolean ownIsTop = myFort.getY() <= GameMapConstants.LAST_TOP_HALF_Y;

            for (FullMapNode node : mapNodes) {
                if (ownIsTop && node.getY() <= GameMapConstants.LAST_TOP_HALF_Y) {
                    ownHalf.add(new Position(node.getX(), node.getY()));
                }

                if (!ownIsTop && node.getY() >= GameMapConstants.FIRST_BOTTOM_HALF_Y) {
                    ownHalf.add(new Position(node.getX(), node.getY()));
                }
            }
        }

        return ownHalf;
    }

    public void expandMountainVision(GameState gameState,
                                     Position currentPosition,
                                     Set<Position> visitedPositions) {
        Set<Position> knownMapPositions = collectKnownMapPositions(gameState);

        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            if (node.getX() == currentPosition.getX()
                    && node.getY() == currentPosition.getY()) {

                if (node.getTerrain() == ETerrain.Mountain) {
                    addVisibleMountainNeighbors(currentPosition, visitedPositions, knownMapPositions);
                }

                break;
            }
        }
    }

    private Set<Position> collectKnownMapPositions(GameState gameState) {
        Set<Position> knownMapPositions = new HashSet<>();

        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            knownMapPositions.add(new Position(node.getX(), node.getY()));
        }

        return knownMapPositions;
    }

    private void addVisibleMountainNeighbors(Position currentPosition,
                                             Set<Position> visitedPositions,
                                             Set<Position> knownMapPositions) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                Position visiblePosition = new Position(
                        currentPosition.getX() + dx,
                        currentPosition.getY() + dy
                );

                if (knownMapPositions.contains(visiblePosition)) {
                    visitedPositions.add(visiblePosition);
                }
            }
        }
    }

    public CastlePlacementTarget determineCastlePlacementTarget(GameState gameState) {
        if (gameState == null || gameState.getMap() == null) {
            return CastlePlacementTarget.ANY;
        }

        Collection<FullMapNode> mapNodes = gameState.getMap().getMapNodes();

        if (mapNodes == null || mapNodes.isEmpty()) {
            return CastlePlacementTarget.ANY;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        for (FullMapNode node : mapNodes) {
            if (node.getX() < minX) {
                minX = node.getX();
            }

            if (node.getY() < minY) {
                minY = node.getY();
            }
        }

        if (minX == 0 && minY == 0) {
            return CastlePlacementTarget.BOTTOM_RIGHT;
        }

        if (minX > 0 && minY == 0) {
            return CastlePlacementTarget.BOTTOM_LEFT;
        }

        if (minX == 0 && minY > 0) {
            return CastlePlacementTarget.TOP_RIGHT;
        }

        return CastlePlacementTarget.TOP_LEFT;
    }
}