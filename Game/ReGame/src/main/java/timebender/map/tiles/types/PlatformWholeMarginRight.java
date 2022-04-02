package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class PlatformWholeMarginRight extends WholeTile {
    public PlatformWholeMarginRight() {
        super(Assets.wholePlatformBox[4], TileType.PlatformWholeMarginRight);
    }

    public boolean isSolid() {
        return true;
    }
}
