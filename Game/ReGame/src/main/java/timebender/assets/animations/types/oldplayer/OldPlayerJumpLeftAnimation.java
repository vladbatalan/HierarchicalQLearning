package timebender.assets.animations.types.oldplayer;

import timebender.assets.ImageLoader;
import timebender.assets.SpriteSheet;
import timebender.assets.animations.types.IAnimation;

public class OldPlayerJumpLeftAnimation extends IAnimation {

    public OldPlayerJumpLeftAnimation() {
        //init assets
        SpriteSheet playerSpriteSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Player/OldPlayerSprite.png"));
        //the size of the tile to be cropped
        int width = 32;
        int height = 64;
        imagesArray.add(playerSpriteSheet.crop(0, 1, width, height));
    }

    @Override
    public String getAnimationName() {
        return "JumpLeftAnimation";
    }
}
