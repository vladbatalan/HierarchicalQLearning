package timebender.assets.animations.collections;

import timebender.assets.animations.types.pushbutton.PushBlueButtonTurnOffType1;
import timebender.assets.animations.types.pushbutton.PushBlueButtonTurnOnType1;

public class PushBlueButtonType1AnimationCollection extends AnimationCollection {
    public PushBlueButtonType1AnimationCollection() {
        this.addAnimation(new PushBlueButtonTurnOnType1());
        this.addAnimation(new PushBlueButtonTurnOffType1());
    }
}
