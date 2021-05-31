package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.Stack;

public class ObservableChat {

    //all the chat send by the two players, the map  associate each chat, at if the own player send it.
    private final ObservableMap<String, Boolean> allChats;

    /**
     * initialize a new observableChat.
     */
    public ObservableChat() {
        allChats = FXCollections.observableHashMap();
    }

    /**
     * add a new chat to the list of all chats send, knowing if the current player is the sender.
     *
     * @param currentPlayerIsTheSender is true if the current player is the sender.
     * @param chat in question.
     */
    public void addNewChat(Boolean currentPlayerIsTheSender , String chat){
        allChats.put(chat, currentPlayerIsTheSender);
    }

    /**
     * get the allChats
     *
     * @return the all chats property. (ObservableMap)
     */
    public ObservableMap<String, Boolean> allChatsProperty(){
        return allChats;
    }
}
