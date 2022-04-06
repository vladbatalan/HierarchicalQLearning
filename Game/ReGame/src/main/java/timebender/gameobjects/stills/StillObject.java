package timebender.gameobjects.stills;

import timebender.gameobjects.GameObject;
import timebender.map.Map;
import timebender.physics.Body;

import java.awt.*;

public abstract class StillObject extends GameObject {

    public StillObject(Body body) {
        super(body);
        this.isMobile = false;
        this.isCollisional = true;
    }

    @Override
    public void Update(Map currentMap) {}
}
