package client.logic.search;

import java.util.Set;

import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;

public class TreasureTargetSearchStrategy implements TargetSearchStrategy {
    private final Set<Position> ownHalfPositions;

    public TreasureTargetSearchStrategy(Set<Position> ownHalfPositions) {
        this.ownHalfPositions = ownHalfPositions;
    }

    @Override
    public Position findVisibleTarget(GameSnapshot gameSnapshot) {
        for (FullMapNodeInfo node : gameSnapshot.getFullGameMap().getMapNodes()) {
            if (node.isMyTreasurePresent()) {
                return node.getPosition();
            }
        }

        return null;
    }

    @Override
    public boolean isInSearchArea(Position position) {
        return ownHalfPositions.contains(position);
    }
}