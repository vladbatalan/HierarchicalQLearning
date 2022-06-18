package timebender.api;

import timebender.Game;
import timebender.input.ExternalInput;
import timebender.physics.states.movecommands.MoveCommand;
import timebender.physics.states.movecommands.MoveCommandType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameThread extends Thread {
    private final Game game;
    private final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> communicationQueue;
    private boolean loopIsOn = false;

    public GameThread(Game game, ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> concurrentLinkedQueue) {
        this.game = game;
        this.communicationQueue = concurrentLinkedQueue;
    }

    private void receiveMessageLoop() {
        String message;
        while (loopIsOn) {

            if (!communicationQueue.get(0).isEmpty() && (message = communicationQueue.get(0).poll()) != null) {

                // Check message type
                if ("RestartLevel".equals(message)) {
                    game.restartLevel();
                    communicationQueue.get(1).add("CommandEnded");
                    continue;
                }

                // Check message type
                if ("SubRestartLevel".equals(message)) {
                    game.subRestartLevel();
                    communicationQueue.get(1).add("CommandEnded");
                    continue;
                }

                // Check if it is a step command
                if ("FrameStep".equals(message))  {
                    game.triggerFrameStep();
                    communicationQueue.get(1).add("CommandEnded");
                    continue;
                }

                if ("RequestLevelState".equals(message))  {
                    String status = game.collectLevelDynamicStatus();
                    communicationQueue.get(1).add(status);
                    communicationQueue.get(1).add("CommandEnded");
                    continue;
                }

                if ("RequestInitialState".equals(message))  {
                    String status = game.collectLevelStaticStatus();
                    communicationQueue.get(1).add(status);
                    communicationQueue.get(1).add("CommandEnded");
                    continue;
                }

                // Check Move Commands
                if (message.startsWith("Player command: ")) {
                    String command = message.substring("Player command: ".length());
                    try {
                        // Get the command
                        MoveCommandType moveCommandType = MoveCommandType.valueOf(command);

                        // Create command
                        MoveCommand moveCommand = new MoveCommand(moveCommandType);

                        // Send to External Input
                        ExternalInput externalInput = game.getExternalInput();
                        externalInput.receiveCommand(moveCommand);

                        communicationQueue.get(1).add("CommandEnded");
                    }
                    catch (IllegalArgumentException e){
                        e.printStackTrace();
                        communicationQueue.get(1).add("CommandEnded");
                    }
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
        communicationQueue.get(1).add("CommandEnded");
    }
}
