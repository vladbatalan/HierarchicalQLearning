package timebender.assets.animations.collections;

import timebender.assets.animations.types.pushbutton.PushGreenButtonTurnOffType1;
import timebender.assets.animations.types.pushbutton.PushGreenButtonTurnOnType1;

public class PushGreenButtonType1AnimationCollection extends AnimationCollection {
    public PushGreenButtonType1AnimationCollection() {
        this.addAnimation(new PushGreenButtonTurnOnType1());
        this.addAnimation(new PushGreenButtonTurnOffType1());
    }
}
