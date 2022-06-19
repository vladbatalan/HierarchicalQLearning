package timebender.levels;

import timebender.gameobjects.mobs.MobileObject;

public class LevelFlagsSystem {

    // the player can move end the map is updating
    public static Boolean isLevelRunning = false;
    public static Boolean playerPressedSpaceEvent = false;


    // we use this to check if pressing space will restart the game creating a new instance of player
    public static Boolean playerOnTimeMachine = false;
    public static Boolean playerOnGoal = false;
    public static Boolean unsuccessfulCommand = false;


    // we some kind of system to check if old player instance when press space, maybe
    public static MobileObject onParadoxMob = null;
    public static boolean isInstanceOnParadox = false;


    // Win/lose indicators of the game
    public static Boolean levelComplete = false;
    public static Boolean levelLost = false;


    public static void CreateParadoxOnMob(MobileObject mob){
        onParadoxMob = mob;
        isInstanceOnParadox = true;
    }

    public static boolean enablePlayerControl = true;

    public static void ResetAllFlags(){
        isLevelRunning = false;
        playerOnTimeMachine = false;
        onParadoxMob = null;
        isInstanceOnParadox = false;
        enablePlayerControl = true;
        playerPressedSpaceEvent = false;

        // For level state purpose
        levelLost = false;
        levelComplete = false;

        // Check if a command is unsuccessful
        unsuccessfulCommand = false;
    }


}
