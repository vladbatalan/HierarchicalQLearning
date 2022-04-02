package timebender;

import java.time.LocalTime;

public class Logger {
    private static boolean isActive = true;
    private static Game game;

    public static void Print(String message){
        if(isActive) {
            LocalTime currentTime = LocalTime.now();
            String toDisplay = ("[%s]: " + message).formatted(currentTime);
            System.out.println(toDisplay);
        }
    }

    public static void PrintEndline() {
        if(isActive) {
            System.out.println();
        }
    }

    public static void ChangeIsActive(boolean isActive){
        Logger.isActive = isActive;
    }

    public static void SetGame(Game game){
        Logger.game = game;
    }
}
