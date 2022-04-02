package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

public class BlackBoxLeft extends WholeTile {
    public BlackBoxLeft() {
        super(Assets.blackBoxes[0], TileType.BlackBoxLeft);
    }

    public boolean isSolid() {
        return true;
    }
}
