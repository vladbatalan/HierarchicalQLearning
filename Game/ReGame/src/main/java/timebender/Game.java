package timebender;

import timebender.assets.Assets;
import timebender.gameobjects.controllers.ControllerBuilder;
import timebender.gameobjects.controllers.ExternalController;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.gameobjects.mobs.Player;
import timebender.gameobjects.controllers.KeyboardController;
import timebender.input.ExternalInput;
import timebender.input.KeyInput;
import timebender.input.MouseInput;
import timebender.levels.*;
import timebender.levels.reward.IRewardSystem;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.concurrent.ConcurrentLinkedQueue;

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
     * Configuration properties.
     */
    private String levelString = "SimpleLevel";
    private Boolean keyboardInputType;
    private Boolean manualStep = false;
    private Boolean graphicsMode = true;
    private IRewardSystem rewardSystem;

    private MouseInput mouseInput;
    private KeyInput keyInput;
    private ExternalInput externalInput;

    private Level level;
    private final ConcurrentLinkedQueue<Boolean> stepSignalQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Boolean> endStepQueue = new ConcurrentLinkedQueue<>();
    private LevelStateObject levelDynamicStateObserver;
    private LevelStateObject levelStaticStateObserver;

    public Game() {
        runState = false;
    }

    /**
     * Method responsable for configuring the initialisation parameters of the Game
     */
    public KeyboardController keyboardController;
    public ExternalController externalController;
    public ControllerBuilder controllerBuilder;


    private void initGame() {
        // Only if the game runs on graphics mode
        if (graphicsMode) {
            gameWindow = new GameWindow("Dr. TimeBender", GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
            gameWindow.buildGameWindow();

            // Create input listeners
            mouseInput = new MouseInput(this);
            keyInput = new KeyInput(this);

            // Add listeners
            gameWindow.getCanvas().addMouseListener(mouseInput);
            gameWindow.getCanvas().addMouseMotionListener(mouseInput);
            gameWindow.getJFrame().addKeyListener(keyInput);

        }

        // Add this game to GameObjectHandler
        GameObjectHandler.SetGame(this);
        GameObjectHandler.ClearGameObjects();

        externalInput = new ExternalInput();

        Assets.init();

        // Create player
        Player player = new Player();

        // Create a controllerBuilder for OldPlayerInstances
        controllerBuilder = new ControllerBuilder();

        // Check the source of the input
        if (keyboardInputType) {

            keyboardController = new KeyboardController(player.getBody(), player.id);
            keyboardController.attachObserver(controllerBuilder);
            // Add keyboard to input
            keyInput.addKeyboardController(keyboardController);
            // Attach keyboard controller to player

        } else {
            // Add external input
            externalController = new ExternalController(player.getBody(), player.getId());
            externalInput.addExternalController(externalController);
            externalController.attachObserver(controllerBuilder);
        }

        // Add player to Game Object Handler
        GameObjectHandler.AddGameObject(player);

        // Initialize level
        level = LevelBuilder.CreateLevel(levelString);
        level.initLevelObjects();
        level.resetComplete();

        // Initialize the static level observer
        levelStaticStateObserver = new LevelStateObject().getStateBuilder()
                .setLevelMap(level.getMap())
                .setInitialObjectsState()
                .build();
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

            // Consider the mode of step
            boolean doFrame = false;
            boolean stepSignal = false;

            if (!stepSignalQueue.isEmpty()) {
                stepSignal = stepSignalQueue.poll();
            }

            // In case of automatic step frame
            if (!manualStep && (curentTime - oldTime) > timeFrame) {
                doFrame = true;
            }

            // In case of manual step frame
            else if (manualStep && stepSignal) {
                doFrame = true;
            }

            if (doFrame) {
//                Logger.Print("Frame (" + getCurrentFrame() + ")");
                currentFrame++;
                update();
                if (graphicsMode) {
                    draw();
                }

                oldTime = curentTime;
                if (manualStep) {
                    endStepQueue.add(true);
                }
                updateGameState();
            }
        }
    }

    private void updateGameState() {
        levelDynamicStateObserver = level.getDynamicLevelState();
        if (rewardSystem != null) {
            levelDynamicStateObserver.setReward(rewardSystem.evaluateReward(level));
        }
    }

    /**
     * Method responsible for creating the main thread of the game and running it.
     */
    public synchronized void startGame() {
        if (!runState) {
            runState = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    /**
     * Method responsible for stopping the main thread.
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
        if (graphicsMode) {
            gameWindow.getJFrame().requestFocus();
        }

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

    public void triggerFrameStep() {
        if (manualStep) {
            stepSignalQueue.add(true);
            while (endStepQueue.isEmpty()) {
                Thread.onSpinWait();
            }
            endStepQueue.poll();
        }
    }

    public String collectLevelDynamicStatus() {
        if (levelDynamicStateObserver == null) {
            Logger.Error("Got a null as levelStateObserver!");
        }
        return levelDynamicStateObserver.serializeDynamicComponents();
    }


    public String collectLevelStaticStatus() {
        if (levelStaticStateObserver == null) {
            Logger.Error("Got a null as levelStateObserver!");
        }
        return levelStaticStateObserver.serializeStaticComponents();
    }


    public void restartLevel() {
        if (level != null) {
            level.resetComplete();
        }
    }

    public void subRestartLevel() {
        if (level != null) {
            level.resetSubState();
        }
    }

    public int getCurrentFrame() {
        return level.getFrameNumber();
    }

    public ControllerBuilder getControllerBuilder() {
        return controllerBuilder;
    }

    public ExternalInput getExternalInput() {
        return externalInput;
    }

    public void setLevelCreateString(String levelString) {
        this.levelString = levelString;
    }

    public void setKeyboardInputType(Boolean keyboardInput) {
        this.keyboardInputType = keyboardInput;
    }

    public void setManualStep(Boolean manualStep) {
        this.manualStep = manualStep;
    }

    public void setGraphicsMode(Boolean graphicsMode) {
        // With no graphics mode, the input is external
        if (!graphicsMode)
            this.keyboardInputType = false;
        this.graphicsMode = graphicsMode;
    }

    public void setRewardSystem(IRewardSystem rewardSystem) {
        this.rewardSystem = rewardSystem;
    }

}

