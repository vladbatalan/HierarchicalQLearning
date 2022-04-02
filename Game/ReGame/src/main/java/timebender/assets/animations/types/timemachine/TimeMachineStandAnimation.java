package timebender.assets.animations.types.timemachine;

import timebender.assets.ImageLoader;
import timebender.assets.animations.types.IAnimation;

public class TimeMachineStandAnimation extends IAnimation {

    public TimeMachineStandAnimation() {
        imagesArray.add(ImageLoader.LoadImage("/textures/Objects/Time_Machine_HD.png"));
    }

    @Override
    public String getAnimationName() {
        return "StandAnimation";
    }
}
