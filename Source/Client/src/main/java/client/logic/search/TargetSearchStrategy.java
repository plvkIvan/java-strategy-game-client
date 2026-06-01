package client.logic.search;

import client.domain.GameSnapshot;
import client.domain.Position;

public interface TargetSearchStrategy {

    Position findVisibleTarget(GameSnapshot gameSnapshot);

    boolean isInSearchArea(Position position);
}