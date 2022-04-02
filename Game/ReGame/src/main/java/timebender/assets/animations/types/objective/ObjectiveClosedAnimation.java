package timebender.assets.animations.types.objective;

import timebender.assets.ImageLoader;
import timebender.assets.animations.types.IAnimation;

public class ObjectiveClosedAnimation extends IAnimation {

    public ObjectiveClosedAnimation() {
        imagesArray.add(ImageLoader.LoadImage("/textures/Objects/DoorLocked.png"));
    }

    @Override
    public String getAnimationName() {
        return "ClosedAnimation";
    }
}
