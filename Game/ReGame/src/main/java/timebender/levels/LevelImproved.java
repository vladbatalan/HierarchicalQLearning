package timebender.levels;

import timebender.gameobjects.camera.GameCamera;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.map.Map;

import java.awt.*;

public class LevelImproved {

    // The name of the level
    protected String name;

    // The map of the level
    protected Map map;
    // The map has its own camera
    protected GameCamera camera;


    // Each level has a timer
    protected LevelTimer timer;

    public LevelImproved(String levelName, Map map) {
        this.name = levelName;
        this.map = map;
        this.timer = new LevelTimer();
        this.camera = new GameCamera(map.getMaxBounds());
    }

    public void update() {

        // Update the timer
        timer.update();

        // Use the game handler to update the map
        GameObjectHandler.Update(map);

        // TODO: Check the death conditions
        // TODO: Check the win conditions
        // TODO: Take action into reseting/restarting the Level if conditions matched
    }

    public void Draw(Graphics g){
        //sort of background color for map
        g.setColor(new Color(0xF8F77F));
        g.fillRect(0, 0, (int)map.getMaxBounds().getX(), (int)map.getMaxBounds().getY());

        camera.update(g);
        map.draw(g);
        GameObjectHandler.Draw(g);
    }

    /**
     * Function responsible for initializing all objects of the level and place them on initial positions.
     */
    public void initLevelObjects() {

    }

    /**
     * Function responsible for restarting all the objects in the scene.
     * Reset all positions and states.
     * Reset ControllerBuilder.
     * Reset time counter.
     */
    public void resetSubState() {

    }

    /**
     * Function responsible for resetting all the level.
     * Clear all mobiles except player.
     * Reset positions.
     * Reset timer counter.
     */
    public void resetComplete() {

    }

    /**
     * Function that is called when player entity teleports in time.
     * Reset all positions and states.
     * Create Fixed Controller using the Controller Builder.
     * Create Old Instance using Fixed Controller.
     * Reset Controller Builder.
     * Reset timer counter.
     */
    public void levelToNextIteration() {

    }


}
