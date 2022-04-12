package timebender.gameobjects.camera;


import timebender.Game;
import timebender.gameobjects.GameObject;
import timebender.physics.utils.PointVector;

import java.awt.*;

public class GameCamera {
    private PointVector position;

    private final int maxWidth;
    private final int maxHeight;

    private GameObject followedObject;

    // Specify if camera is on
    private boolean cameraOn = false;

    public GameCamera(int mapSizeWidth, int mapSizeHeight) {
        this.maxWidth = Math.max(mapSizeWidth - Game.GAME_WINDOW_WIDTH, 0);
        this.maxHeight = Math.max(mapSizeHeight - Game.GAME_WINDOW_HEIGHT, 0);
        position = new PointVector();
    }

    public GameCamera(PointVector mapBounds) {
        this.maxWidth = (int) Math.max(mapBounds.getX() - Game.GAME_WINDOW_WIDTH, 0);
        this.maxHeight = (int) Math.max(mapBounds.getY() - Game.GAME_WINDOW_HEIGHT, 0);
        position = new PointVector();
    }

    public void update(Graphics g) {

        //camera must be turned on
        if (!cameraOn) return;

        //daca urmareste un obiect
        if (followedObject != null) {

            PointVector followedPosition = followedObject.getBody().getPosition();
            PointVector cameraDisplacement =
                    new PointVector((float) Game.GAME_WINDOW_WIDTH / 2, (float) Game.GAME_WINDOW_HEIGHT * 2 / 3);

            PointVector newCameraCoords = followedPosition.sub(cameraDisplacement);

            setPosition(newCameraCoords);
        }

        //move the camera
        g.translate(-(int) position.getX(), -(int) position.getY());
    }

    public void start() {
        cameraOn = true;
        this.reset();
    }

    public void cameraStop() {
        cameraOn = false;
    }


    public void reset() {
        position = new PointVector();
    }


    public void setFollowedObject(GameObject obj) {
        this.followedObject = obj;
    }

    public void setPosition(PointVector position) {
        //no negative coordonates
        if (position.getX() < 0)
            position.setX(0);
        if (position.getY() < 0)
            position.setY(0);

        //set camera to maximum coords possible
        this.position.setX(Math.min(position.getX(), maxWidth));
        this.position.setY(Math.min(position.getY(), maxHeight));
    }

    public PointVector getPosition() {
        return position;
    }
}
