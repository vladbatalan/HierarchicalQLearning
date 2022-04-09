package timebender.gameobjects.controllers;

import timebender.Game;
import timebender.physics.states.movecommands.MoveCommand;

import java.util.ArrayList;

public class ControllerBuilder implements ICommandObserver{
    private ArrayList<MoveCommand> commandList = new ArrayList<>();
    private Game game;

    public ControllerBuilder(Game game){
        this.game = game;
    }

    public Controller buildController(){

        // Create a new controller based on the gathered commands
        Controller build = new Controller();

        ArrayList<MoveCommand> controllerCommands = (ArrayList<MoveCommand>) commandList.clone();

        build.setCommandList(controllerCommands);

        return build;
    }

    @Override
    public void receiveCommand(MoveCommand command) {

        // If a received command has no timestamp
        if(command.getFrameTimestamp() == -1)
        {
            // Assign it a timestamp
            command.setTimestamp(game.getCurrentFrame());
        }

        // Add it to the list
        commandList.add(command);
    }

    @Override
    public void clearCommands() {
        commandList.clear();
    }
}