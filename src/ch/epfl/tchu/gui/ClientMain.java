package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represent the client, used to connect and get the information from the server, extends from a javaFX application.
 *
 * @author Théo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public class ClientMain{
    // public static void main(String[] args) throws IOException { run(args); }

    public static void run(String[] parameters, SimpleBooleanProperty isConnectedProperty, SimpleBooleanProperty isTryingToConnect) {

        // Check correctness of the argument
        Preconditions.checkArgument(parameters.length == 4);

        // Try to connect
        isTryingToConnect.setValue(true);

        // get the arguments of the program
        String hostName = parameters[0];
        int serverGamePort = Integer.parseInt(parameters[1]);
        int serverChatPort = Integer.parseInt(parameters[2]);
        String playerName = parameters[3];

        try {
            RemotePlayerClient client = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                                                                hostName,
                                                                serverGamePort,
                                                                serverChatPort,
                                                                playerName);
            // start the thread
            new Thread(client::runGame).start();
            new Thread(client::runChat).start();
            isConnectedProperty.setValue(true);

        } catch (IOException e) {
            isTryingToConnect.setValue(false);
        }
    }
}
