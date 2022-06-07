package timebender.api;

import timebender.Game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class GameArgumentConfigurer {

    private final String[] args;

    private final String[] levels = {"Level0", "SimpleLevel"};

    private String pickedLevel = null;
    private Boolean graphicsMode = null;
    private Boolean keyboardInput = true;
    private Boolean manualStep = false;

    public GameArgumentConfigurer(String[] args){
        this.args = args;
    }

    public Game configureGame() {
        if (Arrays.asList(args).contains("-h")) {
            System.out.println("Help.");
            System.out.println("-lvl <level name> \t for level title (default: SimpleLevel)");
            System.out.println("-g <boolean> \t for graphics mode on or off (default: true)");
            System.out.println("-ctrl <keyboard/external> \t for controller type (default: keyboard)");
            System.out.println("-manual-step <false/true> \t for controller type (default: false)");

            return null;
        }

        for (int index = 0; index < args.length; index++) {
            String command = args[index];

            if (command.equals("-lvl") && index + 1 < args.length) {
                pickedLevel = args[index + 1];

                boolean ok = false;
                for (String s : levels) {
                    if (s.equals(pickedLevel)) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    System.out.println("No level with the name " + args[index + 1] + " found!");
                    return null;
                }
                index++;
                continue;
            }

            if (command.equals("-g") && index + 1 < args.length) {
                graphicsMode = null;
                if (args[index + 1].equals("true"))
                    graphicsMode = true;
                if (args[index + 1].equals("false"))
                    graphicsMode = false;

                if (graphicsMode == null) {
                    System.out.println("Invalid value for -g! Expected: true/false.");
                    return null;
                }
                index++;
                continue;
            }

            if (command.equals("-ctrl") && index + 1 < args.length) {
                String keyType = args[index + 1];

                keyboardInput = keyType.equals("keyboard");
                continue;
            }

            if (command.equals("-manual-step") && index + 1 < args.length) {
                 String value = args[index + 1];

                 if("true".equals(value))
                     manualStep = true;
            }

        }

        if (pickedLevel == null)
            pickedLevel = "SimpleLevel";
        if (graphicsMode == null)
            graphicsMode = true;

        Game game = new Game();
        // Set level
        game.setLevelCreateString(pickedLevel);
        // Set graphics mode
        game.setKeyboardInputType(keyboardInput);
        // Set the step mode
        game.setManualStep(manualStep);

        return game;
    }
}
