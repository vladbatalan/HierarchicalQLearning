package timebender.assets.animations.collections;

import timebender.assets.animations.types.pushbutton.PushYellowButtonTurnOffType1;
import timebender.assets.animations.types.pushbutton.PushYellowButtonTurnOnType1;

public class PushYellowButtonType1AnimationCollection extends AnimationCollection {
    public PushYellowButtonType1AnimationCollection() {
        this.addAnimation(new PushYellowButtonTurnOnType1());
        this.addAnimation(new PushYellowButtonTurnOffType1());
    }
}
