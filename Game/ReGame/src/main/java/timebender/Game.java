package timebender;

import timebender.assets.Assets;
import timebender.gameobjects.controllers.ControllerBuilder;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.controllers.KeyboardController;
import timebender.input.KeyInput;
import timebender.input.MouseInput;
import timebender.levels.Level0;
import timebender.levels.Level;
import timebender.levels.SimpleLevel;

import java.awt.*;
import java.awt.image.BufferStrategy;

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
    public int currentFrame = 0;

    /**
     * The map of the game
     */

    private MouseInput mouseInput;
    private KeyInput keyInput;
    private Level level;

    public Game() {
        runState = false;
    }

    /**
     * Method responsable for configuring the initialisation parameters of the Game
     */
    public KeyboardController keyboardController;
    public ControllerBuilder controllerBuilder;

    private void initGame() {
        // Only if the game runs on graphics mode
        gameWindow = new GameWindow("Dr. TimeBender", GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        gameWindow.buildGameWindow();

        // Add this game to GameObjectHandler
        GameObjectHandler.SetGame(this);
        GameObjectHandler.ClearGameObjects();

        // Create input listeners
        mouseInput = new MouseInput(this);
        keyInput = new KeyInput(this);

        // Add listeners
        gameWindow.getCanvas().addMouseListener(mouseInput);
        gameWindow.getCanvas().addMouseMotionListener(mouseInput);
        gameWindow.getJFrame().addKeyListener(keyInput);

        Assets.init();

        // Create player
        Player player = new Player();
        // Attach keyboard controller to player
        keyboardController = new KeyboardController(player.getBody(), player.id);
        // Add keyboard to input
        keyInput.addKeyboardController(keyboardController);

        // Create a controllerBuilder for OldPlayerInstances
        controllerBuilder = new ControllerBuilder();
        keyboardController.attachObserver(controllerBuilder);

        // Add player to Game Object Handler
        GameObjectHandler.AddGameObject(player);

        // Initialize level
        level = new SimpleLevel();
        level.initLevelObjects();
        level.resetComplete();
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
                currentFrame++;
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

        // Update the level
        level.update();
    }

    /**
     * Method responsible for rendering the graphic elements.
     */
    private void draw() {
        BufferStrategy bufferStrategy = gameWindow.getCanvas().getBufferStrategy();

        if (bufferStrategy == null) {
            try {
                gameWindow.getCanvas().createBufferStrategy(2);
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

        // Draw the level
        level.draw(g);

        bufferStrategy.show();
        g.dispose();
    }

    public int getCurrentFrame() {
        return level.getFrameNumber();
    }

    public ControllerBuilder getControllerBuilder() {
        return controllerBuilder;
    }
}

