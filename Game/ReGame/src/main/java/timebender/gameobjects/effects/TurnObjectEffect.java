package timebender.gameobjects.effects;

// TurnOnObjectEffect is special designed to be created when a switch changes it's state and tundOn an object
// The effect created is the one that at the starting point this object will be created and will move
//                                                 in timeUntilDissapear ticks over the destination (endPosition)

import timebender.gameobjects.ObjectID;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.gameobjects.stills.ISwitchable;
import timebender.gameobjects.stills.StillObject;
import timebender.map.Map;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

import java.awt.*;

public class TurnObjectEffect extends StillObject {
    private PointVector velocity;
    private ISwitchable targetObject;
    private final int framesUntilDisappear = 30;
    private String command;
    private boolean turn;


    public TurnObjectEffect(boolean turn, PointVector startPosition, ISwitchable targetObj) {
        super(new Body(startPosition, 5, 5, 0));
        initTurnEffect(turn, startPosition, targetObj, "");
    }

    public TurnObjectEffect(boolean turn, PointVector startPosition, ISwitchable targetObj, String command){
        super(new Body(startPosition, 5, 5, 0));
        initTurnEffect(turn, startPosition, targetObj, command);
    }

    private void initTurnEffect(boolean turn, PointVector startPosition, ISwitchable targetObj, String command){
        this.turn = turn;

        if(turn) {
            this.id = ObjectID.TurnOnObjectEffect;
            this.body.setBodyColor(new Color(0x5BFF6A));
        }
        else {
            this.id = ObjectID.TurnOffObjectEffect;
            this.body.setBodyColor(new Color(0xFF0002));
        }

        this.targetObject = targetObj;
        this.command = command;

        // Evaluate the velocity as difference between target position and end position
        velocity = targetObj.getSwitchablePosition().sub(startPosition);

        // Set the velocity module
        velocity = velocity.scalarMultiply(1/(float) framesUntilDisappear);
    }

    public void Draw(Graphics g) {
        body.draw(g);
    }

    public void Update(Map currentMap) {

        PointVector oldPosition = body.getPosition();

        // Set the new position of the effect
        body.setPosition(oldPosition.add(velocity));

        // The left distance to the target
        float distanceTo = body.getPosition().distanceTo(targetObject.getSwitchablePosition());

        // Finish reached, command to turn on and remove object
        if(distanceTo < velocity.abs()){

            if(turn) {
                // Command the turn on
                targetObject.turnOn(command);
            }
            else{
                // Command the turn off
                targetObject.turnOff(command);
            }

            // Destroy the object
            GameObjectHandler.RemoveGameObject(this);
        }
    }

    public int getFramesUntilDisappear(){
        return framesUntilDisappear;
    }
}
