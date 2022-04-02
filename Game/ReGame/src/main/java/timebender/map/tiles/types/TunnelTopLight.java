package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.OnlyTopTile;

public class TunnelTopLight extends OnlyTopTile {
    public TunnelTopLight() {
        super(Assets.tunnelTile[2], TileType.TunnelTopLight);
    }

    public boolean isSolid() {
        return true;
    }
}
