package timebender;

import timebender.map.Map;

import static timebender.map.MapBuilder.BuildFromXmlFile;

public class Main
{
    public static void main(String[] args)
    {

        Game timeBenderGame = new Game();

        // Active logging
        Logger.ChangeIsActive(true);
        Logger.SetGame(timeBenderGame);

        // Start game
        timeBenderGame.startGame();


    }
}
