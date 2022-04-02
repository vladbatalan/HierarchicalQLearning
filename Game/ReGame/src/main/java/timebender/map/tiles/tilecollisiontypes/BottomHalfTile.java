package timebender.map.tiles.tilecollisiontypes;

import timebender.map.tiles.Tile;
import timebender.map.tiles.types.TileType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BottomHalfTile extends Tile {
    public BottomHalfTile(BufferedImage image, TileType tileType) {
        super(image, tileType);
        this.collisionBox = new Rectangle(0, TILE_HEIGHT / 2, TILE_WIDTH, TILE_HEIGHT / 2);
    }
}
