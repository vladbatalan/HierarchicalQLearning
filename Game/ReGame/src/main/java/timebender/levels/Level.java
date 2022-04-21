package timebender.levels;


import timebender.gameobjects.GameObject;
import timebender.gameobjects.camera.GameCamera;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.gameobjects.stills.Objective;
import timebender.gameobjects.stills.TimeMachine;
import timebender.map.Map;

import java.awt.*;

public abstract class Level {
    protected Level nextLevel;
//    protected ScreenTag mainScreenTag;
//    protected ScreenTag mainScreenTagShadow;
    protected String levelName;
    protected Map currentMap;

//    protected Timer gameTimer;
//
//    protected ButtonCollection buttons = new ButtonCollection();

    protected TimeMachine startTimeMachine;
    protected Objective gameObjective;
//    protected ControllerBuilder myControllerBuilder;

    //camera
    protected GameCamera camera;

    //Flags
    //protected Boolean isLevelRunning = false;
    //protected Boolean isOnReset = false;

    //constructor
    public Level(){
//        buttons.AddElement(new RestartGameButton(
//                new Rectangle(-5, 550, 130, 25),
//                "Restart Level",
//                new Point()
//                ));
//
//        buttons.AddElement(new ReturnFromGameToMainMenuButton(
//                new Rectangle(700, 550, 105, 25),
//                "Menu",
//                new Point()
//        ));
    }

    //Update and Draw basic guidelines
    public void Update(){
//        if(LevelFlagsSystem.isLevelRunning) {
//            if (currentMap != null) {
//                handler.Update(currentMap);
//                checkDeathConditions();
//            } else {
//                System.out.println("A map has not been initialised for the update!");
//            }
//        }
    }
    public void Draw(Graphics g){
        //sort of background color for map
        g.setColor(new Color(0x6ED1C5));
        g.setColor(new Color(0xF8F77F));
        g.fillRect(0, 0, (int)currentMap.getMaxBounds().getX(), (int)currentMap.getMaxBounds().getY());

        camera.update(g);
        currentMap.draw(g);
        GameObjectHandler.Draw(g);

//        displayTextEvents(g);

//        buttons.Draw(g);
    }

    protected void InitGenericLevel(){
        // Camera running
        camera = new GameCamera((int)currentMap.getMaxBounds().getX(), (int)currentMap.getMaxBounds().getY());
//        for(MyButton btn : buttons.getButtonCollection()){
//            btn.setCamera(camera);
//        }
        currentMap.setCamera(camera);
        //display the level screen tag
//        mainScreenTag = new ScreenTag(levelName,40, 40, 100, 40 , camera);
//        mainScreenTagShadow = new ScreenTag(levelName,40, 40, 100, 40, camera);
//        mainScreenTagShadow.setColor(Color.BLACK);

        //if gameStateChanges stop the screen tags
//        mainScreenTagShadow.addTimerIntreruptor(new GameStateGameIntrerupter());
//        mainScreenTag.addTimerIntreruptor(new GameStateGameIntrerupter());

        //adding a Timmer on the screen
//        gameTimer = new Timer(new PointVector(400, Game.GAME_WINDOW_HEIGHT - 30), camera);
        //gameTimer.setBackground(new Color(0xB3DAFF), 5);
//        gameTimer.setDisplayColor(new Color(0xFFE332));
    }

    public void resetLevel(){
//        LevelFlagsSystem.resetFlagStates();
//        myControllerBuilder = new ControllerBuilder(new Controller());

        //new Player and Old Instances
//        spawnMobileAtStartingPoint();
        camera.start();
        camera.setFollowedObject(GameObjectHandler.GetPlayer());

        // All Old Instances are brought back to life
//        handler.renewOldInstances();
//        handler.resetInitialLevelPosition();


        //apply BlackFadeIn
//        IAction fadeInAction = new FadeInColorAction(Color.black, 1, 120, camera);
//        IActionTimer fadeInEffect = new PeriodicFiniteActionTimer(fadeInAction, 1, 120);
//        fadeInEffect.addTimerIntreruptor(new GameStateGameIntrerupter());
//        fadeInEffect.startTimer();
//
//        IAction resetManagementAction = new DelaySetResetFalseFlagAction(this);
//        IActionTimer resetFalse = new DelayedActionTimer(resetManagementAction, 120);
//        resetFalse.startTimer();
//
//        mainScreenTagShadow.showScreenTag(Game.GAME_WINDOW_WIDTH/2+3, Game.GAME_WINDOW_HEIGHT/6+3);
//        mainScreenTag.showScreenTag(Game.GAME_WINDOW_WIDTH/2, Game.GAME_WINDOW_HEIGHT/6);
//        LevelFlagsSystem.isLevelRunning = true;
//        LevelFlagsSystem.isOnReset = true;

        //restart timer
//        gameTimer.showTimer();
//        gameTimer.restartTimer();
    }

