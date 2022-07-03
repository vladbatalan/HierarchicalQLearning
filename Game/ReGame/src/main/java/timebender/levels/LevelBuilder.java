package timebender.levels;

public class LevelBuilder {

    public static String[] LevelNames() {
        return new String[]{"Level0", "SimpleLevel", "SimpleLevelNoJump", "OnlyObjective", "LeverWithObjective", "Level0Simple"};
    }

    public static Level CreateLevel(String levelName) {
        return switch (levelName) {
            case "SimpleLevel" -> new SimpleLevel();
            case "Level0" -> new Level0();
            case "SimpleLevelNoJump" -> new SimpleLevelNoJump();
            case "OnlyObjective" -> new OnlyObjectiveLevel();
            case "LeverWithObjective" -> new LeverObjectiveLevel();
            case "Level0Simple" -> new Level0Easier();
            default -> null;
        };
    }
}
