package timebender;

public class Main {
    public static void main(String[] args) {

        GameArgumentConfigurer configurer = new GameArgumentConfigurer(args);
        Game timeBenderGame = configurer.configureGame();
        if(timeBenderGame == null)
            return;

        // Active logging
        Logger.SetIsActive(true);
        Logger.SetGame(timeBenderGame);

        // Create GameAPI for configured game if external control option enabled
        GameAPI gameAPI = new GameAPI(timeBenderGame);
        gameAPI.startAPI(4303);

    }
}
