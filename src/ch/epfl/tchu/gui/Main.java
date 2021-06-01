package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main extends Application {

    //Useful to enable or disable the button for the server/client menu
    private SimpleBooleanProperty isServerMenu = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isConnectable = new SimpleBooleanProperty(false);

    //Effectively final
    private VBox center = new VBox();
    private BorderPane borderPane = new BorderPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage stage = new Stage();

        //Center of the borderPane
        center = centerClientMenu();
        center.getStyleClass().add("center");

        //Top of the borderPane
        HBox top = new HBox();
        Text title = new Text("TCHU - Menu de connection");
        title.setId("title");
        top.getChildren().add(title);
        top.getStyleClass().add("top");

        //Bottom of the borderPane
        HBox bottom = new HBox();
        Button connectBtn = new Button("Connect");
        connectBtn.getStyleClass().add("buttonConnection");
        Text connectionState = new Text();
        bottom.getChildren().addAll(connectBtn, connectionState);
        bottom.getStyleClass().add("bottom");


        //Left of the borderPane
        VBox right = new VBox();
        Button serverMenuBtn = new Button("Server");
        serverMenuBtn.setOnAction((event -> {
            isServerMenu.setValue(true);
            borderPane.setCenter(centerServerMenu());
            connectBtn.setText("Host");
        }));
        serverMenuBtn.disableProperty().bind(isServerMenu);
        serverMenuBtn.getStyleClass().add("button");
        Button clientMenuButton = new Button("Client");
        clientMenuButton.setOnAction((event -> {
            isServerMenu.setValue(false);
            borderPane.setCenter(centerClientMenu());
            connectBtn.setText("Connect");
        }));
        clientMenuButton.disableProperty().bind(isServerMenu.not());
        clientMenuButton.getStyleClass().add("button");
        right.getChildren().addAll(serverMenuBtn, clientMenuButton);
        right.getStyleClass().add("right");

        //Right of the borderPane
        borderPane = new BorderPane(center, top, right, bottom, null);
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("menu.css");
        stage.setScene(scene);
        stage.show();
    }


    private VBox centerServerMenu(){
        String ipAddress = new String();
        try{
            InetAddress inetadr = InetAddress.getLocalHost();
            //adresse ip sur le réseau
            ipAddress = (String) inetadr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        VBox vBox = new VBox();
        HBox hBoxIp = new HBox();
        Label ipTextLabel = new Label("L'addresse ip du server :");
        ipTextLabel.getStyleClass().add("label");
        Label ipLabel = new Label(ipAddress);
        ipLabel.getStyleClass().add("label");
        hBoxIp.getChildren().addAll(ipTextLabel, ipLabel);
        HBox hBoxName = textAreaCreator("Choisissez votre nom :    ");

        vBox.getChildren().addAll(hBoxIp, hBoxName);
        return vBox;
    }

    private VBox centerClientMenu(){
        VBox vBox = new VBox();
        HBox hBoxIP = textAreaCreator("Sesissez l'adresse ip du server    ");
        HBox hBoxName = textAreaCreator("Choisissez votre nom :             ");
        vBox.getChildren().addAll(hBoxIP, hBoxName);
        return vBox;
    }

    private HBox textAreaCreator(String strLabel){
        HBox hBox = new HBox();
        hBox.setFillHeight(false);
        Label nameLabel = new Label(strLabel);
        nameLabel.getStyleClass().add("label");
        TextArea nameArea = new TextArea();
        nameArea.getStyleClass().add("textArea");
        hBox.getChildren().addAll(nameLabel, nameArea);
        return hBox;
    }
}
