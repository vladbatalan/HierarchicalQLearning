package timebender.levels;

import timebender.Logger;
import timebender.assets.animations.types.lever.LeverTurnedOffAnimation;
import timebender.gameobjects.ObjectID;
import timebender.gameobjects.camera.GameCamera;
import timebender.gameobjects.controllers.ControllerBuilder;
import timebender.gameobjects.controllers.FixedController;
import timebender.gameobjects.mobs.MobileObject;
import timebender.gameobjects.mobs.OldPlayerInstance;
import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.stills.Objective;
import timebender.gameobjects.stills.StillObject;
import timebender.gameobjects.stills.TimeMachine;
import timebender.map.Map;
import timebender.physics.states.movecommands.MoveCommand;
import timebender.physics.utils.PointVector;

import java.awt.*;

import static timebender.gameobjects.handlers.GameObjectHandler.*;
import static timebender.levels.LevelFlagsSystem.*;

public abstract class LevelImproved {

    // The name of the level
    protected String name;

    // The map of the level
    protected Map map;

    // The map has its own camera
    protected GameCamera camera;

    // Each level has a timer
    protected LevelTimer timer;

    // Starting point
    protected TimeMachine startingTimeMachine;
    // Ending point
    protected Objective gameObjective;

    protected LevelImproved(String levelName, Map map) {
        this.name = levelName;
        this.map = map;
        this.timer = new LevelTimer();
        this.camera = new GameCamera(map.getMaxBounds());
        this.map.setCamera(this.camera);
    }

    public void update() {

        // Update the timer
        timer.update();

        // Use the game handler to update the map
        Update(map);

        for(MobileObject mobileObject : GetMobileObjects()){
            if(mobileObject.isDead()){
                Logger.Print("Mobile died!");
                // TODO: Offer possibility to subreset level
                resetComplete();
                return;
            }
        }
        if(GetPlayer().isDead()){
            Logger.Print("Player died!");
            // TODO: Offer possibility to subreset level
            resetComplete();
            return;
        }

        // If Space was pressed by player and intersects with objective active
        if(playerPressedSpaceEvent && playerOnGoal && gameObjective.getActiveState()){
            // TODO: Level complete!
            Logger.Print("Level complete!");
        }

        // TODO: Take action into reseting/restarting the Level if conditions matched
        // If Space pressed by any, if on timeMachine, teleport
        // If player pressed, levelToNextIteration
        if(playerPressedSpaceEvent && playerOnTimeMachine){
            levelToNextIteration();
        }
        if(isInstanceOnParadox){
            camera.setFollowedObject(onParadoxMob);
            Logger.Print("Instance on paradox!");
            // TODO: Offer possibility to subreset level
            resetComplete();
        }

        // Reset flags for next frame
        playerPressedSpaceEvent = false;
    }

    public void draw(Graphics g) {
        //sort of background color for map
        g.setColor(new Color(0xF8F77F));
        g.fillRect(0, 0, (int) map.getMaxBounds().getX(), (int) map.getMaxBounds().getY());

        if (camera != null) {
            camera.update(g);
        }
        map.draw(g);
        Draw(g);
    }

    /**
     * Function responsible for initializing all objects of the level and place them on initial positions.
     * Before initialising Level Objects, make sure that a player object has been added to the Game Object Handler.
     * <p>
     * Make sure to set the starting time machine and the goal objective.
     */
    public abstract void initLevelObjects();

    /**
     * Function responsible for restarting all the objects in the scene.
     * Reset ControllerBuilder.
     * Reset time counter.
     * Reset all positions and states.
     */
    public void resetSubState() {

        // Reset all the flags of the level
        ResetAllFlags();

        // Reset controller builder
        ControllerBuilder controllerBuilder = GetControllerBuilder();
        controllerBuilder.clearCommands();

        // Renew the OldInstances
        RenewOldInstances();

        // Reset all positions and states
        resetPositions();

        // Start level
        isLevelRunning = true;

        // Reset timer counter
        timer.restartTimer();
    }

    /**
     * Function responsible for resetting all the level.
     * Clear all Old Instances.
     * Reset timer counter.
     * Reset positions.
     */
    public void resetComplete() {

        // Reset all the flags of the level
        ResetAllFlags();

        // Delete all old instances
        ClearOldInstances();

        // Reset all positions and states
        resetPositions();

        // Start camera if needed
        if(camera != null)
            camera.start();

        // Start level
        isLevelRunning = true;

        // Reset timer counter
        timer.restartTimer();

    }

    /**
     * Function that is called when player entity teleports in time.
     * Create Fixed Controller using the Controller Builder.
     * Create Old Instance using Fixed Controller.
     * Reset Controller Builder.
     * Reset timer counter.
     * Reset all positions and states.
     */
    public void levelToNextIteration() {

        // Reset all the flags of the level
        ResetAllFlags();

        // Create Fixed Controller
        ControllerBuilder controllerBuilder = GetControllerBuilder();
        FixedController fixedController = controllerBuilder.buildController();

        Logger.PrintEndline();
        for(MoveCommand moveCommand : fixedController.getCommandList()){
            Logger.Print(moveCommand.toString());
        }
        Logger.PrintEndline();

        // Create Old instance using Fixed Controller
        OldPlayerInstance oldPlayerInstance = new OldPlayerInstance(fixedController);
        AddOldInstance(oldPlayerInstance);
        RenewOldInstances();

        // Reset controller builder
        controllerBuilder.clearCommands();

        // Reset all positions and states
        resetPositions();

        // Start level
        isLevelRunning = true;

        // Reset timer counter
        timer.restartTimer();

    }

    protected void resetPositions() {

        // Check if the starting point has been set
        if (startingTimeMachine == null)
            throw new RuntimeException("A starting time machine was not set for this level!");

        PointVector timeMachinePosition = startingTimeMachine.getPosition();

        // Set the player to the starting position
        Player player = GetPlayer();
        PointVector displacementPlayer = new PointVector((float) Player.BODY_WIDTH / 2,
                TimeMachine.BODY_HEIGHT - Player.BODY_HEIGHT);
        player.setPosition(timeMachinePosition.add(displacementPlayer));

        // Camera follow player
        if (camera != null) {
            camera.setFollowedObject(player);
        }

        //OldInstances to the starting position
        PointVector displacementOldInstance = new PointVector((float) OldPlayerInstance.BODY_WIDTH / 2,
                TimeMachine.BODY_HEIGHT - OldPlayerInstance.BODY_HEIGHT);
        PointVector startingPosition = timeMachinePosition.add(displacementOldInstance);

        for (MobileObject mobileObject : GetMobileObjects()) {

            // Resets the controller and internal configurations
            mobileObject.resetToInitialState();
            if (mobileObject.getId() == ObjectID.OldPlayerInstance) {

                // Get all old instances and reset position and controller
                OldPlayerInstance instance = (OldPlayerInstance) mobileObject;
                instance.setPosition(startingPosition);
            }
        }

        // update still objects
        for (StillObject structure : GetStillObjects()) {
            structure.resetToInitialState();
        }
    }

    public int getFrameNumber(){
        return timer.getFrameNumber();
    }

}
