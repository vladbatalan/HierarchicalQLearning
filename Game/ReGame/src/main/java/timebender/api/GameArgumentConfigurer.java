package timebender.api;

import timebender.Game;
import timebender.levels.LevelBuilder;
import timebender.levels.reward.*;

import java.util.Arrays;

public class GameArgumentConfigurer {

    private final String[] args;

    private String pickedLevel = null;
    private Boolean graphicsMode = null;
    private Boolean keyboardInput = true;
    private Boolean manualStep = false;
    private String rewardType = null;

    public GameArgumentConfigurer(String[] args) {
        this.args = args;
    }

    public Game configureGame() {
        if (Arrays.asList(args).contains("-h")) {
            System.out.println("Help.");
            System.out.println("-lvl <level name> \t for level title (default: SimpleLevel)");
            System.out.println("-g <boolean> \t for graphics mode on or off (default: true)");
            System.out.println("-ctrl <keyboard/external> \t for controller type (default: keyboard)");
            System.out.println("-manual-step <false/true> \t for controller type (default: false)");
            System.out.println("-reward <reward name> \t for controller type (default: OnlyWinLose)");

            return null;
        }

        for (int index = 0; index < args.length; index++) {
            String command = args[index];

            if (command.equals("-lvl") && index + 1 < args.length) {
                pickedLevel = args[index + 1];

                boolean ok = false;
                for (String s : LevelBuilder.LevelNames()) {
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

                if ("true".equals(value))
                    manualStep = true;
                continue;
            }

            if (command.equals("-reward") && index + 1 < args.length) {
                rewardType = args[index + 1];
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
        game.setGraphicsMode(graphicsMode);
        // Set the input
        game.setKeyboardInputType(keyboardInput);
        // Set the step mode
        game.setManualStep(manualStep);
        // Set the reward
        game.setRewardSystem(seekRewardSystemType());

        return game;
    }

    private IRewardSystem seekRewardSystemType() {
        if(rewardType == null)
            return new OnlyWinLoseReward();

        switch (rewardType) {
            case "DistanceToTarget":
                return new DistanceToActiveTargetReward();
            case "PunishIllegalActions":
                return new PunishIllegalActionsReward();
            case "PunishIllegalActionsDistance":
                return new PunishIllegalActionsAndDistanceReward();
            case "PromoteAllStatesActive":
                return new PromoteAllStatesActiveReward();
            case "OptimalReward":
                return new OptimalReward();
            default:
                return new OnlyWinLoseReward();
        }
    }

}
