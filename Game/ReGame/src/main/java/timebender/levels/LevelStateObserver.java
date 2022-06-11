package timebender.levels;

import timebender.map.utils.MapUtils;
import timebender.physics.utils.PointVector;


public class LevelStateObserver {

    private final StateBuilder stateBuilder;

    /**
     * Player position.
     */
    private PointVector playerTilePosition;

    /**
     * The objective properties.
     */
    private PointVector objectiveTilePosition;
    private Boolean objectiveActiveState;

    /**
     * Meta game information.
     */
    private Integer frameNumber;
    private Boolean isLevelRunning;


    public LevelStateObserver() {
        stateBuilder = new StateBuilder(this);
    }

    public static class StateBuilder {
        private final LevelStateObserver levelStateObserver;

        public StateBuilder(LevelStateObserver levelStateObserver) {
            this.levelStateObserver = levelStateObserver;
        }

        public StateBuilder setPlayerTilePosition(PointVector position) {
            levelStateObserver.playerTilePosition = MapUtils.getTileIndexedCoordinates(position);
            return this;
        }

        public StateBuilder setFrameNumber(Integer frameNumber) {
            levelStateObserver.frameNumber = frameNumber;
            return this;
        }


        public StateBuilder setObjective(PointVector position, Boolean activeState) {
            levelStateObserver.objectiveTilePosition = MapUtils.getTileIndexedCoordinates(position);
            levelStateObserver.objectiveActiveState = activeState;
            return this;
        }

        public StateBuilder setLevelRunning(Boolean isLevelRunning) {
            levelStateObserver.isLevelRunning = isLevelRunning;
            return this;
        }

        public LevelStateObserver build() {
            return levelStateObserver;
        }
    }

    public String serialize() {
        return "<GameState>" +
                "<Player>" +
                "<Position x=\"" + (int) playerTilePosition.getX() + "\" y=\"" + (int) playerTilePosition.getY() + "\">" +
                "</Position>" +
                "</Player>" +

                "<Objective>" +
                "<Position x=\"" + (int) objectiveTilePosition.getX() + "\" y=\"" + (int) objectiveTilePosition.getY() + "\">" +
                "</Position>" +
                "<Active>" +
                objectiveActiveState.toString() +
                "</Active>" +
                "</Objective>" +

                // Level state
                "<LevelState>" +
                "<Running>" +
                isLevelRunning.toString() +
                "</Running>" +
                "<Frame>" +
                frameNumber +
                "</Frame>" +
                "</LevelState>" +
                "</GameState>";
    }

    public StateBuilder getStateBuilder() {
        return stateBuilder;
    }
}
