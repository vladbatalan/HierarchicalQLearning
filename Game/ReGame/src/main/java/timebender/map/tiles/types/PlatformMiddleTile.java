package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.TopHalfTile;

public class PlatformMiddleTile extends TopHalfTile {
    public PlatformMiddleTile() {
        super(Assets.platformBox[1], TileType.PlatformMiddleTile);
    }

    public boolean isSolid() {
        return true;
    }
}
