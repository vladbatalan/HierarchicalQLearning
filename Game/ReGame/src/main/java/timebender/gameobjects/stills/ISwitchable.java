package timebender.gameobjects.stills;

import timebender.physics.utils.PointVector;

public interface ISwitchable {
    void turnOn(String command);
    void turnOff(String command);
    PointVector getSwitchablePosition();
}
