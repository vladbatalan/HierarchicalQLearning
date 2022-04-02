package timebender.input;

import timebender.Game;
import timebender.Logger;
import timebender.gameobjects.controllers.KeyboardController;
import timebender.physics.Body;
import timebender.physics.states.MoveCommandType;
import timebender.physics.states.movecommands.MoveCommand;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static timebender.physics.states.MoveCommandType.*;

public class KeyInput extends KeyAdapter {
    private Game game;
    private final ArrayList<KeyboardController> controllers = new ArrayList<>();

    public KeyInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // For loop kept for concurrency reasons
        for(int index = 0; index < controllers.size(); index ++){
            KeyboardController current = controllers.get(index);
            current.receiveKeyEvent(key, true);
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // For loop kept for concurrency reasons
        for(int index = 0; index < controllers.size(); index ++){
            KeyboardController current = controllers.get(index);
            current.receiveKeyEvent(key, false);
        }
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
