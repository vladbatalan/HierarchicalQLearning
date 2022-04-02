package timebender.physics.enums;

public enum Actions {
    MOVE_LEFT(0),
    MOVE_RIGHT(1),
    JUMP(2);

    int value;
    Actions(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
