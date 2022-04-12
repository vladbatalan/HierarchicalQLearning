package timebender.gameobjects.handlers;

import timebender.Game;
import timebender.gameobjects.GameObject;
import timebender.gameobjects.ObjectID;
import timebender.gameobjects.mobs.MobileObject;
import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.stills.StillObject;
import timebender.map.Map;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameObjectHandler {

    // The list needs to be a synchronized one

    private static final ConcurrentLinkedQueue<StillObject> stillObjects = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<MobileObject> mobileObjects = new ConcurrentLinkedQueue<>();
    private static Player player = null;

    private static Game game = null;

    public static void AddGameObject(GameObject gameObject) {

        if (gameObject.isMobile()) {
            mobileObjects.add((MobileObject) gameObject);
            if (gameObject.getId() == ObjectID.Player)
                player = (Player) gameObject;
        } else {
            stillObjects.add((StillObject) gameObject);
        }

    }

    public static void RemoveGameObject(GameObject gameObject) {
        if (gameObject.isMobile()) {
            mobileObjects.remove((MobileObject) gameObject);
            if (gameObject.getId() == ObjectID.Player)
                player = null;
        } else
            stillObjects.remove((StillObject) gameObject);
    }

    public static void ClearGameObjects() {
        mobileObjects.clear();
        stillObjects.clear();
        player = null;
    }

    public static void Update(Map currentMap) {
        for (GameObject object : stillObjects) {
            object.Update(currentMap);
        }
        for (GameObject object : mobileObjects) {
            if (object != player)
                object.Update(currentMap);
        }
        if (player != null) {
            player.Update(currentMap);
        }

        // Interaction between Player and specific stationary objects
//        Player player = getPlayer();
//        LevelFlagsSystem.playerOnTimeMachine = false;
//        LevelFlagsSystem.playerOnGoal = false;
//        if (player != null) {
//            for (StillObject structure : stillObjects) {
//                if (player.getBody().getHitBox().intersects(structure.getBody().getHitBox())) {
//                    //we got a collision
//                    if (structure.id == ObjectID.TimeMachine) {
//                        TimeMachine timeMachine = (TimeMachine) structure;
//                        //setter for end game condition
//                        LevelFlagsSystem.playerOnTimeMachine = true;
//                        //display a help string above time machine
//                        timeMachine.displayHelpString();
//                        //System.out.println("Player on Time Machine");
//                    }
//                    if (structure.id == ObjectID.Objective) {
//                        //setter for player - time machine collision
//                        LevelFlagsSystem.playerOnGoal = true;
//                        //System.out.println("Player on Goal");
//                    }
//                }
//            }
//        }
        // The generate concrete interaction that make objects interract with other objects
        // the objects that are not solid, are not taken into discussion
//
//        for (MobileObject mob : mobileObjects) {
//            for (StillObject structure : stillObjects) {
//
//                // special case: structure is a composit Object
//                if (structure.getId() == ObjectID.TwoPanScale) {
//
//                    TwoPanScale scale = (TwoPanScale) structure;
//                    ObjectCollisionUtil.manageObjectsCollision(mob, scale.getFirstPan());
//                    ObjectCollisionUtil.manageObjectsCollision(mob, scale.getSecondPan());
//                    continue;
//                }
//                // we let the collision handler to manage the interaction
//                ObjectCollisionUtil.manageObjectsCollision(mob, structure);
//            }
//        }

    }

    public static void Draw(Graphics g) {
        for (GameObject object : stillObjects) {
            object.Draw(g);
        }
        for (GameObject object : mobileObjects) {
            if (object.getId() != ObjectID.Player)
                object.Draw(g);
        }
        if (player != null) {
            player.Draw(g);
        }
    }


//    public void renewOldInstances() {
//        ArrayList<MobileObject> toBeRemoved = new ArrayList<>();
//        for (MobileObject mobile : mobileObjects) {
//            if (mobile.id == ObjectID.OldPlayerInstance) {
//                toBeRemoved.add(mobile);
//            }
//        }
//
//        for (MobileObject removed : toBeRemoved) {
//            mobileObjects.remove(removed);
//        }
//
//        mobileObjects.addAll(oldInstances);
//    }

//    public void resetInitialLevelPosition() {
//        // update mobile objects
//        for (MobileObject mob : mobileObjects) {
//            mob.resetToInitialState();
//        }
//
//        // update still objects
//        for (StillObject structure : stillObjects) {
//            structure.resetToInitialState();
//        }
//    }

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
}
