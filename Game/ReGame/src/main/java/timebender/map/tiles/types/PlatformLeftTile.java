package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.TopHalfTile;

public class PlatformLeftTile extends TopHalfTile {
    public PlatformLeftTile() {
        super(Assets.platformBox[0], TileType.PlatformLeftTile);
    }

    public boolean isSolid() {
        return true;
    }
}
