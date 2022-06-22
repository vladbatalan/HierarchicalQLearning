package timebender.levels.reward;

import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.gameobjects.stills.ISwitchable;
import timebender.gameobjects.stills.StillObject;
import timebender.levels.Level;

import static timebender.levels.LevelFlagsSystem.*;

/**
 *  Rewarding system based only on evaluating the state of the game.
 *  If the game is lost, there will be a negative impact.
 *  If the game is won, the big reward will provide strong connections for policy.
 */
public class PromoteAllStatesActiveReward implements IRewardSystem {

    @Override
    public double evaluateReward(Level level) {

        if(levelLost)
            return -5000;

        if(levelComplete)
            return 5000;

        // Foreach element that is active give extra reward
        float activeAmplifier = 0;
        int allSwitchable = 0;
        for(StillObject stillObject : GameObjectHandler.GetStillObjects()){
            if(stillObject instanceof ISwitchable){
                allSwitchable ++;

                if(((ISwitchable) stillObject).isActive())
                    activeAmplifier ++;
            }
        }

        // The SPACE_ACTION was initiated
        if(playerPressedSpaceEvent){

            // Unsuccessful attempt
            if(!playerOnTimeMachine && !playerOnGoal){
                return (allSwitchable + 1) * (-0.1);
            }

            if(playerOnTimeMachine){
                // If number of frames is low, it means that the new Old instance has no purpose
                // Penalize player
//                if(GameObjectHandler.GetFrameNumber() < 20){
//                    System.out.println("Frame: " + GameObjectHandler.GetFrameNumber());
//                    return -10;
//                }

                // Limit old instance number
                if(GameObjectHandler.GetOldInstances().size() > 5){
                    return -10;
                }
            }
        }

        return (allSwitchable - activeAmplifier) * (-0.1) ;
    }
}
