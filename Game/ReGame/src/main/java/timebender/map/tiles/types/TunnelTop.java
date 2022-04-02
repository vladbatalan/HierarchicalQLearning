package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.OnlyTopTile;

public class TunnelTop extends OnlyTopTile {
    public TunnelTop() {
        super(Assets.tunnelTile[1], TileType.TunnelTop);
    }

    public boolean isSolid() {
        return true;
    }
}
