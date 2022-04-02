package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.TopHalfTile;

public class HalfNormalBoxLeft extends TopHalfTile {
    public HalfNormalBoxLeft() {
        super(Assets.halfNormalBox[0], TileType.HalfNormalBoxLeft);
    }

    public boolean isSolid() {
        return true;
    }
}
