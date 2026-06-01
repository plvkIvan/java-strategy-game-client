package client.logic;

import java.util.Set;

import client.domain.GameSnapshot;
import client.domain.Position;
import client.logic.search.TargetSearchService;
import client.logic.search.TreasureTargetSearchStrategy;

public class TreasureSearch {
    private final TargetSearchService targetSearchService;

    public TreasureSearch(Set<Position> ownHalfPositions) {
        this.targetSearchService = new TargetSearchService(
                new TreasureTargetSearchStrategy(ownHalfPositions)
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