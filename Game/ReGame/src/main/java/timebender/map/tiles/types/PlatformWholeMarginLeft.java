package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class PlatformWholeMarginLeft extends WholeTile {
    public PlatformWholeMarginLeft() {
        super(Assets.wholePlatformBox[3], TileType.PlatformWholeMarginLeft);
    }

    public boolean isSolid() {
        return true;
    }
}
