package timebender.assets.animations.types.lever;

import timebender.assets.ImageLoader;
import timebender.assets.animations.types.IAnimation;

public class LeverTurnedOnAnimation extends IAnimation {

    public LeverTurnedOnAnimation() {
        imagesArray.add(ImageLoader.LoadImage("/textures/Objects/Switch (1).png"));
    }

    @Override
    public String getAnimationName() {
        return "TurnedOn";
    }
}
