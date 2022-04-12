package timebender.gameobjects.mobs;


import timebender.assets.animations.collections.PlayerAnimationCollection;
import timebender.gameobjects.ObjectID;
import timebender.gameobjects.stills.TimeMachine;
import timebender.gameobjects.utils.ObjectPlacementUtil;
import timebender.map.Map;
import timebender.map.tiles.Tile;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

import java.awt.*;

import static timebender.gameobjects.utils.ObjectPlacementUtil.*;
import static timebender.physics.enums.Actions.*;

public class Player extends ControllableObject {
    public static int BODY_WIDTH = 20;
    public static int BODY_HEIGHT = 40;
    public static final float PLAYER_MASS = 47f;

    public Player(){
        super(new Body(new PointVector(), BODY_WIDTH, BODY_HEIGHT, PLAYER_MASS));
        this.id = ObjectID.Player;
        this.animation = new PlayerAnimationCollection();
    }

    public Player(PointVector position) {
        super(new Body(position, BODY_WIDTH, BODY_HEIGHT, PLAYER_MASS));
        this.id = ObjectID.Player;
        this.animation = new PlayerAnimationCollection();
    }

    public Player positionedInTileCoordinates(int width, int height){
        SetPositionByTileCoordinates(width, height, body);
        return this;
    }

    public void StandAnimation(Graphics g){

        if(lastAction == MOVE_RIGHT.getValue()) {
            animation.displayAnimation(
                    "StandRightAnimation", body.getHitBox(), g);
        }
        else {
            animation.displayAnimation(
                    "StandLeftAnimation", body.getHitBox(), g);
        }
    }
    public void MoveLeftAnimation(Graphics g){
        animation.displayAnimation(
                "MoveLeftAnimation", body.getHitBox(), g);
    }
    public void MoveRightAnimation(Graphics g){
        animation.displayAnimation(
                "MoveRightAnimation", body.getHitBox(), g);
    }
    public void JumpAnimation(Graphics g){
        if(lastAction == MOVE_RIGHT.getValue()) {
            animation.displayAnimation(
                    "JumpLeftAnimation", body.getHitBox(), g);
        }
        else {
            animation.displayAnimation(
                    "JumpRightAnimation", body.getHitBox(), g);
        }
    }

    @Override
    public void Update(Map currentMap) {
        body.update(currentMap);
    }

}
