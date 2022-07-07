package timebender.map.tiles.types;

import timebender.assets.Assets;
import timebender.map.tiles.TileType;
import timebender.map.tiles.tilecollisiontypes.WholeTile;

import java.awt.*;

public class NoTileColor extends WholeTile {
    public NoTileColor() {
        super(Assets.noTile, TileType.NoTileColor);
        backcolor = new Color(30, 31, 41);
    }
}
