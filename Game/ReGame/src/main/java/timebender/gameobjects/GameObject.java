package timebender.gameobjects;


import timebender.assets.animations.collections.AnimationCollection;
import timebender.gameobjects.mobs.Player;
import timebender.map.Map;
import timebender.map.tiles.Tile;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

import java.awt.*;
import java.util.ArrayList;

public abstract class GameObject {
    protected Body body;
    public ObjectID id;
    protected AnimationCollection animation;
    protected boolean isMobile = false;
    protected boolean isCollisional = false;

    public GameObject(Body body) {
        this.body = body;
    }

    public abstract void Draw(Graphics g);
    public abstract void Update(Map currentMap);
    public Body getBody(){
        return body;
    }
    public boolean isDead(){
        return body.getCollisionState()[4];
    }
    public ObjectID getId(){
        return id;
    }
    public boolean isCollisional() {
        return isCollisional;
    }
    public boolean isMobile(){
        return isMobile;
    }

    /**
     * Method responsible for collecting a set of hitboxes for a specific object
     * @return A list of Rectangles.
     */
    public ArrayList<Rectangle> getHitBoxCollection(){
        ArrayList<Rectangle> myHitBoxCollection = new ArrayList<>();
        myHitBoxCollection.add(body.getHitBox());
        return myHitBoxCollection;
    }

    public void resetToInitialState(){
        // do nothing for most of the objects
    }
}
