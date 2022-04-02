package timebender.assets.animations.collections;

import timebender.assets.animations.types.scalepan.ScalePanAnimation;

import java.awt.*;

public class ScalePanAnimationCollection extends AnimationCollection {
    public ScalePanAnimationCollection() {
        this.addAnimation(new ScalePanAnimation());
    }

    public ScalePanAnimationCollection(Color myBackgroundColor) {
        this.addAnimation(new ScalePanAnimation(myBackgroundColor));
    }
}
