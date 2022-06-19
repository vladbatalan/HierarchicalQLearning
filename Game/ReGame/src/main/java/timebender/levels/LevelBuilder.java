package timebender.levels;

public class LevelBuilder {

    public static String[] LevelNames(){
        return new String[] {"Level0", "SimpleLevel", "SimpleLevelNoJump"};
    }

    public static Level CreateLevel(String levelName){
        return switch (levelName) {
            case "SimpleLevel" -> new SimpleLevel();
            case "Level0" -> new Level0();
            case "SimpleLevelNoJump" -> new SimpleLevelNoJump();
            default -> null;
        };
    }
}
