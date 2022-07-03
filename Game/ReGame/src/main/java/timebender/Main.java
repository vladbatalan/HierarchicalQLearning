package timebender;

import timebender.api.GameAPI;
import timebender.api.GameArgumentConfigurer;

public class Main {
    public static void main(String[] args) {
        // Active logging
        Logger.SetIsActive(true);

        if(args.length != 0 && "-extern".equals(args[0])){
            int port = 4303;
            if(args.length == 3){
                if("-port".equals(args[1])){
                    port = Integer.parseInt(args[2]);
                }
            }

            // Create GameAPI for configured game if external control option enabled
            GameAPI gameAPI = new GameAPI();
            gameAPI.startAPI(port);
        }
        else{

            GameArgumentConfigurer configurer = new GameArgumentConfigurer(args);
            Game timeBenderGame = configurer.configureGame();
            Logger.SetGame(timeBenderGame);
            timeBenderGame.startGame();
        }
    }
}
