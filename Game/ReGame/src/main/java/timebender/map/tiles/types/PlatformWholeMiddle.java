package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class PlatformWholeMiddle extends WholeTile {
    public PlatformWholeMiddle() {
        super(Assets.wholePlatformBox[1], TileType.PlatformWholeMiddle);
    }

    public boolean isSolid() {
        return true;
    }
}
