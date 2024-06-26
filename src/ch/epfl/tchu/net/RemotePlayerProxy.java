package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 *This class represent the player in the eye of the server, is used to communicate with the distant player.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class RemotePlayerProxy implements Player {
    //the socket of the proxy
    private final Socket gameSocket;
    private final Socket chatSocket;

    /**
     * Create a RemotePlayerProxy with connected to a given Socket.
     *
     * @param gameSocket the socket for the connection with the client.
     * @param chatSocket the socket for the connection with the client.
     *
     */
    public RemotePlayerProxy(Socket gameSocket, Socket chatSocket){
        this.gameSocket = gameSocket;
        this.chatSocket = chatSocket;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        //Send message
        String msgIdString = MessageId.INIT_PLAYERS.name();
        String ownIdSerialized = Serdes.PLAYER_ID_SERDE.serialize(ownId);
        List<String> players = List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2));
        String dataPlayers = Serdes.LIST_STRING_SERDE.serialize(players);
        List<String> dataList = List.of(msgIdString, ownIdSerialized, dataPlayers, "\n");

        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage, gameSocket);
    }

    /**
     * get the lastChat that the player send.
     *
     * @return the chat. (String)
     */
    @Override
    public String lastChat() {
        //ask the client the new chat
        List<String> dataList = List.of(MessageId.LAST_CHAT.name(), "\n");
        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage, chatSocket);

        //get the answer
        String data = receiveMessage(chatSocket);
        return Serdes.STRING_SERDE.deserialize(data);
    }

    /**
     * used to inform the player he receive a new chat.
     *
     * @param chat the player need the receive.
     */
    @Override
    public void receiveChat(String chat) {
        //send the chat to the client
        List<String> dataList = List.of(MessageId.RECEIVE_CHAT.name(), Serdes.STRING_SERDE.serialize(chat), "\n");
        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage, chatSocket);
    }

    @Override
    public void receiveInfo(String info) {
        //Send message
        String msgIdString = MessageId.RECEIVE_INFO.name();
        String infoSerialized = Serdes.STRING_SERDE.serialize(info);
        List<String> dataList = List.of(msgIdString, infoSerialized, "\n");

        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage, gameSocket);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        //Send message
        String msgIdString = MessageId.UPDATE_STATE.name();
        String newStateSerialized = Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState);
        String ownStateSerialized = Serdes.PLAYER_STATE_SERDE.serialize(ownState);
        List<String> dataList = List.of(msgIdString, newStateSerialized, ownStateSerialized, "\n");

        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage, gameSocket);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        //Send message
        String msgIdString = MessageId.SET_INITIAL_TICKETS.name();
        String ticketsSerialized = Serdes.SORTED_BAG_TICKETS_SERDE.serialize(tickets);
        List<String> dataList = List.of(msgIdString, ticketsSerialized, "\n");

        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage, gameSocket);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        //Send message
        String sendMessage = String.join(" ", List.of(MessageId.CHOOSE_INITIAL_TICKETS.name(), "\n"));
        sendMessage(sendMessage, gameSocket);

        //Receive message
        String receivedMessage = receiveMessage(gameSocket);

        return Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(receivedMessage);
    }

    @Override
    public TurnKind nextTurn() {
        //Send message
        String sendMessage = String.join(" ", List.of(MessageId.NEXT_TURN.name(), "\n"));
        sendMessage(sendMessage, gameSocket);

        //Receive message
        String receiveMessage = receiveMessage(gameSocket);

        return Serdes.TURN_KIND_SERDE.deserialize(receiveMessage);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        //Send message
        String msgIdString = MessageId.CHOOSE_TICKETS.name();
        String optionsSerialized = Serdes.SORTED_BAG_TICKETS_SERDE.serialize(options);
        String sendMessage = String.join(" ", List.of(msgIdString, optionsSerialized, "\n"));
        sendMessage(sendMessage, gameSocket);

        //Receive message
        String receivedMessage = receiveMessage(gameSocket);

        return Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(receivedMessage);
    }

    @Override
    public int drawSlot() {
        //Send message
        String sendMessage = String.join(" ", List.of(MessageId.DRAW_SLOT.name(), "\n"));
        sendMessage(sendMessage, gameSocket);

        //Receive message
        String receivedMessage = receiveMessage(gameSocket);

        return Serdes.INTEGER_SERDE.deserialize(receivedMessage);
    }

    @Override
    public Route claimedRoute() {
        //Send message
        String sendMessage = String.join(" ", MessageId.ROUTE.name(), "\n");
        sendMessage(sendMessage, gameSocket);

        //Receive message
        String receivedMessage = receiveMessage(gameSocket);

        return Serdes.ROUTE_SERDE.deserialize(receivedMessage);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        //Send message
        String sendMessage = String.join(" ", MessageId.CARDS.name(), "\n");
        sendMessage(sendMessage, gameSocket);

        //Receive message
        String receivedMessage = receiveMessage(gameSocket);
        return Serdes.SORTED_BAG_CARD_SERDE.deserialize(receivedMessage);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        //send message
        String msgIdString = MessageId.CHOOSE_ADDITIONAL_CARDS.name();
        String optionsSerialized = Serdes.LIST_SORTED_BAG_CARD_SERDE.serialize(options);
        String sendMessage = String.join(" ", msgIdString, optionsSerialized, "\n");
        sendMessage(sendMessage, gameSocket);

        //Receive message
        String receiveMessage = receiveMessage(gameSocket);

        return Serdes.SORTED_BAG_CARD_SERDE.deserialize(receiveMessage);
    }

    @Override
    public String receivePlayerName(){
        return Serdes.STRING_SERDE.deserialize(receiveMessage(gameSocket));
    }

    //Send a message to the player that isn't on the same machine as the server
    //parameter is the serialized message (instruction) to send
    private void sendMessage(String msg, Socket socket) {
        try {
            BufferedWriter sender = new BufferedWriter(
                                        new OutputStreamWriter(socket.getOutputStream(),
                                                                StandardCharsets.US_ASCII));
            sender.write(msg);
            sender.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    //Receive a message from the player
    //returns the serialization of the message (instruction) received
    private String receiveMessage(Socket socket) {
        try {
            BufferedReader receiver = new BufferedReader(
                                          new InputStreamReader(socket.getInputStream(),
                                                                StandardCharsets.US_ASCII));
            return receiver.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
