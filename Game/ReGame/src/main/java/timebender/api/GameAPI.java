package timebender.api;

import timebender.Game;
import timebender.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class GameAPI {

    /**
     * Server properties.
     */
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isClientConnected = false;

    /**
     * Game properties.
     */
    private Game game;
    private boolean isGameStarted = false;
    private GameThread gameThread;


    /**
     * Communication elements.
     */
    private final ConcurrentHashMap<Integer, LinkedList<String>> communicationQueue = new ConcurrentHashMap<>();
    private volatile boolean commandEnded = false;

    public GameAPI() {
        this.game = null;
        communicationQueue.put(0, new LinkedList<>());
        communicationQueue.put(1, new LinkedList<>());
    }

    public GameAPI(Game game) {
        this.game = game;
        communicationQueue.put(0, new LinkedList<>());
        communicationQueue.put(1, new LinkedList<>());
    }

    private void clientCommunicationHandle() {
        while (isClientConnected) {

            if (!communicationQueue.get(1).isEmpty()) {
                System.out.println("CommunicationQueue content:");
                for(Object s : communicationQueue.get(1).toArray()){
                    if(s == null || Objects.equals(s.toString(), ""))
                        System.out.print("\t" + "null" + "\n");
                    else
                        System.out.print("\t" + s + "\n");
                }

                String message = communicationQueue.get(1).poll();

                if(message == null)
                    isClientConnected = false;

                // Gather end command message
                if ("CommandEnded".equals(message)) {
                    commandEnded = true;
                    Logger.Print("CommandEnded received!");
                }
                else{
//                    System.out.println("To send: " + message);
                    out.println(message);
                }
            }
        }
    }

    public void startAPI(int port) {
        try {
            // Create server
            serverSocket = new ServerSocket(port);

            // Wait for client
            Logger.Print("[SERVER]: Waiting for client ...");
            clientSocket = serverSocket.accept();
            isClientConnected = true;
            Logger.Print("[SERVER]: Client connected!");

            // Get = I/O means
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Start client communication thread
            Thread clientCommunication = new Thread(this::clientCommunicationHandle);
            clientCommunication.start();

            // Receive message loop
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                Logger.Print("[SERVER]: From client: " + inputLine);

                commandEnded = false;

                // Exit condition
                if (".".equals(inputLine)) {
                    out.println("good bye");
                    break;
                }

                commandInterpretor(inputLine);

                // Wait for command to properly end
                while (!commandEnded) {
                    Thread.onSpinWait();
                    // Do nothing
                }
            }

            // Terminate client socket
            in.close();
            out.close();
            isClientConnected = false;
            clientSocket.close();
            Logger.Print("Client disconnected!");

            // After finishing with the client stop server
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void commandInterpretor(String command){

        // Configure a game
        if (command.startsWith("Configure ")) {
            String[] args = command.substring("Configure ".length()).split(" ");
            GameArgumentConfigurer gameArgumentConfigurer = new GameArgumentConfigurer(args);
            game = gameArgumentConfigurer.configureGame();

            // Set that this command ended
            commandEnded = true;
            return;
        }

        // Start game if game not null
        if ("StartGame".equals(command) && !isGameStarted && game != null) {

            // Another thread might be needed
            isGameStarted = true;
            gameThread = new GameThread(game, communicationQueue);
            gameThread.start();

        }

        // Restart level command if game is started
        if ("RestartLevel".equals(command) && isGameStarted) {

            // Send the command to the game through queue
            communicationQueue.get(0).add("RestartLevel");
        }

        // Check if it is a step command
        if ("FrameStep".equals(command) && isGameStarted){

            // Send the command to the game through queue
            communicationQueue.get(0).add("FrameStep");

        }

        // Check if it is a step command
        if ("RequestLevelState".equals(command)){
            // Send the command to the game through queue
            communicationQueue.get(0).add("RequestLevelState");
        }

        // Player command
        if (command.startsWith("Player command: ")) {
            communicationQueue.get(0).add(command);
        }
    }
}
