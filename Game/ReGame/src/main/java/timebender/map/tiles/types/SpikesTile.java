package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.BottomHalfTile;

public class SpikesTile extends BottomHalfTile {
    public SpikesTile() {
        super(Assets.spikes, TileType.SpikesTile);
    }

    public boolean isSolid() {
        return false;
    }

    public boolean isDeadly() {
        return true;
    }
}
