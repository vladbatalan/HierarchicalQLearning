package timebender.levels;

import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.stills.Lever;
import timebender.gameobjects.stills.Objective;
import timebender.gameobjects.stills.TimeMachine;
import timebender.map.MapBuilder;

import java.util.Objects;

import static timebender.gameobjects.handlers.GameObjectHandler.AddGameObject;
import static timebender.gameobjects.handlers.GameObjectHandler.GetPlayer;

public class LeverObjectiveLevel extends Level {
    public LeverObjectiveLevel() {
        super("Lever with Objective Level",
                Objects.requireNonNull(MapBuilder.BuildFromXmlFile("/maps/only-objective.xml")));
    }

    @Override
    public void initLevelObjects() {
        // Create Time machine
        startingTimeMachine = new TimeMachine()
                .positionedInTileCoordinates(2, 13);

        gameObjective = new Objective(false)
                .positionedInTileCoordinates(18, 13);

        Lever timedGateLever = new Lever().positionedInTileCoordinates(6, 13);
        timedGateLever.addAffectedObject(gameObjective);

        // Get player and set camera
        Player player = GetPlayer();

        // If camera exists
        camera.setFollowedObject(player);

        AddGameObject(player);
        AddGameObject(startingTimeMachine);
        AddGameObject(gameObjective);
        AddGameObject(timedGateLever);
    }
}
