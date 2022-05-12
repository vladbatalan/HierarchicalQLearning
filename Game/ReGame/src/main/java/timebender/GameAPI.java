package timebender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class GameAPI extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Game game;
    private boolean isGameStarted = false;

    public GameAPI(Game game) {
        this.game = game;
    }

    private static class GameThread extends Thread{
        private final Game game;
        public GameThread(Game game){
            this.game = game;
        }

        @Override
        public void run(){
            game.startGame();
        }
    }

    public void startAPI(int port){
        try {
            // Create server
            serverSocket = new ServerSocket(port);

            // Wait for client
            Logger.Print("Waiting for client ...");
            clientSocket = serverSocket.accept();
            Logger.Print("Client connected!");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Receive message loop
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                Logger.Print(inputLine);
                if (".".equals(inputLine)) {
                    out.println("good bye");
                    break;
                }
                if("StartGame".equals(inputLine) && !isGameStarted){
                    // Another thread might be needed
                    Logger.Print("Client started the game!");
                    out.println("Started the game");
                    isGameStarted = true;

                    GameThread gameThread = new GameThread(game);
                    gameThread.start();
                }
                out.println(inputLine);
            }

            // Terminate client socket
            in.close();
            out.close();
            clientSocket.close();
            Logger.Print("Client disconnected!");

            // After finishing with the client stop server
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
