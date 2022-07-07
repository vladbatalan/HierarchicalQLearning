package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class PlatformWholeLeft extends WholeTile {
    public PlatformWholeLeft() {
        super(Assets.wholePlatformBox[0], TileType.PlatformWholeLeft);
    }

    public boolean isSolid() {
        return true;
    }
}
