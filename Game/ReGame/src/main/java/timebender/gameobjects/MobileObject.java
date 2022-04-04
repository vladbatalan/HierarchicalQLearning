package timebender.gameobjects;

import timebender.physics.Body;

import java.awt.*;

public abstract class MobileObject extends GameObject {

    public MobileObject(Body body) {
        super(body);
        this.isMobile = true;
        this.isCollisional = true;
    }

    public abstract void StandAnimation(Graphics g);

    public abstract void MoveLeftAnimation(Graphics g);

    public abstract void MoveRightAnimation(Graphics g);

    public abstract void JumpAnimation(Graphics g);
}
