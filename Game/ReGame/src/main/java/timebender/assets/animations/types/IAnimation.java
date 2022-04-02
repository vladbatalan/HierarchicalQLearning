package timebender.assets.animations.types;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class IAnimation {
    protected ArrayList<BufferedImage> imagesArray = new ArrayList<>();

    //timer
    protected int animationTimer = 0;
    protected int frameDelay = 0;
    protected int waitTime = 5;

    public void displayAnimation(Rectangle shape, Graphics g) {
        g.drawImage(
                imagesArray.get(animationTimer),
                (int) shape.getX(),
                (int) shape.getY(),
                (int) shape.getWidth(),
                (int) shape.getHeight(),
                null);

        updateTimer();
    }

    /**
     * Once every waitTime frames, there will be an update to the next image of the animation.
     */
    protected void updateTimer() {
        int arraySize = imagesArray.size();
        if (arraySize == 0) {
            animationTimer = 0;
            return;
        }

        // Update the delay
        frameDelay = (frameDelay + 1) % waitTime;
        if (frameDelay == 0) {
            animationTimer = (animationTimer + 1) % arraySize;
        }
    }

    public abstract String getAnimationName();
}
