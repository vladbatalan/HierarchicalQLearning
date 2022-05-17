package timebender.api;

import timebender.Game;
import timebender.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameAPI{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Game game;
    private boolean isGameStarted = false;
    private boolean isClientConnected = false;
    private GameThread gameThread;
    private final ConcurrentHashMap<Integer, LinkedList<String>> communicationQueue = new ConcurrentHashMap<>();

    public GameAPI(){
        this.game = null;
        communicationQueue.put(0, new LinkedList<>());
        communicationQueue.put(1, new LinkedList<>());
    }
    public GameAPI(Game game) {
        this.game = game;
        communicationQueue.put(0, new LinkedList<>());
        communicationQueue.put(1, new LinkedList<>());
    }

    private void clientCommunicationHandle(){
        while(isClientConnected){
            if(!communicationQueue.get(1).isEmpty())
            {
                // Peek the top element
                String message = communicationQueue.get(1).poll();

                // Send the message to the app
                out.println(message);
            }
        }
    }

    public void startAPI(int port){
        try {
            // Create server
            serverSocket = new ServerSocket(port);

            // Wait for client
            Logger.Print("[SERVER]: Waiting for client ...");
            clientSocket = serverSocket.accept();
            isClientConnected = true;
            Logger.Print("[SERVER]: Client connected!");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Start client communication thread
            Thread clientCommunication = new Thread(this::clientCommunicationHandle);
            clientCommunication.start();

            // Receive message loop
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                Logger.Print("[SERVER]: From client: " + inputLine);

                // Exit condition
                if (".".equals(inputLine)) {
                    out.println("good bye");
                    break;
                }

                // Configure a game
                if(inputLine.startsWith("Configure ")){
                    String[] args = inputLine.substring("Configure ".length()).split(" ");
                    GameArgumentConfigurer gameArgumentConfigurer = new GameArgumentConfigurer(args);
                    game = gameArgumentConfigurer.configureGame();

                }

                // Start game if game not null
                if("StartGame".equals(inputLine) && !isGameStarted && game != null){

                    // Another thread might be needed
                    // out.println("Started the game");
                    isGameStarted = true;

                    gameThread = new GameThread(game, communicationQueue);
                    gameThread.start();
                }

                // Restart level command if game is started
                if("RestartLevel".equals(inputLine) && isGameStarted){

                    // Another thread might be needed
                    //out.println("Complete restarting the level");

                    // Send the command to the game through queue
                    communicationQueue.get(0).add("RestartLevel");
                }
                //out.println(inputLine);
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
}
