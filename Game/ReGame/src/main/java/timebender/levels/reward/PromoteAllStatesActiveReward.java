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
            return -10000;

        if(levelComplete)
            return 100000;

        // The SPACE_ACTION was initiated
        if(playerPressedSpaceEvent){

            // Unsuccessful attempt
            if(!playerOnTimeMachine && !playerOnGoal){
                return -10;
            }
        }

        // Detect other moves that do not have any effect and punish them
        if(unsuccessfulCommand){
            unsuccessfulCommand = false;
            return -10;
        }

        // Foreach element that is active give extra reward
        float activeAmplifier = 0;
        for(StillObject stillObject : GameObjectHandler.GetStillObjects()){
            if(stillObject instanceof ISwitchable){
                if(((ISwitchable) stillObject).isActive())
                    activeAmplifier ++;
            }
        }

        return -0.5 + 5 * activeAmplifier;
    }
}
