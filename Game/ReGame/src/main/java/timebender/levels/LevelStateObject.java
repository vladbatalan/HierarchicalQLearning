package timebender.levels;

import timebender.gameobjects.ObjectID;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.gameobjects.stills.ISwitchable;
import timebender.gameobjects.stills.StillObject;
import timebender.map.Map;
import timebender.map.tiles.Tile;
import timebender.map.utils.MapUtils;
import timebender.physics.utils.PointVector;

import javax.swing.plaf.nimbus.State;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static timebender.map.tiles.Tile.TILE_HEIGHT;
import static timebender.map.tiles.Tile.TILE_WIDTH;


public class LevelStateObject {

    private final StateBuilder stateBuilder;

    /**
     * Static aspects of a level.
     * This includes the map and the position of all still objects.
     */
    private Map levelMap;
    private final HashMap<ObjectID, List<PointVector>> initialObjPosition = new HashMap<>();

    // Dynamic properties of the level
    /**
     * Player position.
     */
    private PointVector playerTilePosition;

    /**
     * The state properties.
     */
    private final HashMap<ObjectID, List<Boolean>> activeStates = new HashMap<>();
    private int objectsWithStates = 0;
    private int numberOfOldInstances = 0;

    /**
     * Meta game information.
     */
    private Integer frameNumber;
    private Boolean isLevelRunning;
    private Boolean levelLost;
    private Boolean levelComplete;
    private double reward;


    public LevelStateObject() {
        stateBuilder = new StateBuilder(this);
    }

    public static class StateBuilder {
        private final LevelStateObject levelStateObserver;

        public StateBuilder(LevelStateObject levelStateObserver) {
            this.levelStateObserver = levelStateObserver;
        }

        // Set for dynamic parts of a level
        public StateBuilder setPlayerTilePosition(PointVector position, int divisions) {
            PointVector indexedPosition = MapUtils.getTileIndexedCoordinates(position);

            float diffX = position.getX() - (indexedPosition.getX() * TILE_WIDTH);
            float diffY = position.getY() - (indexedPosition.getY() * TILE_HEIGHT);

            float divisionSizeX = (float)(TILE_WIDTH)/divisions;
            float divisionSizeY = (float)(TILE_HEIGHT)/divisions;

            float addX = (float) (divisionSizeX * Math.floor(diffX/divisionSizeX)/(float)TILE_WIDTH);
            float addY = (float) (divisionSizeY * Math.floor(diffY/divisionSizeY)/(float)TILE_HEIGHT);

            indexedPosition.setX(indexedPosition.getX() + addX);
            indexedPosition.setY(indexedPosition.getY() + addY + 1);

            levelStateObserver.playerTilePosition = indexedPosition;

            return this;
        }

        public StateBuilder setNumberIfOldInstances(int instances){
            levelStateObserver.numberOfOldInstances = instances;
            return this;
        }

        public StateBuilder setFrameNumber(Integer frameNumber) {
            levelStateObserver.frameNumber = frameNumber;
            return this;
        }

        public StateBuilder setLevelRunning(Boolean isLevelRunning) {
            levelStateObserver.isLevelRunning = isLevelRunning;
            return this;
        }

        public StateBuilder setLevelLost(Boolean isLevelLost) {
            levelStateObserver.levelLost = isLevelLost;
            return this;
        }

        public StateBuilder setLevelComplete(Boolean isLevelComplete) {
            levelStateObserver.levelComplete = isLevelComplete;
            return this;
        }

        public StateBuilder setFrameReward(double reward){
            levelStateObserver.reward = reward;
            return this;
        }

        public StateBuilder setStillObjectsState() {
            HashMap<ObjectID, List<Boolean>> activeHash = levelStateObserver.activeStates;

            for (StillObject stillObject : GameObjectHandler.GetStillObjects()) {
                ObjectID stillID = stillObject.getId();

                // Only if the object implements ISwitchable
                if (stillObject instanceof ISwitchable) {
                    ISwitchable switchableOfStill = (ISwitchable) stillObject;

                    if (activeHash.containsKey(stillID)) {
                        activeHash.get(stillID).add(switchableOfStill.isActive());
                    } else {
                        List<Boolean> newList = new ArrayList<>();
                        newList.add(switchableOfStill.isActive());
                        activeHash.put(stillID, newList);
                    }
                }
            }

            return this;
        }


        // Set for static parts of a level
        public StateBuilder setLevelMap(Map levelMap) {
            levelStateObserver.levelMap = levelMap;
            return this;
        }

