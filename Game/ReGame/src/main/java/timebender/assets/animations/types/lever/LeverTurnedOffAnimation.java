package timebender.assets.animations.types.lever;

import timebender.assets.ImageLoader;
import timebender.assets.animations.types.IAnimation;

public class LeverTurnedOffAnimation extends IAnimation {

    public LeverTurnedOffAnimation() {
        imagesArray.add(ImageLoader.LoadImage("/textures/Objects/Switch (2).png"));
    }

    @Override
    public String getAnimationName() {
        return "TurnedOff";
    }
}
