package timebender.gameobjects.stills;


import timebender.assets.animations.collections.TimeMachineAnimationCollection;
import timebender.gameobjects.ObjectID;
import timebender.map.Map;
import timebender.map.tiles.Tile;
import timebender.physics.Body;
import timebender.physics.utils.PointVector;

import java.awt.*;

import static timebender.gameobjects.mobs.Player.BODY_HEIGHT;
import static timebender.gameobjects.mobs.Player.BODY_WIDTH;

public class TimeMachine extends StillObject {
    public static final int BODY_WIDTH = 50;
    public static final int BODY_HEIGHT = 100;
    public static final float BODY_MASS = 50;

//    private ScreenTag helpOnInteractionTop;
//    private ScreenTag helpOnInteractionBottom;
//
//    private ScreenTag helpOnInteractionShadowTop;
//    private ScreenTag helpOnInteractionShadowBottom;

    public TimeMachine() {
        super(new Body(new PointVector(), BODY_WIDTH, BODY_HEIGHT, BODY_MASS));
        this.id = ObjectID.TimeMachine;
        this.animation = new TimeMachineAnimationCollection();
    }

    public TimeMachine(PointVector position) {
        super(new Body(position, BODY_WIDTH, BODY_HEIGHT, BODY_MASS));
        this.id = ObjectID.TimeMachine;
        this.animation = new TimeMachineAnimationCollection();
        // TODO: Display helper string only if graphics mode is on
        // helpStringDisplayInitiate();
    }

    public TimeMachine positionedInTileCoordinates(int width, int height) {
        PointVector position = new PointVector(width * Tile.TILE_WIDTH, height * Tile.TILE_HEIGHT - BODY_HEIGHT);
        this.body.setPosition(position);
        return this;
    }

//    private void helpStringDisplayInitiate(){
//        helpOnInteractionTop = new ScreenTag("Press \"Space\"", 12);
//        helpOnInteractionBottom = new ScreenTag("to time travel.", 12);
//        helpOnInteractionShadowTop = new ScreenTag("Press \"Space\"", 12);
//        helpOnInteractionShadowBottom = new ScreenTag("to time travel.", 12);
//        helpOnInteractionShadowBottom.setColor(Color.BLACK);
//        helpOnInteractionShadowTop.setColor(Color.BLACK);
//
//        // if player stops interracting, the string stops displaying
//        helpOnInteractionShadowTop.addTimerIntreruptor(new PlayerStopsInteractingWithTimeMachine());
//        helpOnInteractionShadowBottom.addTimerIntreruptor(new PlayerStopsInteractingWithTimeMachine());
//        helpOnInteractionTop.addTimerIntreruptor(new PlayerStopsInteractingWithTimeMachine());
//        helpOnInteractionBottom.addTimerIntreruptor(new PlayerStopsInteractingWithTimeMachine());
//        helpOnInteractionShadowTop.addTimerIntreruptor(new GameStateGameIntrerupter());
//        helpOnInteractionShadowBottom.addTimerIntreruptor(new GameStateGameIntrerupter());
//        helpOnInteractionTop.addTimerIntreruptor(new GameStateGameIntrerupter());
//        helpOnInteractionBottom.addTimerIntreruptor(new GameStateGameIntrerupter());
//    }
//
//    public void displayHelpString() {
//        if (!helpOnInteractionTop.getIsOnShow() && !LevelFlagsSystem.isOnReset) {
//            helpOnInteractionShadowTop.showScreenTag((int) (body.getPosition().getX() + body.getBodyWidth() / 2 + 2), (int) body.getPosition().getY() - 30 + 2);
//            helpOnInteractionTop.showScreenTag((int) (body.getPosition().getX() + body.getBodyWidth() / 2), (int) body.getPosition().getY() - 30);
//
//            helpOnInteractionShadowBottom.showScreenTag((int) (body.getPosition().getX() + body.getBodyWidth() / 2 + 2), (int) body.getPosition().getY() - 10 + 2);
//            helpOnInteractionBottom.showScreenTag((int) (body.getPosition().getX() + body.getBodyWidth() / 2), (int) body.getPosition().getY() - 10);
//        }
//    }

    @Override
    public void Draw(Graphics g) {
        animation.displayAnimation("StandAnimation", body.getHitBox(), g);
    }
}
