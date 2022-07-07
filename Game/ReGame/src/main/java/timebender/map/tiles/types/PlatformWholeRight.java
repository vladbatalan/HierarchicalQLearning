package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class PlatformWholeRight extends WholeTile {
    public PlatformWholeRight() {
        super(Assets.wholePlatformBox[2], TileType.PlatformWholeRight);
    }

    public boolean isSolid() {
        return true;
    }
}
