package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.TopHalfTile;

public class PlatformLonelyTile extends TopHalfTile {
    public PlatformLonelyTile() {
        super(Assets.platformBox[3], TileType.PlatformLonelyTile);
    }

    public boolean isSolid() {
        return true;
    }
}

