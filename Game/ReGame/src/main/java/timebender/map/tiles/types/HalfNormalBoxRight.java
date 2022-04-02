package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.TopHalfTile;

public class HalfNormalBoxRight extends TopHalfTile {
    public HalfNormalBoxRight() {
        super(Assets.halfNormalBox[2], TileType.HalfNormalBoxRight);
    }

    public boolean isSolid() {
        return true;
    }
}
