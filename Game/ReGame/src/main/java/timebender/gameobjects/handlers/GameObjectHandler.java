package timebender.gameobjects.handlers;

import timebender.Game;
import timebender.gameobjects.GameObject;
import timebender.gameobjects.ObjectID;
import timebender.gameobjects.mobs.MobileObject;
import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.stills.StillObject;
import timebender.map.Map;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameObjectHandler {

    // The list needs to be a synchronized one
    private static final List<StillObject> stillObjects = Collections.synchronizedList(new ArrayList<>());
    private static final List<MobileObject> mobileObjects = Collections.synchronizedList(new ArrayList<>());
    private static Player player = null;

    private static Game game = null;

    public static void AddGameObject(GameObject gameObject){

        if(gameObject.isMobile())
            mobileObjects.add((MobileObject) gameObject);
        else
            stillObjects.add((StillObject) gameObject);
    }

    public static void RemoveGameObject(GameObject gameObject){
        if(gameObject.isMobile())
            mobileObjects.remove((MobileObject) gameObject);
        else
            stillObjects.remove((StillObject) gameObject);
    }

    public static void ClearGameObjects(){
        mobileObjects.clear();
        stillObjects.clear();
        player = null;
    }

    public static void Update(Map currentMap) {
        for(GameObject object : stillObjects){
            object.Update(currentMap);
        }
        for(GameObject object: mobileObjects){
            object.Update(currentMap);
        }
        if(player != null){
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
        for(GameObject object : stillObjects){
            object.Draw(g);
        }
        for(GameObject object: mobileObjects){
            object.Draw(g);
        }
        if(player != null){
            player.Draw(g);
        }
    }
//
//    public void setPlayer(Player player) {
//        if (getPlayer() == null)
//            mobileObjects.add(player);
//        else
//            for (int index = 0; index < mobileObjects.size(); index++)
//                if (mobileObjects.get(index).id == ObjectID.Player)
//                    mobileObjects.set(index, player);
//    }
//
//    public Player getPlayer() {
//        for (int index = 0; index < mobileObjects.size(); index++)
//            if (mobileObjects.get(index).id == ObjectID.Player)
//                return (Player) mobileObjects.get(index);
//        return null;
//    }

//    public void addStillObject(StillObject structure) {
//        stillObjects.add(structure);
//    }
//
//    public void addMobileObject(MobileObject mobile) {
//        mobileObjects.add(mobile);
//    }

//    public void addOldInstance(OldPlayerInstance oldPlayer) {
//        oldInstances.add(oldPlayer);
//    }

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

//    public void clearOldInstances() {
//        oldInstances.clear();
//    }

    public static void SetGame(Game game){
        GameObjectHandler.game = game;
    }

    public static void SetPlayer(Player player){
        GameObjectHandler.player = player;
    }

    public static int GetFrameNumber(){
        if(game != null)
            return game.getCurrentFrame();
        return -1;
    }
}
