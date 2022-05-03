package timebender.gameobjects.handlers;

import timebender.Game;
import timebender.Logger;
import timebender.gameobjects.GameObject;
import timebender.gameobjects.ObjectID;
import timebender.gameobjects.controllers.ControllerBuilder;
import timebender.gameobjects.mobs.MobileObject;
import timebender.gameobjects.mobs.OldPlayerInstance;
import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.stills.StillObject;
import timebender.gameobjects.stills.TimeMachine;
import timebender.gameobjects.utils.ObjectCollisionUtil;
import timebender.levels.LevelFlagsSystem;
import timebender.map.Map;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameObjectHandler {

    private static final ConcurrentLinkedQueue<StillObject> stillObjects = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<MobileObject> mobileObjects = new ConcurrentLinkedQueue<>();
    private static final ArrayList<OldPlayerInstance> oldInstances = new ArrayList<>();
    private static Player player = null;

    private static Game game = null;

    public static void AddGameObject(GameObject gameObject) {

        if (gameObject.getId() == ObjectID.Player) {
            player = (Player) gameObject;
            return;
        }

        if (gameObject.isMobile()) {
            mobileObjects.add((MobileObject) gameObject);
        } else {
            stillObjects.add((StillObject) gameObject);
        }

    }

    public static void RemoveGameObject(GameObject gameObject) {
        if (gameObject.isMobile()) {
            mobileObjects.remove((MobileObject) gameObject);
        } else {
            stillObjects.remove((StillObject) gameObject);
        }
    }

    public static void ClearGameObjects() {
        mobileObjects.clear();
        stillObjects.clear();
    }

    public static void Update(Map currentMap) {

        // Update all still objects
        for (GameObject object : stillObjects) {
            object.Update(currentMap);
        }

        // Update mobile objects (Except player one)
        for (GameObject object : mobileObjects) {
            object.Update(currentMap);
        }

        // Update the player
        if (player != null) {
            player.Update(currentMap);
        }

        // Manage collisions
        for (MobileObject mob : mobileObjects) {
            for (StillObject structure : stillObjects) {
                // If mob is oldInstance, check special collisions
                if(mob.id == ObjectID.OldPlayerInstance) {

                    OldPlayerInstance oldPlayerInstance = (OldPlayerInstance) mob;

                    // Only if the space event has been triggered by oldInstance
                    if(oldPlayerInstance.isSpaceEvent()) {

                        // Check collision with the structure
                        if (ObjectCollisionUtil.isThereCollisionBetween(mob, structure)) {

                            if (structure.id == ObjectID.TimeMachine) {
                                Logger.Print("Old Instance teleport in time.");
                                // Remove instance from play
                                RemoveGameObject(oldPlayerInstance);
                                continue;
                            }
                       //  This case should not be possible
                            if (structure.id == ObjectID.Objective) {
                                Logger.Print("Old Instance on Goal. This case should not be possible.");
                            }

                        }
                    }

                    if(oldPlayerInstance.isCommandsFinishedEvent()){
                        // Create Paradox
                        LevelFlagsSystem.CreateParadoxOnMob(oldPlayerInstance);
                    }

                }

                // we let the collision handler to manage the interaction
                ObjectCollisionUtil.manageObjectsCollision(mob, structure);


            }
        }

        LevelFlagsSystem.playerOnGoal = false;
        LevelFlagsSystem.playerOnTimeMachine = false;
        // Check player collision
        // If the player was not checked and player is not null
        if (player != null) {
            for (StillObject structure : stillObjects) {

                // Check special collisions
                if (ObjectCollisionUtil.isThereCollisionBetween(player, structure)) {

                    if (structure.id == ObjectID.TimeMachine) {
//                        Logger.Print("Player on time machine");
                        LevelFlagsSystem.playerOnTimeMachine = true;
                    }

                    if (structure.id == ObjectID.Objective) {

//                        Logger.Print("Player on objective");
                        LevelFlagsSystem.playerOnGoal = true;
                    }
                }

                // Adjust position oc collision
                ObjectCollisionUtil.manageObjectsCollision(player, structure);


            }
        }
    }

    public static void Draw(Graphics g) {
        for (GameObject object : stillObjects) {
            object.Draw(g);
        }
        for (GameObject object : mobileObjects) {
            object.Draw(g);
        }
        if (player != null) {
            player.Draw(g);
        }
    }

    public static void AddOldInstance(OldPlayerInstance oldPlayer){
        oldInstances.add(oldPlayer);
    }
    public static void RenewOldInstances(){
        mobileObjects.removeIf(mobileObject -> mobileObject.getId() == ObjectID.OldPlayerInstance);
        mobileObjects.addAll(oldInstances);
    }

    public static void ClearOldInstances(){
        mobileObjects.removeIf(mobileObject -> mobileObject.getId() == ObjectID.OldPlayerInstance);
        oldInstances.clear();
    }

    public static void SetGame(Game game) {
        GameObjectHandler.game = game;
    }

    public static void SetPlayer(Player player) {
        GameObjectHandler.player = player;
        mobileObjects.add(player);
    }

    public static int GetFrameNumber() {
        if (game != null)
            return game.getCurrentFrame();
        return -1;
    }

    public static ConcurrentLinkedQueue<MobileObject> GetMobileObjects() {
        return GameObjectHandler.mobileObjects;
    }

    public static ConcurrentLinkedQueue<StillObject> GetStillObjects() {
        return GameObjectHandler.stillObjects;
    }

    public static Player GetPlayer() {
        return player;
    }

    public static ControllerBuilder GetControllerBuilder() {
        return game.getControllerBuilder();
    }
}
