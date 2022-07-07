package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class NormalBoxLeft extends WholeTile {
    public NormalBoxLeft() {
        super(Assets.normalBox[0], TileType.NormalBoxLeft);
    }

    public boolean isSolid() {
        return true;
    }
}
