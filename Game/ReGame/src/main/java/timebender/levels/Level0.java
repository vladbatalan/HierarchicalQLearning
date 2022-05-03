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

import static timebender.gameobjects.handlers.GameObjectHandler.*;

public class Level0 extends Level {
    public Level0() {
        super("Into the laboratory",
                Objects.requireNonNull(MapBuilder.BuildFromXmlFile("/maps/map-text.xml")));
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
