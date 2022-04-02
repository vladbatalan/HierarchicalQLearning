package timebender.assets.animations.types.pushbutton;

import timebender.assets.ImageLoader;
import timebender.assets.SpriteSheet;
import timebender.assets.animations.types.IAnimation;

public class PushBlueButtonTurnOnType1 extends IAnimation {

    public PushBlueButtonTurnOnType1() {
        SpriteSheet mySheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Objects/buttons_spritesheet.png"));
        int width = 64;
        int height = 40;

        imagesArray.add(mySheet.crop(1, 0, width, height));
    }

    @Override
    public String getAnimationName() {
        return "TurnedOn";
    }
}
