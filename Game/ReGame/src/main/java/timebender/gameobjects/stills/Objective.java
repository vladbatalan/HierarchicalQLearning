package timebender.gameobjects.stills;

import timebender.assets.animations.collections.ObjectiveAnimationCollection;
import timebender.gameobjects.ObjectID;
import timebender.map.Map;
import timebender.map.tiles.Tile;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

import java.awt.*;

import static timebender.gameobjects.utils.ObjectPlacementUtil.SetPositionByTileCoordinates;

public class Objective extends StillObject implements ISwitchable {
    private static final int BODY_WIDTH = 40;
    private static final int BODY_HEIGHT = 80;
    private static final int MASS = 50;
    // tells if the door to the next level is open or not
    private boolean isActive;

    public Objective(boolean isInitiallyActive){
        super(new Body(new PointVector(), BODY_WIDTH, BODY_HEIGHT, MASS));
        this.id = ObjectID.Objective;
        this.animation = new ObjectiveAnimationCollection();
        this.isActive = isInitiallyActive;
    }

    public Objective(PointVector position, boolean isInitiallyActive){
        super(new Body(position, BODY_WIDTH, BODY_HEIGHT, MASS));
        this.id = ObjectID.Objective;
        this.animation = new ObjectiveAnimationCollection();
        this.isActive = isInitiallyActive;
    }

    public Objective positionedInTileCoordinates(int width, int height) {
        SetPositionByTileCoordinates(width, height, body);
        return this;
    }

    @Override
    public void Draw(Graphics g) {
        if(isActive)
            animation.displayAnimation("OpenAnimation", body.getHitBox(), g);
        else
            animation.displayAnimation("ClosedAnimation", body.getHitBox(), g);
    }

    @Override
    public void Update(Map currentMap) {}

    @Override
    public void turnOn(String command) {
        isActive = true;
    }

    @Override
    public void turnOff(String command) {
        isActive = false;
    }

    public boolean getActiveState(){
        return isActive;
    }
    public PointVector getSwitchablePosition(){
        return body.getPosition();
    }

    @Override
    public Boolean isActive() {
        return isActive;
    }
}
