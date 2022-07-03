package timebender.levels;

import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.stills.Lever;
import timebender.gameobjects.stills.Objective;
import timebender.gameobjects.stills.TimeMachine;
import timebender.gameobjects.stills.TimedGate;
import timebender.map.MapBuilder;
import timebender.map.tiles.Tile;
import timebender.physics.utils.PointVector;

import java.util.Objects;

import static timebender.gameobjects.handlers.GameObjectHandler.AddGameObject;
import static timebender.gameobjects.handlers.GameObjectHandler.GetPlayer;

public class Level0Easier extends Level {
    public Level0Easier() {
        super("Into the simplified laboratory",
                Objects.requireNonNull(MapBuilder.BuildFromXmlFile("/maps/level0-simple-map.xml")));
    }

    @Override
    public void initLevelObjects() {

        // Create Time machine
        startingTimeMachine = new TimeMachine()
                .positionedInTileCoordinates(2, 13);

        gameObjective = new Objective(false)
                .positionedInTileCoordinates(7, 13);

        TimedGate timedGate = new TimedGate(new PointVector(Tile.TILE_WIDTH * 17 + 12, Tile.TILE_HEIGHT * 13 - 4),
                3 * Tile.TILE_HEIGHT - 4, true);

        Lever timedGateLever = new Lever().positionedInTileCoordinates(14, 13);
        timedGateLever.addAffectedObject(timedGate);

        Lever objectiveLever = new Lever().positionedInTileCoordinates(3, 21);
        objectiveLever.addAffectedObject(gameObjective);

        // Get player and set camera
        Player player = GetPlayer();

        // If camera exists
        camera.setFollowedObject(player);

        AddGameObject(player);
        AddGameObject(startingTimeMachine);
        AddGameObject(gameObjective);
        AddGameObject(objectiveLever);
        AddGameObject(timedGateLever);
        AddGameObject(timedGate);
    }
}
