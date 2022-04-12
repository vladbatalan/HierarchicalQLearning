package timebender.gameobjects.utils;

import timebender.map.tiles.Tile;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

public class ObjectPlacementUtil {
    public static void SetPositionByTileCoordinates(int width, int height, Body body){
        PointVector position = new PointVector(
                width * Tile.TILE_WIDTH + body.getBodyWidth()/2,
                height * Tile.TILE_HEIGHT - body.getBodyHeight());
        body.setPosition(position);
    }
}
