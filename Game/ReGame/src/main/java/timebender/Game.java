package timebender;

import timebender.assets.Assets;
import timebender.gameobjects.controllers.KeyboardController;
import timebender.input.KeyInput;
import timebender.input.MouseInput;
import timebender.map.Map;
import timebender.map.tiles.Tile;
import timebender.physics.Body;
import timebender.physics.PointVector;

import java.awt.*;
import java.awt.image.BufferStrategy;

import static timebender.map.MapBuilder.BuildFromXmlFile;

/**
 * Class responsible for running the instance of the game.
 */
public class Game implements Runnable {
    /**
     * The main window of the game.
     */
    private GameWindow gameWindow;

    /**
     * The variable that describes the state of the game.
     */
    private boolean runState;

    /**
     * The main thread of the game
     */
    private Thread gameThread;

    /**
     * The width of the screen
     */
    public static Integer GAME_WINDOW_WIDTH = 800;
    public static Integer GAME_WINDOW_HEIGHT = 600;

    public float CURRENT_FRAME_TIME = 0;

    /**
     * The map of the game
     */
    private Map gameMap;

    private MouseInput mouseInput;
    private KeyInput keyInput;

    public Game() {
        runState = false;
    }

    /**
     * Method responsable for configuring the initialisation parameters of the Game
     */
    public Body testBody;
    private void initGame() {
        // Only if the game runs on graphics mode
        gameWindow = new GameWindow("Dr. TimeBender", GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        gameWindow.buildGameWindow();


        // Create input listeners
        mouseInput = new MouseInput(this);
        keyInput = new KeyInput(this);

        testBody = new Body(
                new PointVector(0,0),//Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT * 13 - 100),
                20, 40,
                47);
        testBody.setBodyColor(Color.CYAN);
        keyInput.addKeyboardController(new KeyboardController(testBody));

        // Add listeners
        gameWindow.getCanvas().addMouseListener(mouseInput);
        gameWindow.getCanvas().addMouseMotionListener(mouseInput);
        gameWindow.getJFrame().addKeyListener(keyInput);

        Assets.init();

        gameMap = BuildFromXmlFile("/maps/map-text.xml");
    }

    /**
     * Method responsible with the main loop of the game.
     */
    public void run() {
        // Initialise the game object
        initGame();

        // The previous time
        long oldTime = System.nanoTime();

        // The current time
        long curentTime;

        final int framesPerSecond = 60;
        final double timeFrame = 1000000000.0 / framesPerSecond;

        // As long as the thread is running, do the Update() and Draw() methods
        while (runState) {

            curentTime = System.nanoTime();
            CURRENT_FRAME_TIME = (float) ((curentTime - oldTime) / timeFrame);

            if ((curentTime - oldTime) > timeFrame) {
                update();
                draw();
                oldTime = curentTime;
            }
        }
    }


    /**
     * Method responsable for creating the main thread of the game and running it.
     */
    public synchronized void startGame() {
        if (!runState) {
            runState = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    /**
     * Method responsable for stopping the main thread.
     */
    public synchronized void stopGame() {
        if (runState) {
            runState = false;
            try {
                gameThread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Method responsible for actualising the state of the game elements.
     */
    private void update() {
        // Only if Graphics mode is enabled
        gameWindow.getJFrame().requestFocus();

        // To check body
        testBody.update(gameMap);
    }

    /**
     * Method responsible for rendering the graphic elements.
     */
    private void draw() {
        BufferStrategy bufferStrategy = gameWindow.getCanvas().getBufferStrategy();

        if (bufferStrategy == null) {
            try {
                gameWindow.getCanvas().createBufferStrategy(3);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        assert bufferStrategy != null;
        Graphics g = bufferStrategy.getDrawGraphics();

        g.clearRect(0, 0, gameWindow.getWndWidth(), gameWindow.getWndHeight());
        g.setColor(new Color(30, 31, 41));
        g.fillRect(0, 0, gameWindow.getWndWidth(), gameWindow.getWndHeight());

        // Draw the map
        if(gameMap != null) {
            gameMap.draw(g);
            testBody.draw(g);
        }

        bufferStrategy.show();
        g.dispose();
    }

}
