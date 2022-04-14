package timebender.gameobjects.stills;

import timebender.gameobjects.ObjectID;
import timebender.map.Map;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

import java.awt.*;
import java.util.ArrayList;

public class TimedGate extends StillObject implements ISwitchable {

    public static int BODY_WIDTH = 20;
    public static int BODY_HEIGHT = 4;
    public static final float MASS = 50;

    // Color of the gate
    private Color backColor = new Color(0x090D58);

    // Active status of the gate
    private boolean initialActive;
    private boolean isActive;

    // Time in frames to open/close
    private int timeToFinish = 60;
    private float speed;

    // The maximum height a door can reach
    private int gateMovingHeight;

    // The target oscillates between minPosition and maxPosition
    private int targetHeight;
    private int minPosition;
    private int maxPosition;
    // The current height of the door
    private int currentHeight;

    public TimedGate(PointVector position, int gateMovingHeight, boolean isInitiallyActive, Color backColor) {
        super(new Body(position, BODY_WIDTH, BODY_HEIGHT, MASS));
        this.isCollisional = true;
        this.id = ObjectID.TimedGate;
        this.backColor = backColor;
        initTimedGate(position, gateMovingHeight, isInitiallyActive);
    }

    public TimedGate(PointVector position, int gateMovingHeight, boolean isInitiallyActive) {
        super(new Body(position, BODY_WIDTH, BODY_HEIGHT, MASS));
        this.isCollisional = true;
        this.id = ObjectID.TimedGate;
        initTimedGate(position, gateMovingHeight, isInitiallyActive);
    }

    private void initTimedGate(PointVector position, int gateMovingHeight, boolean isInitiallyActive) {
        this.body.setBodyColor(new Color(0x9292CB));
        this.animation = null;

        this.gateMovingHeight = gateMovingHeight;
        this.minPosition = (int) position.getY();
        this.maxPosition = (int) position.getY() - this.gateMovingHeight;

        this.initialActive = isInitiallyActive;
        this.isActive = isInitiallyActive;

        if (this.isActive) {
            targetHeight = getMaxTarget();
        } else {
            targetHeight = getMinTarget();
        }

        this.currentHeight = targetHeight;
        this.speed = (float) gateMovingHeight / timeToFinish;

    }

    @Override
    public void Draw(Graphics g) {
        Rectangle myGate = getGateHitBox();

        // draw door
        g.setColor(backColor);
        g.fillRect(myGate.x, myGate.y, myGate.width, myGate.height);

        // draw body
        body.draw(g);

    }

    @Override
    public void Update(Map currentMap) {
        int diff = currentHeight - targetHeight;
        int oldDif = Math.abs(diff);

        if (diff < 0) {
            currentHeight += speed;
        }

        // door goes up
        if (diff > 0) {
            currentHeight -= speed;
        }

        int newDiff = Math.abs(currentHeight - targetHeight);

        if (oldDif < newDiff)
            currentHeight = targetHeight;
    }

    @Override
    public void turnOn(String command) {
        targetHeight = getMinTarget();
    }

    @Override
    public void turnOff(String command) {
        targetHeight = getMaxTarget();
    }

    public PointVector getSwitchablePosition() {
        return body.getPosition();
    }

    public void setTimeToFinish(int timeToFinish) {
        this.timeToFinish = timeToFinish;
        this.speed = (float) gateMovingHeight / timeToFinish;
    }

    private Rectangle getGateHitBox() {
        int x = (int) body.getPosition().getX() + 5;
        int y = currentHeight;
        int width = 10;
        int height = (int) body.getPosition().getY() - currentHeight;

        return new Rectangle(x, y, width, height);
    }

    public void resetToInitialState() {
        if (initialActive)
            currentHeight = getMaxTarget();
        else
            currentHeight = getMinTarget();
        targetHeight = currentHeight;
    }

    private int getMaxTarget() {
        return maxPosition;
    }

    private int getMinTarget() {
        return minPosition;
    }


    @Override
    public ArrayList<Rectangle> getHitBoxCollection() {
        ArrayList<Rectangle> myHitBoxCollection = new ArrayList<Rectangle>();
        myHitBoxCollection.add(body.getHitBox());
        myHitBoxCollection.add(getGateHitBox());
        return myHitBoxCollection;
    }

}
