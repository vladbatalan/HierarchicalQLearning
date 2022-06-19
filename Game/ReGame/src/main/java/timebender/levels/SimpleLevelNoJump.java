package timebender.levels;

import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.stills.Objective;
import timebender.gameobjects.stills.TimeMachine;
import timebender.map.MapBuilder;

import java.util.Objects;

import static timebender.gameobjects.handlers.GameObjectHandler.AddGameObject;
import static timebender.gameobjects.handlers.GameObjectHandler.GetPlayer;

public class SimpleLevelNoJump extends Level {
    public SimpleLevelNoJump() {
        super("Harciog land easy",
                Objects.requireNonNull(MapBuilder.BuildFromXmlFile("/maps/simple-map-no-jump.xml")));
    }

    @Override
    public void initLevelObjects() {
        // Create Time machine
        startingTimeMachine = new TimeMachine()
                .positionedInTileCoordinates(2, 13);

        gameObjective = new Objective(true)
                .positionedInTileCoordinates(18, 13);

        // Get player and set camera
        Player player = GetPlayer();

        // If camera exists
        camera.setFollowedObject(player);

        AddGameObject(player);
        AddGameObject(startingTimeMachine);
        AddGameObject(gameObjective);
    }
}
