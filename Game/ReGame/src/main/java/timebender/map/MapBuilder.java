package timebender.map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import timebender.Logger;
import timebender.map.tiles.Tile;
import timebender.map.tiles.types.TileFactory;
import timebender.map.xmlobj.MapXmlComponent;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class MapBuilder {

    public static Map BuildFromXmlFile(String filePath) {
        try {
            Serializer serializer = new Persister();
            URL resource = MapBuilder.class.getResource(filePath);

            File resourceFile = new File(resource.toURI());

            MapXmlComponent mapXmlComponent = serializer.read(MapXmlComponent.class, resourceFile);

            return createMapFromXML(mapXmlComponent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map createMapFromXML(MapXmlComponent xmlComponent){

        Integer mapTilesHeight = xmlComponent.getHeight();
        Integer mapTilesWidth = xmlComponent.getWidth();

        Map result = new Map(mapTilesWidth, mapTilesHeight);

        ArrayList<ArrayList<Tile>> tileMatrix = result.getTileMatrix();
        Integer[][] tileMatrixFromComponent = xmlComponent.getMatrix();

        // Transform element list to ArrayList of ArrayList
        int heightIndex, widthIndex;
        for (heightIndex = 0; heightIndex < xmlComponent.getHeight(); heightIndex++) {
            ArrayList<Tile> tmp = new ArrayList<>();

            for (widthIndex = 0; widthIndex < xmlComponent.getWidth(); widthIndex++) {

                int tileNumber = tileMatrixFromComponent[heightIndex][widthIndex];

                // Create tile of type
                Tile tile = TileFactory.CreateTile(tileNumber);

                // Add tile to row
                tmp.add(tile);
            }

            tileMatrix.add(tmp);
        }

        if(xmlComponent.getBackgroundImageUrl() != null) {
            // Add background if exists
            result.setBackground(xmlComponent.getBackgroundImageUrl());
        }


        return result;
    }

}
