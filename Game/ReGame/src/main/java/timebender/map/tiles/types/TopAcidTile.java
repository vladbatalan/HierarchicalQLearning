package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.BottomHalfTile;

public class TopAcidTile extends BottomHalfTile {
    public TopAcidTile() {
        super(Assets.acid[0], TileType.TopAcidTile);
    }

    public boolean isSolid() {
        return false;
    }

    public boolean isDeadly() {
        return true;
    }
}
