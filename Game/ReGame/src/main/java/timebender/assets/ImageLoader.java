package timebender.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Class that contains a static method for getting an image from memory.
 */
public class ImageLoader
{

    public static BufferedImage LoadImage(String path)
    {
        try
        {
            return ImageIO.read(ImageLoader.class.getResource(path));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }}

