package timebender.input;

import timebender.gameobjects.controllers.KeyboardController;

import java.util.ArrayList;

public class ExternalInput {
    private final ArrayList<KeyboardController> controllers = new ArrayList<>();

    public void ExternalInput(){
        
    }

    public void addKeyboardController(KeyboardController controller){
        controllers.add(controller);
    }

    public void removeKeyboardController(KeyboardController controller){
        controllers.remove(controller);
    }

    public void clearKeyboardContrellers(){
        controllers.clear();
    }
}
