package timebender.assets.animations.collections;

import timebender.assets.animations.types.timemachine.TimeMachineStandAnimation;

public class TimeMachineAnimationCollection extends AnimationCollection {
    public TimeMachineAnimationCollection() {
        this.addAnimation(new TimeMachineStandAnimation());
    }
}
