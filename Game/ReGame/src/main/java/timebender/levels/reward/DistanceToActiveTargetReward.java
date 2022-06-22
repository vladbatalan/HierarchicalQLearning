package timebender.levels.reward;

import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.levels.Level;
import timebender.levels.LevelFlagsSystem;
import timebender.physics.utils.PointVector;

/**
 *  Rewarding system based only on evaluating the state of the game.
 *  If the game is lost, there will be a negative impact.
 *  If the game is won, the big reward will provide strong connections for policy.
 *  The reward is proportional to distance to the target.
 */
public class DistanceToActiveTargetReward implements IRewardSystem {

    @Override
    public double evaluateReward(Level level) {

        if(LevelFlagsSystem.levelLost)
            return -5000;

        if(LevelFlagsSystem.levelComplete)
            return 5000;

        if(level.getGameObjective().getActiveState()) {
            PointVector playerPosition = GameObjectHandler.GetPlayer().getPosition();
            PointVector objectivePosition = level.getGameObjective().getPosition();
            double distance = playerPosition.distanceTo(objectivePosition);
            double maxDistance = new PointVector().distanceTo(level.getMap().getMaxBounds());

            return -1 * (distance / maxDistance);
        }

        return -0.5;
    }
}
