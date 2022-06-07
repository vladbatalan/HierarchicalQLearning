package timebender.api;

import timebender.Game;
import timebender.Logger;
import timebender.input.ExternalInput;
import timebender.physics.states.movecommands.MoveCommand;
import timebender.physics.states.movecommands.MoveCommandType;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class GameThread extends Thread {
    private final Game game;
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

                // Check if it is a step command
                if ("FrameStep".equals(message))  {
                    Logger.Print("Before triggering Step Frame ... ");
                    game.triggerFrameStep();
                    Logger.Print("After triggering Step Frame.");


                    // TODO: communicationQueue should send back the state of the level
                    continue;
                }

                // Check Move Commands
                if (message.startsWith("Player command: ")) {
                    String command = message.substring("Player command: ".length());
//                    System.out.println("Received command: " + command);

                    try {
                        // Get the command
                        MoveCommandType moveCommandType = MoveCommandType.valueOf(command);
                        communicationQueue.get(1).add(command + "(" + game.getCurrentFrame() + ")");

                        // Create command
                        MoveCommand moveCommand = new MoveCommand(moveCommandType);

                        // Send to External Input
                        ExternalInput externalInput = game.getExternalInput();
                        externalInput.receiveCommand(moveCommand);

                    }
                    catch (IllegalArgumentException e){
                        e.printStackTrace();
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
    }
}
