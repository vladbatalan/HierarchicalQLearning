package timebender.assets.animations.collections;

import timebender.assets.animations.types.lever.LeverTurnedOffAnimation;
import timebender.assets.animations.types.lever.LeverTurnedOnAnimation;

public class LeverAnimationColection extends AnimationCollection {
    public LeverAnimationColection() {
        this.addAnimation(new LeverTurnedOffAnimation());
        this.addAnimation(new LeverTurnedOnAnimation());
    }
}
