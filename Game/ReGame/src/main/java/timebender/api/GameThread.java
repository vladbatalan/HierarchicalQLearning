package timebender.api;

import timebender.Game;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameThread extends Thread {
    private Game game;
    private final ConcurrentHashMap<Integer, LinkedList<String>> communicationQueue;
    private boolean loopIsOn = false;

    public GameThread(Game game, ConcurrentHashMap<Integer, LinkedList<String>> concurrentLinkedQueue) {
        this.game = game;
        this.communicationQueue = concurrentLinkedQueue;
    }

    private void receiveMessageLoop() {
        while (loopIsOn) {

            if (!communicationQueue.get(0).isEmpty()) {

                // Peek top value
                String message = communicationQueue.get(0).poll();
                assert message != null;

                // Check message type
                if ("RestartLevel".equals(message)) {
                    game.restartLevel();
                    communicationQueue.get(1).add("Game restarted");
                    continue;
                }

                // Check Move Commands
                if (message.startsWith("Player command: ")) {
                    String command = message.substring("Player command: ".length());
                    communicationQueue.get(1).add("New state of game");
                    // Check command type
                }
            }
        }
    }

    @Override
    public void run() {
        // Initiate receive message communication
        Thread receiveMessage = new Thread(this::receiveMessageLoop);
        loopIsOn = true;
        receiveMessage.start();

        // Start the game
        game.startGame();
    }
}
