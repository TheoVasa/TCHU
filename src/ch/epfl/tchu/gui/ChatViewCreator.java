package ch.epfl.tchu.gui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;


public class ChatViewCreator {

    private ChatViewCreator() {
    }

    public static Pane createChatView(ObservableChat chat, ActionHandler.sendChatHandler chatHandler) {
        //TODO afficher le chat, les boutons etc...
        //idée faire comme watsapp, afficher d'une couleur si c'est nous qui l'avons envoyé, d'une autre si non
        //et de deux cotés différents aussi

        String lastChat = "";
        Pane chatPane = new Pane();


        Button sendButton = createStyleButton();
        sendButton.setOnAction((event -> chatHandler.sendChat(lastChat)));
        return null;
    }

    private static Node generateCaseOfChat(String chat){
        //.....
        return null;
    }

    private static Node generateTextBar(){
        //.....
        return null;
    }

    private static Button createStyleButton(){
        //....
        return null;

    }
}
