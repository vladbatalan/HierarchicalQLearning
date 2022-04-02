package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.OnlyBottomTile;

public class TunnelBottom extends OnlyBottomTile {
    public TunnelBottom() {
        super(Assets.tunnelTile[0], TileType.TunnelBottom);
    }

    public boolean isSolid() {
        return true;
    }
}
