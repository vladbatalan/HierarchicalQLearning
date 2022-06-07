package timebender.input;

import timebender.gameobjects.controllers.ExternalController;
import timebender.gameobjects.controllers.KeyboardController;
import timebender.physics.states.movecommands.MoveCommand;

import java.util.ArrayList;

import static timebender.levels.LevelFlagsSystem.isLevelRunning;

public class ExternalInput {
    private final ArrayList<ExternalController> controllers = new ArrayList<>();

    public ExternalInput(){
        
    }

    public void receiveCommand(MoveCommand moveCommand){
        if(!isLevelRunning)
            return;

        // For loop kept for concurrency reasons
        for(int index = 0; index < controllers.size(); index ++){
            ExternalController current = controllers.get(index);
            current.receiveCommend(moveCommand);
        }
    }

    public void addExternalController(ExternalController controller){
        controllers.add(controller);
    }

    public void removeExternalController(ExternalController controller){
        controllers.remove(controller);
    }

    public void clearExternalControllers(){
        controllers.clear();
    }
}
