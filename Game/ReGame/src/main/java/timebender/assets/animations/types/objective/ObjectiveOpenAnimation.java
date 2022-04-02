package timebender.assets.animations.types.objective;

import timebender.assets.ImageLoader;
import timebender.assets.animations.types.IAnimation;

public class ObjectiveOpenAnimation extends IAnimation {

    public ObjectiveOpenAnimation() {
        imagesArray.add(ImageLoader.LoadImage("/textures/Objects/DoorOpen.png"));
    }

    @Override
    public String getAnimationName() {
        return "OpenAnimation";
    }
}
