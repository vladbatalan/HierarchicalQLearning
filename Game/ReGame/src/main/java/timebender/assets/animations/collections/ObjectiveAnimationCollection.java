package timebender.assets.animations.collections;

import timebender.assets.animations.types.objective.ObjectiveClosedAnimation;
import timebender.assets.animations.types.objective.ObjectiveOpenAnimation;

public class ObjectiveAnimationCollection extends AnimationCollection {
    public ObjectiveAnimationCollection() {
        this.addAnimation(new ObjectiveOpenAnimation());
        this.addAnimation(new ObjectiveClosedAnimation());
    }
}
