package client.state;

import java.util.HashSet;
import java.util.Set;

import client.domain.Position;
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

        if (mapNodes.size() < 100) {
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

    public void expandMountainVision(GameState gameState,
                                     Position currentPosition,
                                     Set<Position> visitedPositions) {
        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            if (node.getX() == currentPosition.getX()
                    && node.getY() == currentPosition.getY()) {

                if (node.getTerrain() == ETerrain.Mountain) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx == 0 && dy == 0) {
                                continue;
                            }

                            visitedPositions.add(new Position(
                                    currentPosition.getX() + dx,
                                    currentPosition.getY() + dy
                            ));
                        }
                    }
                }

                break;
            }
        }
    }
}