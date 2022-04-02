package timebender.assets.animations.collections;

import timebender.assets.animations.types.pushbutton.PushRedButtonTurnOffType1;
import timebender.assets.animations.types.pushbutton.PushRedButtonTurnOnType1;

public class PushRedButtonType1AnimationCollection extends AnimationCollection {
    public PushRedButtonType1AnimationCollection() {
        this.addAnimation(new PushRedButtonTurnOnType1());
        this.addAnimation(new PushRedButtonTurnOffType1());
    }
}
