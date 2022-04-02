package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.TopHalfTile;

public class HalfNormalBoxMiddle extends TopHalfTile {
    public HalfNormalBoxMiddle() {
        super(Assets.halfNormalBox[1], TileType.HalfNormalBoxMiddle);
    }

    public boolean isSolid() {
        return true;
    }
}
