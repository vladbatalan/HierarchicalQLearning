package timebender.levels;

import timebender.physics.utils.PointVector;

import java.util.ArrayList;

public class LevelStateObserver {

    private final StateBuilder stateBuilder;

    /**
     * Player position.
     */
    private PointVector playerPosition;

    /**
     * The objective properties.
     */
    private PointVector objectivePosition;
    private Boolean objectiveActiveState;


    public LevelStateObserver(){ stateBuilder = new StateBuilder(this);}

    public static class StateBuilder{
        private final LevelStateObserver levelStateObserver;
        public StateBuilder(LevelStateObserver levelStateObserver){
            this.levelStateObserver = levelStateObserver;
        }

        public StateBuilder setPlayerPosition(PointVector position){
            levelStateObserver.playerPosition = position;
            return this;
        }

        public StateBuilder setObjectiveState(PointVector position, Boolean activeState){
            levelStateObserver.objectivePosition = position;
            levelStateObserver.objectiveActiveState = activeState;
            return this;
        }


        public LevelStateObserver build(){
            return levelStateObserver;
        }
    }

    public String serialize(){
        return "";
    }

    public StateBuilder getStateBuilder() {
        return stateBuilder;
    }
}
