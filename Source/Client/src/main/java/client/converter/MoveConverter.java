package client.converter;

import client.domain.MoveDirection;
import messagesbase.messagesfromclient.EMove;

public class MoveConverter {

    public EMove toDto(MoveDirection moveDirection) {
        switch (moveDirection) {
            case UP:
                return EMove.Up;
            case DOWN:
                return EMove.Down;
            case LEFT:
                return EMove.Left;
            case RIGHT:
                return EMove.Right;
            default:
                throw new IllegalArgumentException("Unsupported move direction: " + moveDirection);
        }
    }
}