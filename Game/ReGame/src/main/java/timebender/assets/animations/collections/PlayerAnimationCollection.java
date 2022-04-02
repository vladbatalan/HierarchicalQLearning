package timebender.assets.animations.collections;

import timebender.assets.animations.types.player.*;

public class PlayerAnimationCollection extends AnimationCollection {
    public PlayerAnimationCollection() {
        this.addAnimation(new PlayerMoveLeftAnimation());
        this.addAnimation(new PlayerMoveRightAnimation());
        this.addAnimation(new PlayerJumpLeftAnimation());
        this.addAnimation(new PlayerJumpRightAnimation());
        this.addAnimation(new PlayerStandRightAnimation());
        this.addAnimation(new PlayerStandLeftAnimation());
    }
}
