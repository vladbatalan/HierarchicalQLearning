package timebender.physics.states.movecommands;

public class MoveCommand implements IMoveCommand {
    private final MoveCommandType commandType;
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

    @Override
    public String toString(){
        return commandType.toString() + " (" + frameTimestamp + ")";
    }

    @Override
    public void setTimestamp(int timestamp) {
        this.frameTimestamp = timestamp;
    }

    public int getFrameTimestamp() {
        return frameTimestamp;
    }
}
