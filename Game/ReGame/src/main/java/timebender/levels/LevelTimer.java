package timebender.levels;

public class LevelTimer {

    // The time counts down the frames of the game
    private int frameNumber = 0;

    // A timer can be controlled through these flags
    public boolean isTimerOn = false;
    public boolean isTimerVisible = false;

    public LevelTimer(){}

    public void update() {

        // Update frame number only if timer is active
        if(isTimerOn) {
            frameNumber ++;
        }
    }

    public void restartTimer(){
        frameNumber = 0;
        isTimerOn = true;
    }

    public void setTimerOn(boolean isTimerOn){
        this.isTimerOn = isTimerOn;
    }

    public int getFrameNumber() {
        return frameNumber;
    }
}
