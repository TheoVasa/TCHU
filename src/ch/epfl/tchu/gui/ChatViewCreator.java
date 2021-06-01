package ch.epfl.tchu.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.MapChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChatViewCreator {

    private ChatViewCreator() {
    }

    public static VBox createChatView(ObservableChat chat, ActionHandler.sendChatHandler chatHandler, SimpleBooleanProperty isChatDisplayed) {

        VBox root = new VBox();
        root.getStylesheets().add("chat.css");
        root.setSpacing(5);

        // the chatField with the scroller
        VBox chatField = generateChatField();
        ScrollPane scroller = new ScrollPane();
        scroller.setContent(chatField);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        //the text field and the button
        HBox textField = generateTextFieldAndSendButton(chatHandler);

        //create the show info button
        Button showInfoButton = new Button("afficher les infos");
        showInfoButton.setPrefWidth(220);
        showInfoButton.setOnAction((event) -> isChatDisplayed.set(!isChatDisplayed.getValue()));

        root.getChildren().addAll(scroller, textField, showInfoButton);

        //add the new chat if needed
        chat.allChatsProperty().addListener((MapChangeListener<? super String, ? super Boolean>) change -> {
            //add the new chat to the chatbox
            addCaseOfChat(chatField, change.getKey(), change.getValueAdded());
        });

        return root;
    }
    private static VBox generateChatField(){
        VBox chatField = new VBox();
        chatField.getStyleClass().add("chatField");
        return chatField;
    }

    private static HBox generateTextFieldAndSendButton(ActionHandler.sendChatHandler chatHandler){
        HBox hbox = new HBox();

        //generate the text bar
        TextField textField = new TextField();
        textField.setPromptText("Nouveau chat");

        //the button to send the chat
        Button sendButton = new Button("Envoi");
        sendButton.setOnAction((event -> {
            //we cannot send empty messages
            if(!textField.getText().equals("")) {
                //send the message
                chatHandler.sendChat(textField.getText());
                //clean the textfield
                textField.setText("");
            }
        }));
        hbox.getChildren().addAll(textField, sendButton);

        return hbox;
    }

    private static void addCaseOfChat(VBox chatField, String chat, Boolean isOwnPlayerChat){

        HBox hbox = new HBox();

        Label message = new Label(chat);
        message.setAlignment(Pos.CENTER);
        message.setWrapText(true);

        //set the message at right if we send the chat
        if (isOwnPlayerChat) {
            message.getStyleClass().add("ownChat");
            hbox.setAlignment(Pos.BASELINE_RIGHT);

        } else {
            message.getStyleClass().add("otherChat");
            hbox.setAlignment(Pos.BASELINE_LEFT);

        }
        //add the message to the chatField
        hbox.getChildren().add(message);
        chatField.getChildren().add(hbox);
    }

}
