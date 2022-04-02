package timebender.physics.enums;

public enum CollisionTypes {
    TOP(0),
    RIGHT(1),
    BOTTOM(2),
    LEFT(3),
    DEADLY(4);

    int value;

    CollisionTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
