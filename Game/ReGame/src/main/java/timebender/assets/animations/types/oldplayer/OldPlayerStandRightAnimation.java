package timebender.assets.animations.types.oldplayer;

import timebender.assets.ImageLoader;
import timebender.assets.SpriteSheet;
import timebender.assets.animations.types.IAnimation;

public class OldPlayerStandRightAnimation extends IAnimation {

    public OldPlayerStandRightAnimation() {
        //init assets
        SpriteSheet playerSpriteSheet = new SpriteSheet(ImageLoader.LoadImage("/textures/Player/OldPlayerSprite.png"));
        //the size of the tile to be cropped
        int width = 32;
        int height = 64;
        imagesArray.add(playerSpriteSheet.crop(1, 0, width, height));
    }

    @Override
    public String getAnimationName() {
        return "StandRightAnimation";
    }
}
