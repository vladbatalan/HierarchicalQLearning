package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class NoTile extends WholeTile {
    public NoTile() {
        super(Assets.noTile, TileType.NoTile);
    }
}
