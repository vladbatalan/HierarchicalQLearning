package timebender.assets.animations.collections;

import timebender.assets.animations.types.oldplayer.*;

public class OldPlayerAnimationCollection extends AnimationCollection {
    public OldPlayerAnimationCollection() {
        this.addAnimation(new OldPlayerMoveLeftAnimation());
        this.addAnimation(new OldPlayerMoveRightAnimation());
        this.addAnimation(new OldPlayerJumpLeftAnimation());
        this.addAnimation(new OldPlayerJumpRightAnimation());
        this.addAnimation(new OldPlayerStandRightAnimation());
        this.addAnimation(new OldPlayerStandLeftAnimation());
    }
}
