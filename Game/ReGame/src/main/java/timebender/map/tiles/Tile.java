package timebender.map.tiles;

import timebender.map.tiles.types.TileType;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class responsible for handeling the structure of a Tile
 */
public class Tile {

    public static final int TILE_WIDTH = 30;
    public static final int TILE_HEIGHT = 30;

    protected Rectangle collisionBox = new Rectangle(0, 0, TILE_WIDTH, TILE_HEIGHT);
    protected BufferedImage img;
    protected TileType tileType;
    protected Color backcolor = new Color(0, 0, 0, 0);

    public Tile(BufferedImage image, TileType tileType) {
        img = image;
        this.tileType = tileType;
    }

    /**
     * Method responsible for updating the Tile if needed
     */
    public void update() {
    }

    /**
     * Method responsible for drawing the tile to a specific position
     *
     * @param g           The graphic component
     * @param widthCoord  The x axis
     * @param heightCoord The y axis
     */
    public void draw(Graphics g, int widthCoord, int heightCoord) {

        // Draw the Tile
        g.setColor(backcolor);
        g.fillRect(widthCoord, heightCoord, TILE_WIDTH, TILE_HEIGHT);
        g.drawImage(img, widthCoord, heightCoord, TILE_WIDTH, TILE_HEIGHT, null);
    }

    /**
     * Method responsible for returning if the object is solid or not
     *
     * @return A boolean that represents if the Tile is solid or not
     */
    public boolean isSolid() {
        return false;
    }

    /**
     * Methor responsible for returning if the object is deadly or not
     *
     * @return A boolean that represents if the Tile is Deadly or not
     */
    public boolean isDeadly() {
        return false;
    }

    /**
     * Method responsible for checking the collision with an object
     *
     * @param relativeX The relative x coordinate within the Tile
     * @param relativeY The relative y coordinate withn the Tile
     * @return A boolean value that describes if a collision is happening
     */
    public boolean onCollision(float relativeX, float relativeY) {
        if (collisionBox.getX() <= relativeX && relativeX <= collisionBox.getX() + collisionBox.getWidth())
            return collisionBox.getY() <= relativeY && relativeY <= collisionBox.getY() + collisionBox.getHeight();
        return false;
    }

    public TileType getId() {
        return tileType;
    }

    public String toString() {
        return tileType.getValue() + "";
    }


    public void setBackcolor(Color backcolor) {
        this.backcolor = backcolor;
    }
}
