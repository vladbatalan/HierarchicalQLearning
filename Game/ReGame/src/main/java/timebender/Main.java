package timebender;

public class Main {
    public static void main(String[] args) {

        Game timeBenderGame = new Game();

        // Active logging
        Logger.ChangeIsActive(true);
        Logger.SetGame(timeBenderGame);

        // Start game
        timeBenderGame.startGame();


    }
}
