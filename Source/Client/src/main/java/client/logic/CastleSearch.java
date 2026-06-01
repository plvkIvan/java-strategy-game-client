package client.logic;

import java.util.Set;

import client.domain.GameSnapshot;
import client.domain.Position;
import client.logic.search.CastleTargetSearchStrategy;
import client.logic.search.TargetSearchService;

public class CastleSearch {
    private final TargetSearchService targetSearchService;

    public CastleSearch(Set<Position> ownHalfPositions) {
        this.targetSearchService = new TargetSearchService(
                new CastleTargetSearchStrategy(ownHalfPositions)
        );
    }

    public Position chooseTarget(GameSnapshot gameSnapshot,
                                 Position currentPosition,
                                 Set<Position> visitedPositions,
                                 PathFinder pathFinder) {
        return targetSearchService.chooseTarget(
                gameSnapshot,
                currentPosition,
                visitedPositions,
                pathFinder
        );
    }
}