package timebender.levels;

public class LevelBuilder {

    public static Level CreateLevel(String levelName){
        return switch (levelName) {
            case "SimpleLevel" -> new SimpleLevel();
            case "Level0" -> new Level0();
            default -> null;
        };
    }
}