    //death conditions
//    public void checkDeathConditions(){
//        boolean dead = false;
//        GameObject objectOfInterest = null;
//        String deathMessage = "You Died!";
//
//        //to be improved!
//        if(handler.getPlayer().isDead()) {
//            dead = true;
//            objectOfInterest = handler.getPlayer();
//        }
//
//        for(int index = 0; index < handler.getMobileObjects().size(); index ++){
//            if(handler.getMobileObjects().get(index).getId() == ObjectID.OldPlayerInstance &&
//                    handler.getMobileObjects().get(index).isDead()){
//                dead = true;
//                objectOfInterest = handler.getMobileObjects().get(index);
//                deathMessage = "Grandfather paradox! Past self Died!";
//            }
//        }
//
//        if(LevelFlagsSystem.isInstanceOnParadox){
//            objectOfInterest = LevelFlagsSystem.onParadoxMob;
//            dead = true;
//            deathMessage = "Paradox! Could't time travel!";
//        }
//
//
//        if(dead){
//            if(objectOfInterest != null)
//                camera.setFollowedObject(objectOfInterest);
//
//            // Clear all Old Instances
//            handler.clearOldInstances();
//
//            // timers and fades
//            IAction fadeOutAction = new FadeOutColorAction(Color.black, 1, 240, camera);
//            IAction resetLevelOnDelay = new ResetLevelDelay(this);
//            IActionTimer fadeOutEffect = new PeriodicFiniteActionTimer(fadeOutAction, 1, 240);
//            IActionTimer resetLevel = new DelayedActionTimer(resetLevelOnDelay, 239);
//
//            //interuptors
//            fadeOutEffect.addTimerIntreruptor(new GameStateGameIntrerupter());
//            resetLevel.addTimerIntreruptor(new GameStateGameIntrerupter());
//
//            LevelFlagsSystem.isLevelRunning = false;
//            fadeOutEffect.startTimer();
//            resetLevel.startTimer();
//            gameTimer.stopTimer();
//
//            LevelFlagsSystem.enablePlayerControl = false;
//
//
//            ScreenTag deathTag = new ScreenTag(deathMessage,30, 20, 80, 20 , camera);
//            ScreenTag deathTagShadow = new ScreenTag(deathMessage,30, 20, 80, 20, camera);
//            deathTagShadow.setColor(Color.BLACK);
//            deathTag.setColor(Color.red);
//
//            //if game state changes stop ScreenTags
//            deathTag.addTimerIntreruptor(new GameStateGameIntrerupter());
//            deathTagShadow.addTimerIntreruptor(new GameStateGameIntrerupter());
//
//            deathTagShadow.showScreenTag(Game.GAME_WINDOW_WIDTH/2+3, Game.GAME_WINDOW_HEIGHT/6+3);
//            deathTag.showScreenTag(Game.GAME_WINDOW_WIDTH/2, Game.GAME_WINDOW_HEIGHT/6);
//        }
//    }
//
//    protected void spawnMobileAtStartingPoint(){
//        //Player
//        PointVector displacement1 = new PointVector((float)Player.body_width/2, startTimeMachine.getBody().getBodyHeight() - Player.body_height);
//        Player player = new Player(startTimeMachine.getBody().getPosition().add(displacement1));
//        handler.setPlayer(player);
//
//        //OldInstances
//        PointVector displacement2 = new PointVector((float)OldPlayerInstance.body_width/2, startTimeMachine.getBody().getBodyHeight() - (float)OldPlayerInstance.body_height);
//        PointVector startingPosition = startTimeMachine.getBody().getPosition().add(displacement2);
//
//        for(OldPlayerInstance instance:handler.getOldInstances()){
//            instance.getBody().setPosition(startingPosition);
//            instance.getController().resetController();
//        }
//    }
//
//    public void addOldInstance(){
//        myControllerBuilder.finishControllerBuild(gameTimer.getCurrentTime());
//        OldPlayerInstance instance = new OldPlayerInstance(new PointVector(), myControllerBuilder.getMyController());
//        handler.addOldInstance(instance);
//    }
//
//    public GameObjectHandler getHandler() {
//        return handler;
//    }
//
//    //sets all the required objects into the level
//    //adds a map, a camera, game objects, a timmer
//    public void displayTextEvents(Graphics g){
//        // here it does nothing
//        // but during a level, there will be moments when some texts should be displayed at specific times
//    }
//
//    // manages the changes when the player has finished a level
//    public void LevelWinConditionAchieved(){
//        // timers and fades
//        LevelFlagsSystem.isLevelRunning = false;
//        gameTimer.stopTimer();
//
//
//        IAction fadeOutAction = new FadeOutColorAction(Color.black, 1, 120, camera);
//        IActionTimer fadeOutEffect = new PeriodicFiniteActionTimer(fadeOutAction, 1, 120);
//
//        IAction levelTransitionAction = new LevelTransitionAction();
//        IActionTimer levelTransitionEffect = new DelayedActionTimer(levelTransitionAction, 120);
//
//        fadeOutEffect.startTimer();
//        levelTransitionEffect.startTimer();
//    }
//
//
//
//    public abstract void InitLevel();
//
//    public Player getPlayer(){
//        return handler.getPlayer();
//    }
//    public ButtonCollection getButtons(){
//        return buttons;
//    }
//    public Timer getGameTimer(){
//        return gameTimer;
//    }
//    public boolean getLevelRunningState(){
//        return LevelFlagsSystem.isLevelRunning;
//    }
//    public void setLevelRunningState(Boolean state){
//        LevelFlagsSystem.isLevelRunning = state;
//    }
//    public Boolean getOnResetState() {
//        return LevelFlagsSystem.isOnReset;
//    }
//    public void setOnResetState(Boolean state){
//        LevelFlagsSystem.isOnReset = state;
//    }
//    public ControllerBuilder getControllerBuilder(){
//        return myControllerBuilder;
//    }
//    public Objective getGameObjective(){
//        return gameObjective;
//    }
//    public Level getNextLevel(){
//        return nextLevel;
//    }
//    public abstract int getLevelCode();
}
