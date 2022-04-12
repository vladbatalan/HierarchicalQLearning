package timebender.gameobjects.stills;

/**
 * A class that denotes a command for a Switchable object.
 * <p>
 * It contains the object that is controlled, a string which is the command for that object and
 * the front of change.
 * <p>
 * <p>
 * frontOfChange designs how an ISwitchable should change based on the change state
 * frontOfChange = true => it will be turned on when the switch is turned on
 * frontOfChange = false => it will be turned off when the switch is turned on
 */
public class SwitchCommand {


    public ISwitchable switchable;
    public String command;
    public boolean frontOfChange;

    public SwitchCommand(ISwitchable switchable, String command, boolean frontOfChange) {
        this.command = command;
        this.frontOfChange = frontOfChange;
        this.switchable = switchable;
    }
}