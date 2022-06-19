package timebender.levels.reward;

import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.levels.Level;
import timebender.physics.utils.PointVector;

import static timebender.levels.LevelFlagsSystem.*;

/**
 *  Rewarding system based only on evaluating the state of the game.
 *  If the game is lost, there will be a negative impact.
 *  If the game is won, the big reward will provide strong connections for policy.
 */
public class PunishIllegalActionsAndDistanceReward implements IRewardSystem {

    @Override
    public double evaluateReward(Level level) {

        if(levelLost)
            return -2000;

        if(levelComplete)
            return 10000;

        // The SPACE_ACTION was initiated
        if(playerPressedSpaceEvent){

            // Unsuccessful attempt
            if(!playerOnTimeMachine && !playerOnGoal){
                return -5;
            }
        }

        // Detect other moves that do not have any effect and punish them
        if(unsuccessfulCommand){
            unsuccessfulCommand = false;
            return -5;
        }

        if(level.getGameObjective().getActiveState()) {
            PointVector playerPosition = GameObjectHandler.GetPlayer().getPosition();
            PointVector objectivePosition = level.getGameObjective().getPosition();
            double distance = playerPosition.distanceTo(objectivePosition);
            double maxDistance = new PointVector().distanceTo(level.getMap().getMaxBounds());

            return -10 * (distance / maxDistance);
        }

        return -0.5;
    }
}
