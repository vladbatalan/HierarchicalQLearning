package timebender.map.xmlobj;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;


@Root(name = "map")
public class MapXmlComponent {
    @Attribute(name="width")
    private int width;

    @Attribute(name="height")
    private int height;

    @ElementArray(entry = "row")
    private String[] tiles;

    @Element(name="backgroundImage", required = false)
    private String backgroundImageUrl;

    public MapXmlComponent(){}

    public MapXmlComponent(int width, int height, String[] tiles, String backgroundImageUrl){
        this.width = width;
        this.height = height;
        this.tiles = tiles;
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public String[] getTiles() {
        return tiles;
    }

    public Integer[][] getMatrix() {

        Integer [][]matrix = new Integer[height][width];
        try {
            for (int row = 0; row < height; row++) {
                String rowString = this.tiles[row];

                String[] items = rowString.split(" ");

                int column = 0;
                for (String s : items) {
                    if (s.isEmpty()) continue;

                    int tileNumber = Integer.parseInt(s);

                   matrix[row][column] = tileNumber;

                    column++;
                }
            }
            return matrix;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }
}

