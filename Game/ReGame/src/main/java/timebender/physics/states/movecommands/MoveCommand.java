package timebender.physics.states.movecommands;

public class MoveCommand implements IMoveCommand {
    private MoveCommandType commandType;
    private int frameTimestamp;

    public MoveCommand(MoveCommandType commandType){
        this.commandType = commandType;
        this.frameTimestamp = -1;
    }

    public MoveCommand(MoveCommandType commandType, int frameTimestamp){
        this.commandType = commandType;
        this.frameTimestamp = frameTimestamp;
    }

    public MoveCommandType getCommandType() {
        return commandType;
    }

    public int getFrameTimestamp() {
        return frameTimestamp;
    }
}
