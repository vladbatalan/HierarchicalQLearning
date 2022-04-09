package timebender.gameobjects.controllers;

import timebender.physics.states.movecommands.MoveCommand;

public interface ICommandObserver {
    void receiveCommand(MoveCommand command);
    void clearCommands();
}
