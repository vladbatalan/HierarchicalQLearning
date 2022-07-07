package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class NormalBoxRight extends WholeTile {
    public NormalBoxRight() {
        super(Assets.normalBox[2], TileType.NormalBoxRight);
    }

    public boolean isSolid() {
        return true;
    }
}
