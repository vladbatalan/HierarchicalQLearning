package timebender.gameobjects.controllers;

import timebender.Game;
import timebender.physics.Body;
import timebender.physics.states.movecommands.MoveCommand;

import java.util.ArrayList;

public class Controller {
    private ArrayList<MoveCommand> commandList;

    private Body body;

    private int commandIndex = 0;

    public Controller(){
        commandIndex = 0;
        body = null;
    }

    public Controller(Game game, Body body){
        commandIndex = 0;
        this.body = body;
    }

    public void execute(int executionTime){

        // Will execute only if a body exists
        if(body == null) {
            System.out.println("A body was not attached to Controller!");
            return;
        }

        int numberOfCommands = commandList.size();

        while(commandIndex < numberOfCommands){

            MoveCommand current = commandList.get(commandIndex);

            // Execute all the commands that must be triggered at this current time
            if(current.getFrameTimestamp() == executionTime) {

                // Feed body with command
                body.getMoveStateManager().nextState(current);
            }
            else if(current.getFrameTimestamp() > executionTime){
                // No more commands to be executed at this moment of time
                break;
            }

            // Increase the current index
            commandIndex ++;
        }
    }

    public void resetController(){
        commandIndex = 0;
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

    public void setBody(Body body) {
        this.body = body;
    }

    public void setCommandList(ArrayList<MoveCommand> commandList) {
        this.commandList = commandList;
    }
}
