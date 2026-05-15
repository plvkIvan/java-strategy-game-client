package client.logic.mapgeneration;

import client.domain.GameHalfMap;
import client.domain.Position;

public interface CastlePlacer {
    Position chooseCastlePosition(GameHalfMap gameHalfMap,
                                  CastlePlacementTarget target);
}