package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class Main extends Application {

    // Start the menu on the server
    private static final boolean START_ON_SERVER_MENU = true;
    private static final String GAME_PORT = "5108";
    private static final String CHAT_PORT = "5109";

    // To call only once the main because sometimes the .setOnAction() of a button is
    // called multiple times even if the button was pressed only once...
    private boolean launched = false;

    // Useful to enable or disable the button for the server/client menu
    private final SimpleBooleanProperty isOnServerMenuProperty = new SimpleBooleanProperty();
    private final SimpleBooleanProperty isConnectedProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty isTryingToConnect = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty isTryingToHost = new SimpleBooleanProperty(false);

    /**
     * Main function of the game. It will launch a JavaFx window with a menu.
     *
     * @param args the arguments of the game
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Create Stage BorderPane and Scene
        HBox top = new HBox();
        VBox center = new VBox();
        HBox bottom = new HBox();
        VBox right = new VBox();
        BorderPane borderPane = new BorderPane(center, top, right, bottom, null);
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("menu.css");
        Stage menu = new Stage();
        menu.setScene(scene);

        // Construct the required node for the top of the borderPane
        top.getStyleClass().add("top");
        Text title = new Text("TCHU - Menu de connection");
        title.setId("title");
        top.getChildren().add(title);

        // Construct the required node for the bottom of the BorderPane
        Label connectionState = new Label();
        Label hostState = new Label();
        Button connectionBtn = createButton("Connection", "Connection");
        Button hostBtn = createButton("Host", "Connection");
        HBox connectionBox = new HBox(connectionBtn, connectionState);
        HBox hostBox = new HBox(hostBtn, hostState);
        bottom.getStyleClass().add("bottom");

        // Construct the required node for the center of the border pane
        Label nameLabel = new Label("Sesissez votre nom : ");
        nameLabel.getStyleClass().add("label");
        TextField nameField = new TextField();
        nameField.getStyleClass().add("textArea");
        HBox nameBox = new HBox(nameLabel, nameField);
        Label serverTextLabel = new Label("L'adresse IP du serveur : ");
        serverTextLabel.getStyleClass().add("label");
        TextField serverIpField = new TextField( (!generateIP().isEmpty()) // TextFiel so that we can copy its content !
                                         ? generateIP()
                                         : "IP INACCESSIBLE !");
        serverIpField.getStyleClass().add("textArea");
        serverIpField.setEditable(false);
        //serverIpLabel.getStyleClass().add("label");
        HBox serverBox = new HBox(serverTextLabel, serverIpField);
        Label ipLabel = new Label("Sesissez l'adresse IP du serveur : ");
        ipLabel.getStyleClass().add("label");
        TextField ipField = new TextField();
        ipField.getStyleClass().add("textArea");
        HBox ipBox = new HBox(ipLabel, ipField);
        center.getChildren().add(nameBox);
        center.getStyleClass().add("center");

        // Construct the required node for the right of the BorderPane
        Button serverBtn = createButton("Server", "button");
        serverBtn.disableProperty().bind(isOnServerMenuProperty);
        serverBtn.getStyleClass().add("button");
        Button clientBtn = createButton("Client", "button");
        clientBtn.disableProperty().bind(isOnServerMenuProperty.not());
        clientBtn.getStyleClass().add("button");
        right.getChildren().addAll(serverBtn, clientBtn);
        right.getStyleClass().add("right");

        // Events of the buttons
        EventHandler<ActionEvent> hostBtnEventHandler = (event) -> {
            if (!launched) {
                hostState.setText("En attente du client");
                Platform.runLater(() -> ServerMain.run(new String[]{nameField.getText(), GAME_PORT, CHAT_PORT},
                        isConnectedProperty,
                        isTryingToHost));
                launched = true;
            }
        };
        EventHandler<ActionEvent> connectionBtnEventHandler = (event) -> {
            if (!launched) {
                Platform.runLater(() -> {
                    connectionState.setText("En attente de connection...");
                    ClientMain.run(new String[]{ipField.getText(), GAME_PORT, CHAT_PORT, nameField.getText()},
                                    isConnectedProperty,
                                    isTryingToConnect);
                    launched = true;
                });
            }
        };
        EventHandler<ActionEvent> serverBtnEventHandler = (event) -> isOnServerMenuProperty.setValue(true);
        EventHandler<ActionEvent> clientBtnEventHandler = (event) -> isOnServerMenuProperty.setValue(false);

        //Add the action handler to the respective buttons
        connectionBtn.setOnAction(connectionBtnEventHandler);
        hostBtn.setOnAction(hostBtnEventHandler);
        serverBtn.setOnAction(serverBtnEventHandler);
        clientBtn.setOnAction(clientBtnEventHandler);

        // Start the menu on the server menu and change the center & bottom
        // when there is a switch of menu type (client or server)
        isOnServerMenuProperty.addListener((p, o, n) -> {
                if (center.getChildren().size() > 1)
                    center.getChildren().remove((o) ? serverBox : ipBox);
                center.getChildren().add(0, (n) ? serverBox : ipBox);
                if (bottom.getChildren().size() > 0)
                    bottom.getChildren().removeAll((o) ? hostBox : connectionBox);
                bottom.getChildren().add((n) ? hostBox : connectionBox);
        });
        isTryingToConnect.addListener((p, o, n) -> {
            if (n)
                connectionState.setText("Connection au serveur...");
            else
                connectionState.setText(new StringBuilder()
                        .append("Echec à la connection au serveur ! \n ")
                        .append("Êtes-vous bien connecté au bon réseau local ?")
                        .toString());
        });
        isTryingToHost.addListener((p, o, n) -> {
            if (n)
                hostState.setText("En attente du client");
            else
                hostState.setText("Le serveur a été intéremnpu !");
        });

        //Start on the server menu
        isOnServerMenuProperty.setValue(START_ON_SERVER_MENU);

        // Show the menu window
        menu.show();
    }

    // Generate th IP address for the server
    private String generateIP() throws IOException{
        StringBuilder ip = new StringBuilder();
        NetworkInterface.networkInterfaces()
                .filter(i -> {
                    try { return i.isUp() && !i.isLoopback(); }
                    catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .flatMap(NetworkInterface::inetAddresses)
                .filter(a -> a instanceof Inet4Address)
                .map(InetAddress::getCanonicalHostName)
                .forEachOrdered(ip::append);
        return ip.toString();
    }

    // Create a button with the given text, style and event
    private Button createButton(String buttonText, String styleClass){
        Button button = new Button();
        button.setText(buttonText);
        button.getStyleClass().add(styleClass);
        return button;
    }

}


