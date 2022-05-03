package timebender.gameobjects.controllers;

import timebender.Logger;
import timebender.physics.Body;
import timebender.physics.states.movecommands.MoveCommand;
import timebender.physics.states.movecommands.MoveCommandType;

import java.util.ArrayList;

public class FixedController {
    private ArrayList<MoveCommand> commandList;

    private boolean spaceEvent = false;
    private boolean commandsFinishedEvent = false;
    private boolean firstFinishedEvent = false;

    private int commandIndex = 0;

    public FixedController(){
        commandIndex = 0;
    }

    public void execute(int executionTime, Body body){

        // Will execute only if a body exists
        if(body == null) {
            System.out.println("A body was not attached to Controller!");
            return;
        }

        spaceEvent = false;
        int numberOfCommands = commandList.size();

        while(commandIndex < numberOfCommands){

            MoveCommand current = commandList.get(commandIndex);

            // Execute all the commands that must be triggered at this current time
            if(current.getFrameTimestamp() == executionTime) {

                // Feed body with command
                body.getMoveStateManager().nextState(current);

                // If space event pressed, set space flag
                if(current.getCommandType() == MoveCommandType.SPACE_RELEASED) {
                    spaceEvent = true;
                }
            }
            else if(current.getFrameTimestamp() > executionTime){
                // No more commands to be executed at this moment of time
                break;
            }

            // Increase the current index
            commandIndex ++;
        }

        // Check: if index is greater than the number of commands, but instance still on play
        // We identify a paradox
        commandsFinishedEvent = commandIndex >= numberOfCommands;
        if(commandsFinishedEvent && !firstFinishedEvent){
            firstFinishedEvent = true;
            commandsFinishedEvent = false;
        }

    }

    public void resetController(){
        commandIndex = 0;
        spaceEvent = false;
        commandsFinishedEvent = false;
        firstFinishedEvent = false;
    }

    @Override
    public String toString(){
        StringBuilder ret = new StringBuilder();
        for(MoveCommand command : commandList){
            ret.append(command.toString()).append("\n");
        }
        return ret.toString();
    }

    public ArrayList<MoveCommand> getCommandList(){
        return commandList;
    }

    public void setCommandList(ArrayList<MoveCommand> commandList) {
        this.commandList = commandList;
    }

    public boolean isSpaceEvent() {
        return spaceEvent;
    }

    public boolean isCommandsFinishedEvent(){
        return commandsFinishedEvent;
    }
}
