package timebender.gameobjects;


import timebender.assets.animations.collections.PlayerAnimationCollection;
import timebender.map.Map;
import timebender.physics.Body;
import timebender.physics.PointVector;
import timebender.physics.enums.Actions;

import java.awt.*;

import static timebender.physics.enums.Actions.*;

public class Player extends ControllableObject {
    public static int BODY_WIDTH = 20;
    public static int BODY_HEIGHT = 40;
    public static final float PLAYER_MASS = 47f;


    public Player(PointVector position) {
        super(new Body(position, BODY_WIDTH, BODY_HEIGHT, PLAYER_MASS));
        this.id = ObjectID.Player;
        this.animation = new PlayerAnimationCollection();
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
