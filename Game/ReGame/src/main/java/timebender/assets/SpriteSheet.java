package timebender.assets;

import java.awt.image.BufferedImage;

/**
 * Class responsible for representing a sprite sheet
 */
public class SpriteSheet
{
    private final BufferedImage spriteSheet;
    public static final int tileWidth   = 256;
    public static final int tileHeight  = 256;


    public SpriteSheet(BufferedImage buffImg)
    {
        /// Retine referinta catre BufferedImage object.
        spriteSheet = buffImg;
    }


    /**
     * Returns a BufferedImaged that contains a subimage.
     * The subimage is located as reference on top-left starting point.
     * @param x The tile number on x axix
     * @param y The tile number on y axis
     * @param width The tile width
     * @param height The tile height
     * @return The resulting image
     */
    public BufferedImage crop(int x, int y, int width, int height)
    {
        return spriteSheet.getSubimage(x * width, y * height, width, height);
    }
}
