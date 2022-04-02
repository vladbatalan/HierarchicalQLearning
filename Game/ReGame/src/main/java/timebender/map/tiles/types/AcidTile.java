package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class AcidTile extends WholeTile {
    public AcidTile() {
        super(Assets.acid[1], TileType.AcidTile);
    }

    public boolean isSolid() {
        return false;
    }

    public boolean isDeadly() {
        return true;
    }
}
