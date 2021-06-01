package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class represent the client, used to connect and get the information from the server, extends from a javaFX application.
 *
 * @author Théo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public class ClientMain extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //get the arguments of the program
        String hostName = this.getParameters().getRaw().get(0);
        int serverGamePort = Integer.parseInt(this.getParameters().getRaw().get(1));
        int serverChatPort = Integer.parseInt(this.getParameters().getRaw().get(2));
        String playerName = this.getParameters().getRaw().get(3);


        //the distant client
        RemotePlayerClient client = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostName, serverGamePort, serverChatPort, playerName);

        //start the thread
        new Thread(client::runGame).start();
        new Thread(client::runChat).start();
    }
}
