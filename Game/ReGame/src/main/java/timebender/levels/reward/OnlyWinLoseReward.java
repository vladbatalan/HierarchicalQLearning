package timebender.levels.reward;

import timebender.levels.Level;
import timebender.levels.LevelFlagsSystem;

/**
 *  Rewarding system based only on evaluating the state of the game.
 *  If the game is lost, there will be a negative impact.
 *  If the game is won, the big reward will provide strong connections for policy.
 */
public class OnlyWinLoseReward implements IRewardSystem {

    @Override
    public double evaluateReward(Level level) {

        if(LevelFlagsSystem.levelLost)
            return -5000;

        if(LevelFlagsSystem.levelComplete)
            return 5000;

        return -0.5;
    }
}