        public StateBuilder setInitialObjectsState() {
            HashMap<ObjectID, List<PointVector>> positionHash = levelStateObserver.initialObjPosition;
            positionHash.clear();
            for (StillObject stillObject : GameObjectHandler.GetStillObjects()) {
                ObjectID stillID = stillObject.getId();

                // These are objects that don't affect the game
                if (stillID.equals(ObjectID.TurnOffObjectEffect) || stillID.equals(ObjectID.TurnOnObjectEffect))
                    continue;

                PointVector tilePosition =
                        MapUtils.getTileIndexedCoordinates(
                                stillObject.getPosition().add(
                                        new PointVector(0, stillObject.getBody().getBodyHeight() - TILE_HEIGHT )
                                )
                        );
                tilePosition.setY(tilePosition.getY() - 2);

                if (stillObject instanceof ISwitchable)
                    levelStateObserver.objectsWithStates ++;

                if (positionHash.containsKey(stillID)) {
                    positionHash.get(stillID).add(tilePosition);
                } else {
                    List<PointVector> newList = new ArrayList<>();
                    newList.add(tilePosition);
                    positionHash.put(stillID, newList);
                }
            }
            return this;
        }


        public LevelStateObject build() {
            return levelStateObserver;
        }
    }

    public String serializeDynamicComponents() {

        StringBuilder sb = new StringBuilder();
        sb.append("<GameState>");

        // Data about Player
        NumberFormat formatter = new DecimalFormat("0.00");
        String positionX = formatter.format(playerTilePosition.getX());
        String positionY = formatter.format(playerTilePosition.getY());

        sb.append("<Player>");
        sb.append("<Position x=\"")
                .append(positionX)
                .append("\" y=\"")
                .append(positionY)
                .append("\">");
        sb.append("</Position>");
        sb.append("</Player>");

        // Data about the state of the level
        sb.append("<LevelState>");
        sb.append("<Running>").append(isLevelRunning).append("</Running>");
        sb.append("<Complete>").append(levelComplete).append("</Complete>");
        sb.append("<Lost>").append(levelLost).append("</Lost>");
        sb.append("<Frame>").append(frameNumber).append("</Frame>");
        sb.append("<Reward>").append(reward).append("</Reward>");
        sb.append("<OldInstances>").append(numberOfOldInstances).append("</OldInstances>");
        sb.append("</LevelState>");

        // Append states of all Switchable
        sb.append("<States>");
        for (ObjectID objectID : activeStates.keySet()) {
            sb.append("<").append(objectID.name()).append(">");

            for (int i = 0; i < activeStates.get(objectID).size(); i++) {
                Boolean currentActive = activeStates.get(objectID).get(i);

                sb.append("<Active>");
                sb.append(currentActive);
                sb.append("</Active>");
            }

            sb.append("</").append(objectID.name()).append(">");
        }
        sb.append("</States>");

        sb.append("</GameState>");

        return sb.toString();
    }


    public String serializeStaticComponents() {
        StringBuilder sb = new StringBuilder();

        sb.append("<GameState>");

        // TODO: Manage halfTiles -> For now consider them noTile?
        // Map component where:
        // 0 -> NoTile
        // 1 -> SolidTile
        // 2 -> DeadlyTile
        sb.append("<Map rows=\"")
                .append(levelMap.getTileMatrix().size())
                .append("\">");

        for (ArrayList<Tile> row : levelMap.getTileMatrix()) {
            sb.append("<row>");

            for (int col = 0; col < row.size(); col++) {
                Tile tile = row.get(col);

                // Value based on properties of the tile
                if(tile.isDeadly()){
                    sb.append(2);
                }else if(tile.isSolid()){
                    sb.append(1);
                }else{
                    sb.append(0);
                }

                // Delimitator
                if(col != row.size()-1){
                    sb.append(" ");
                }
            }
            sb.append("</row>");
        }
        sb.append("</Map>");

        sb.append("<Positions>");
        // Add the initial position of all StillObjects
        for(ObjectID objectID : initialObjPosition.keySet()){
            sb.append("<").append(objectID.name()).append(">");

            for(int i = 0; i < initialObjPosition.get(objectID).size(); i++){
                PointVector position = initialObjPosition.get(objectID).get(i);

                sb.append("<Position ")
                                .append("x=\"").append((int)position.getX()).append("\" ")
                                .append("y=\"").append((int)position.getY()).append("\">");
                sb.append("</Position>");
            }

            sb.append("</").append(objectID.name()).append(">");
        }
        sb.append("</Positions>");

        sb.append("<ExtraStates>").append(objectsWithStates).append("</ExtraStates>");


        sb.append("</GameState>");

        return sb.toString();
    }


    public StateBuilder getStateBuilder() {
        return stateBuilder;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }
}
