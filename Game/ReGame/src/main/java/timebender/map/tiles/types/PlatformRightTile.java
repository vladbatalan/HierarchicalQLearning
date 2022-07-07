package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.TopHalfTile;

public class PlatformRightTile extends TopHalfTile {
    public PlatformRightTile() {
        super(Assets.platformBox[2], TileType.PlatformRightTile);
    }

    public boolean isSolid() {
        return true;
    }
}
