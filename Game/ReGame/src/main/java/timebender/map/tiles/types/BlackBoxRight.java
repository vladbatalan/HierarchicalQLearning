package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class BlackBoxRight extends WholeTile {
    public BlackBoxRight() {
        super(Assets.blackBoxes[1], TileType.BlackBoxRight);
    }

    public boolean isSolid() {
        return true;
    }
}
