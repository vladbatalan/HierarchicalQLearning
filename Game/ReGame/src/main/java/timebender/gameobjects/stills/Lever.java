package timebender.gameobjects.stills;


import timebender.assets.animations.collections.LeverAnimationColection;
import timebender.gameobjects.ObjectID;
import timebender.gameobjects.effects.TurnObjectEffect;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.gameobjects.mobs.MobileObject;
import timebender.gameobjects.utils.ObjectCollisionUtil;
import timebender.map.Map;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static timebender.gameobjects.utils.ObjectPlacementUtil.SetPositionByTileCoordinates;

public class Lever extends StillObject implements ISwitch {
    public static int BODY_WIDTH = 30;
    public static int BODY_HEIGHT = 60;
    public static final float MASS = 50;

    private final ArrayList<SwitchCommand> affectedByThis = new ArrayList<>();
    private boolean isSwitched;

    public Lever() {
        super(new Body(new PointVector(), BODY_WIDTH, BODY_HEIGHT, MASS));
        this.id = ObjectID.Lever;
        this.animation = new LeverAnimationColection();
        isSwitched = false;
    }

    public Lever(PointVector position) {
        super(new Body(position, BODY_WIDTH, BODY_HEIGHT, MASS));
        this.id = ObjectID.Lever;
        this.animation = new LeverAnimationColection();
        isSwitched = false;
    }

    public Lever positionedInTileCoordinates(int width, int height) {
        SetPositionByTileCoordinates(width, height, body);
        return this;
    }

    @Override
    public void Draw(Graphics g) {
        if (isSwitched)
            animation.displayAnimation("TurnedOn", body.getHitBox(), g);
        else
            animation.displayAnimation("TurnedOff", body.getHitBox(), g);
    }

    @Override
    public void Update(Map currentMap) {
        ConcurrentLinkedQueue<MobileObject> mobileObjectList = GameObjectHandler.GetMobileObjects();

        // Keep the old state of the Lever
        boolean oldSwitchState = isSwitched;
        isSwitched = false;

        // Check if any entity interacts with the Lever
        for (MobileObject mobile : mobileObjectList) {
            if (ObjectCollisionUtil.isThereCollisionBetween(this, mobile)) {
                isSwitched = true;
                break;
            }
        }

        // Only if the state changed
        if (isSwitched != oldSwitchState) {

            // Iterate through all things affected by the Lever
            for (SwitchCommand myCommand : affectedByThis) {

                ISwitchable affectedObject = myCommand.switchable;
                boolean front = myCommand.frontOfChange;
                boolean createTurnOn = !((front && !isSwitched) || (!front && isSwitched));

                // Create moving effect
                createTurningEffect(affectedObject, myCommand.command, createTurnOn);
            }
        }
    }

    private void createTurningEffect(ISwitchable affected, String myCommand, boolean type) {
        TurnObjectEffect turnObjectEffect = new TurnObjectEffect(type, body.getPosition(), affected, myCommand);
        GameObjectHandler.AddGameObject(turnObjectEffect);
    }

    public void addAffectedObject(ISwitchable obj, boolean frontOfChange) {
        addAffectedObject(obj, frontOfChange, "");
    }

    public void addAffectedObject(ISwitchable obj, boolean frontOfChange, String command) {
        SwitchCommand myCommand = new SwitchCommand(obj, command, frontOfChange);

        // If object already in list, just update the status
        for (int index = 0; index < affectedByThis.size(); index++) {

            // Get the current pair
            SwitchCommand switchCommand = affectedByThis.get(index);

            // Found the object referred
            if (switchCommand.switchable == obj) {
                affectedByThis.set(index, switchCommand);
                return;
            }
        }

        // New object in list, add it
        affectedByThis.add(myCommand);
    }

    public void addAffectedObject(ISwitchable obj) {
        addAffectedObject(obj, true, "");
    }


}
