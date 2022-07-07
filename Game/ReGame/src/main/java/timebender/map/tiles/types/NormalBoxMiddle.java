package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class NormalBoxMiddle extends WholeTile {
    public NormalBoxMiddle() {
        super(Assets.normalBox[1], TileType.NormalBoxMiddle);
    }

    public boolean isSolid() {
        return true;
    }
}
