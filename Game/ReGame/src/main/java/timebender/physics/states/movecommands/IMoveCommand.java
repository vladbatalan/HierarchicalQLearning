package timebender.physics.states.movecommands;

public interface IMoveCommand {
    MoveCommandType getCommandType();
    void setTimestamp(int timestamp);
}
