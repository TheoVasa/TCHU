package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.Chat;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
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
public class ServerMain{

    // public static void main(String[] args) throws IOException { run(args); }

    public static void run(String[] parameters, SimpleBooleanProperty isConnected, SimpleBooleanProperty isTryingToHost){

        // Check correctness of the argument
        Preconditions.checkArgument(parameters.length == 3);

        // Is trying to host
        isTryingToHost.setValue(true);

        // get the arguments of the program
        String player1Name = parameters[0];
        int serverGamePort = Integer.parseInt(parameters[1]);
        int serverChatPort = Integer.parseInt(parameters[2]);

        // wait the connection and initialize the game if it's the case
        try (ServerSocket gameServerSocket = new ServerSocket(serverGamePort);
             ServerSocket chatServerSocket = new ServerSocket(serverChatPort)) {

            Socket gameSocket = gameServerSocket.accept();
            Socket chatSocket = chatServerSocket.accept();

            // the players
            Player localPlayer = new GraphicalPlayerAdapter();
            Player distantPlayer = new RemotePlayerProxy(gameSocket, chatSocket);
            String distantPlayerName = distantPlayer.receivePlayerName();

            // playerNames
            Map<PlayerId, String> playerNames = new EnumMap<PlayerId, String>(
                    PlayerId.class);
            playerNames.put(PLAYER_1, player1Name);
            playerNames.put(PLAYER_2, distantPlayerName);

            Map<PlayerId, Player> player = new EnumMap<PlayerId, Player>(
                    PlayerId.class);
            player.put(PLAYER_1, localPlayer);
            player.put(PLAYER_2, distantPlayer);

            // launch the game
            new Thread(() -> Game
                    .play(player, playerNames, SortedBag.of(ChMap.tickets()), new Random()))
                    .start();
            isConnected.setValue(true);
        } catch (IOException e) {
            isTryingToHost.setValue(false);
        }
    }
}
