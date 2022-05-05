package timebender.gameobjects.mobs;

import timebender.assets.animations.collections.OldPlayerAnimationCollection;
import timebender.gameobjects.ObjectID;
import timebender.gameobjects.controllers.FixedController;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.map.Map;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

import java.awt.*;

import static timebender.gameobjects.utils.ObjectPlacementUtil.SetPositionByTileCoordinates;
import static timebender.physics.enums.Actions.MOVE_LEFT;
import static timebender.physics.enums.Actions.MOVE_RIGHT;

public class OldPlayerInstance extends ControllableObject {
    private FixedController controller;
    public static int BODY_WIDTH = 20;
    public static int BODY_HEIGHT = 40;
    public static final float PLAYER_MASS = 47f;


    public OldPlayerInstance(PointVector position, FixedController controller) {
        super(new Body(position, BODY_WIDTH, BODY_HEIGHT, PLAYER_MASS));
        this.controller = controller;
        this.id = ObjectID.OldPlayerInstance;
        this.animation = new OldPlayerAnimationCollection();
    }


    public OldPlayerInstance(FixedController controller) {
        super(new Body(new PointVector(), BODY_WIDTH, BODY_HEIGHT, PLAYER_MASS));
        this.controller = controller;
        this.id = ObjectID.OldPlayerInstance;
        this.animation = new OldPlayerAnimationCollection();
    }

    public OldPlayerInstance positionedInTileCoordinates(int width, int height) {
        SetPositionByTileCoordinates(width, height, body);
        return this;
    }

    public void StandAnimation(Graphics g) {
        if (lastAction == MOVE_RIGHT.getValue()) //moved right last time
            animation.displayAnimation("StandRightAnimation", body.getHitBox(), g);
        else
            animation.displayAnimation("StandLeftAnimation", body.getHitBox(), g);
    }

    public void MoveLeftAnimation(Graphics g) {
        animation.displayAnimation("MoveLeftAnimation", body.getHitBox(), g);
    }

    public void MoveRightAnimation(Graphics g) {
        animation.displayAnimation("MoveRightAnimation", body.getHitBox(), g);
    }

    public void JumpAnimation(Graphics g) {
        if (lastAction == MOVE_LEFT.getValue()) //moved left last time
            animation.displayAnimation("JumpLeftAnimation", body.getHitBox(), g);
        else
            animation.displayAnimation("JumpRightAnimation", body.getHitBox(), g);
    }

    @Override
    public void Update(Map currentMap) {
        // We need to ask the controller to perform commands before updating the OldInstance
        controller.execute(GameObjectHandler.GetFrameNumber(), body);

        // Update the body
        body.update(currentMap);
    }

    @Override
    public void resetToInitialState(){
        controller.resetController();
        body.resetMoveState();
    }

    public FixedController getController() {
        return controller;
    }

    public boolean isSpaceEvent(){
        return controller.isSpaceEvent();
    }
    public boolean isCommandsFinishedEvent(){
        return controller.isCommandsFinishedEvent();
    }


}
