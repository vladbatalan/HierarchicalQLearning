package timebender.map.utils;

import timebender.map.tiles.Tile;
import timebender.physics.utils.PointVector;

import static timebender.map.tiles.Tile.TILE_HEIGHT;
import static timebender.map.tiles.Tile.TILE_WIDTH;

public class MapUtils {

    /**
     * Method responsible for checking if a point on the map has interaction with a Tile
     *
     * @param point The coordinates of the point
     * @param tile  The Tile the point interacts with
     * @return A boolean value which desribes if the point is inside the collision box of the Tile
     */
    public static boolean checkIfPointInTile(PointVector point, Tile tile) {

        // Get point relative tot Tile
        float x, y;
        PointVector indexes = getTileIndexedCoordinates(point);
        x = point.getX() - indexes.getX() * TILE_WIDTH;
        y = point.getY() - indexes.getY() * TILE_HEIGHT;

        PointVector relativePoints = new PointVector(x, y);

        return tile.onCollision(relativePoints.getX(), relativePoints.getY());
    }

    /**
     * Method responsible for getting the row and the column of a Tile on the Map by a Point
     *
     * @param pointOnMap Represents a point on the Map
     * @return A PointVector that represents the pair of indexes of the Tile containing the given coordinate.
     */
    public static PointVector getTileIndexedCoordinates(PointVector pointOnMap) {
        return new PointVector((int) (pointOnMap.getX() / TILE_WIDTH), (int) (pointOnMap.getY() / TILE_HEIGHT));
    }

}
