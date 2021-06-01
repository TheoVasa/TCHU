package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.Chat;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * This class represent the server used to run the game and to communicate with the different clients, extends from a javaFX application.
 */
public class ServerMain extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws  IOException {
        //get the arguments of the program
        String player1Name = this.getParameters().getRaw().get(0);

        //wait the connection and initialize the game if it's the case
        try (ServerSocket gameServerSocket = new ServerSocket(5108);
             ServerSocket chatServerSocket = new ServerSocket(5109)) {

            Socket gameSocket = gameServerSocket.accept();
            Socket chatSocket = chatServerSocket.accept();

            //the players
            Player localPlayer = new GraphicalPlayerAdapter();
            Player distantPlayer = new RemotePlayerProxy(gameSocket, chatSocket);

            String distantPlayerName = distantPlayer.receivePlayerName();
            System.out.println(distantPlayerName);

            //playerNames
            Map<PlayerId, String> playerNames = new EnumMap<PlayerId, String>(
                    PlayerId.class);
            playerNames.put(PLAYER_1, player1Name);
            playerNames.put(PLAYER_2, distantPlayerName);

            Map<PlayerId, Player> player = new EnumMap<PlayerId, Player>(
                    PlayerId.class);
            player.put(PLAYER_1, localPlayer);
            player.put(PLAYER_2, distantPlayer);

            //launch the game
            new Thread(() -> Game
                    .play(player, playerNames, SortedBag.of(ChMap.tickets()), new Random()))
                    .start();
        }
    }
}
