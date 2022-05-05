package timebender.gameobjects.controllers;

import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.physics.states.movecommands.MoveCommand;
import timebender.physics.states.movecommands.MoveCommandType;

import java.util.ArrayList;

public class ControllerBuilder implements ICommandObserver{
    private final ArrayList<MoveCommand> commandList = new ArrayList<>();


    public FixedController buildController(){

        // Create a new controller based on the gathered commands
        FixedController build = new FixedController();

        ArrayList<MoveCommand> controllerCommands = new ArrayList<>(commandList);

        // From command list remove all after the last space command
//        for(int i = controllerCommands.size() - 1; i >= 0 && controllerCommands.get(i).getCommandType() != MoveCommandType.SPACE_RELEASED; i--) {
//            controllerCommands.remove(i);
//        }

        build.setCommandList(controllerCommands);

        return build;
    }

    @Override
    public void receiveCommand(MoveCommand command) {

        // If a received command has no timestamp
        if(command.getFrameTimestamp() == -1)
        {
            // Assign it a timestamp
            command.setTimestamp(GameObjectHandler.GetFrameNumber());
        }

        // Add it to the list
        commandList.add(command);
    }

    @Override
    public void clearCommands() {
        commandList.clear();
    }
}
