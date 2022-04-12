package timebender.map;


import timebender.Game;
import timebender.assets.ImageLoader;
import timebender.gameobjects.camera.GameCamera;
import timebender.map.tiles.Tile;
import timebender.map.tiles.types.TileFactory;
import timebender.map.utils.MapUtils;
import timebender.physics.utils.PointVector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static timebender.map.tiles.Tile.TILE_HEIGHT;
import static timebender.map.tiles.Tile.TILE_WIDTH;
import static timebender.physics.enums.CollisionTypes.DEADLY;
import static timebender.physics.utils.CollisionUtils.getSideCollisionPoints;


/**
 * Class responsible for handling the creation of the map and handling the collisions with the tiles
 */

public class Map {

    private final int MIN_WIDTH = 28;
    private final int MIN_HEIGHT = 13;
    private final ArrayList<ArrayList<Tile>> tileMatrix = new ArrayList<>();

    private int mapTilesWidth;
    private int mapTilesHeight;

    private BufferedImage backgroundImage;
    private GameCamera camera;

    public Map(int mapTilesWidth, int mapTilesHeight) {
        this.mapTilesWidth = mapTilesWidth;
        this.mapTilesHeight = mapTilesHeight;
    }

    public Map(String fileName) {

        try {
            URL resource = getClass().getResource(fileName);
            File myObj = new File(resource.toURI());
            Scanner myReader = new Scanner(myObj);

            String[] firstLine = myReader.nextLine().split(" ");
            mapTilesHeight = Integer.parseInt(firstLine[0]);
            mapTilesWidth = Integer.parseInt(firstLine[1]);

            for (int i = 0; i < mapTilesHeight; i++) {

                if (myReader.hasNextLine()) {

                    //creez o noua lista care contine date despre harta mea
                    ArrayList<Tile> temp = new ArrayList<>();

                    //citesc prima linie
                    String data = myReader.nextLine();
                    String[] numbers = data.split(" ");

                    for (String s : numbers) {
                        if (!s.isEmpty()) {

                            //limitez la numarul de elemente specificate in antet
                            if (temp.size() < mapTilesWidth) {
                                temp.add(TileFactory.CreateTile(Integer.parseInt(s)));
                            } else {
                                throw new Exception("Wrong map format: width specified does not match with " +
                                        "the given number of tiles on row " + i + "!");
                            }
                        }
                    }
                    if (temp.size() < mapTilesWidth) {
                        throw new Exception("Wrong map format: width specified does not match with the given number" +
                                " of tiles on row " + i + "!");
                    }
                    tileMatrix.add(temp);
                } else {
                    throw new Exception("Wrong map format: height specified does not match " +
                            "with the given number of tiles!");
                }
            }
            myReader.close();

            // extending the bounds of the map
            mapExtend();

        } catch (FileNotFoundException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {

            System.out.println("Map invalid format exception occured.");
            e.printStackTrace();
        }
    }


    /**
     * Method responsible for updating the Map
     */
    public void update() {
        // No updated needed for the map object
        // If map tiles can be changed in the future
    }

    /**
     * Method responsible for drawing the Map
     *
     * @param g The graphic context
     */
    public void draw(Graphics g) {

        // The placement of the background considering the game camera position
        if (backgroundImage != null) {

            int backgroundX = 0;
            int backgroundY = 0;

            if (camera != null) {

                backgroundX = (int) camera.getPosition().getX();
                backgroundY = (int) camera.getPosition().getY();
            }

            g.drawImage(backgroundImage, backgroundX, backgroundY,
                    Game.GAME_WINDOW_WIDTH, Game.GAME_WINDOW_HEIGHT, null);
        }

        // Draw only visible tiles
        Rectangle cameraSquare = new Rectangle(0, 0, Game.GAME_WINDOW_WIDTH, Game.GAME_WINDOW_HEIGHT);

        if(camera != null){
            cameraSquare = camera.getCameraSquare();
        }

        for (int row = 0; row < mapTilesHeight; row++) {
            for (int column = 0; column < mapTilesWidth; column++) {

                int rightSide = (column + 1) * TILE_WIDTH;
                int leftSide = column * TILE_WIDTH;
                int bottomSide = (row + 1) * TILE_HEIGHT;
                int topSide = row * TILE_HEIGHT;

                // Only if tile is within camera, it will be drawn
                if (rightSide >= cameraSquare.getX() &&
                        leftSide <= cameraSquare.getX() + cameraSquare.getWidth()) {

                    if (bottomSide >= cameraSquare.getY() &&
                            topSide <= cameraSquare.getY() + cameraSquare.getHeight()) {

                        Tile selectedTile = getTileByIndex(column, row);

                        selectedTile.draw(g, column * TILE_WIDTH, row * TILE_HEIGHT);
                    }
                }
            }
        }

    }

    /**
     * Method responsible for extending the Map if the bounds are smaller than the minimum size.
     */
    private void mapExtend() {

        if (mapTilesHeight < MIN_HEIGHT) {

            ArrayList<Tile> lastRow = tileMatrix.get(tileMatrix.size() - 1);

            for (int heightIndex = tileMatrix.size(); heightIndex < MIN_HEIGHT; heightIndex++) {
                tileMatrix.add((ArrayList<Tile>) lastRow.clone());
            }

            mapTilesHeight = MIN_HEIGHT;
        }

        if (mapTilesWidth < MIN_WIDTH) {

            for (int heightIndex = 0; heightIndex < mapTilesHeight; heightIndex++) {

                Tile last = getTileByIndex(mapTilesWidth - 1, heightIndex);
                for (int index = mapTilesWidth; index < MIN_WIDTH; index++) {

                    Tile newTile = TileFactory.CreateTile(last.getId());
                    tileMatrix.get(heightIndex).add(newTile);
                }
            }

            mapTilesWidth = MIN_WIDTH;
        }
    }

    /**
     * Method responsible for checking the collision of a given rectangle with the solid tiles on the Map
     *
     * @param rect The coordinates of the object that interracts with the map
     * @return A list of booleans that denote the types of collision resulted
     */
    public boolean[] checkCollision(Rectangle rect) {
        //0 - top collision
        //1 - right collision
        //2 - bottom collision
        //3 - left collision
        //4 - deadly collision
        boolean[] collision = new boolean[5];

        // points where we will check the collisions with the tiles on the map
        PointVector[] checkingPoints = getSideCollisionPoints(rect);

        int totalPoints = checkingPoints.length;
        int totalSidePoints = totalPoints / 4;

        for (int i = 0; i < totalPoints; i++) {

            PointVector currentPointToCheck = checkingPoints[i];

            Tile currentTile = getTileByCoordinates(currentPointToCheck);

            int sideCode = i / totalSidePoints;

            if (MapUtils.checkIfPointInTile(currentPointToCheck, currentTile)) {

                if (currentTile.isDeadly()) {
                    collision[DEADLY.getValue()] = true;
                }
                if (currentTile.isSolid()) {
                    collision[sideCode] = true;
                }
            }
        }
        return collision;
    }

    public Tile getTileByIndex(int column, int row) {
        return tileMatrix.get(row).get(column);
    }

    /**
     * Gets the tile by in map coordinates.
     *
     * @param coordinates A 2D vector with the cordinates. First item is width and the second is the height.
     * @return a tile.
     */
    public Tile getTileByCoordinates(PointVector coordinates) {

        PointVector indexedCoordinates = MapUtils.getTileIndexedCoordinates(coordinates);

        int indexWidth = (int) indexedCoordinates.getX();
        int indexHeight = (int) indexedCoordinates.getY();

        return getTileByIndex(indexWidth, indexHeight);
    }

    /**
     * Gets the tile by in map coordinates.
     *
     * @param xCoordinate The width coordinate of the tile.
     * @param yCoordinate The height coordinate of the tile.
     * @return A tile.
     */
    public Tile getTileByCoordinates(float xCoordinate, float yCoordinate) {
        return getTileByCoordinates(new PointVector(xCoordinate, yCoordinate));
    }

    public PointVector getMaxBounds() {
        PointVector bounds = new PointVector();
        bounds.setX(tileMatrix.get(0).size() * TILE_HEIGHT);
        bounds.setY(tileMatrix.size() * TILE_WIDTH);
        return bounds;
    }

    public void setCamera(GameCamera camera) {
        this.camera = camera;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setBackground(String imagePath) {
        this.backgroundImage = ImageLoader.LoadImage(imagePath);
    }

    public int getMapTilesHeight() {
        return mapTilesHeight;
    }

    public int getMapTilesWidth() {
        return mapTilesWidth;
    }

    public ArrayList<ArrayList<Tile>> getTileMatrix() {
        return tileMatrix;
    }
}
