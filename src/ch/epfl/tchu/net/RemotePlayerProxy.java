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
    private final Socket socket;

    /**
     * Create a RemotePlayerProxy with connected to a given Socket.
     *
     * @param socket the socket to handle the connection on internet.
     */
    RemotePlayerProxy(Socket socket){
        this.socket = socket;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        //Send message
        String msgIdString = MessageId.INIT_PLAYERS.name();
        String ownIdSerialized = Serdes.PLAYER_ID_SERDE.serialize(ownId);
        String player1Name = Serdes.STRING_SERDE.serialize(playerNames.get(PlayerId.PLAYER_1));
        String player2Name = Serdes.STRING_SERDE.serialize(playerNames.get(PlayerId.PLAYER_2));
        List<String> dataList = List.of(msgIdString, ownIdSerialized, player1Name, player2Name, "\n");

        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage);
    }

    @Override
    public void receiveInfo(String info) {
        //Send message
        String msgIdString = MessageId.RECEIVE_INFO.name();
        String infoSerialized = Serdes.STRING_SERDE.serialize(info);
        List<String> dataList = List.of(msgIdString, infoSerialized, "\n");

        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        //Send message
        String msgIdString = MessageId.UPDATE_STATE.name();
        String newStateSerialized = Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState);
        String ownStateSerialized = Serdes.PLAYER_STATE_SERDE.serialize(ownState);
        List<String> dataList = List.of(msgIdString, newStateSerialized, ownStateSerialized, "\n");

        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        //Send message
        String msgIdString = MessageId.SET_INITIAL_TICKETS.name();
        String ticketsSerialized = Serdes.SORTED_BAG_TICKETS_SERDE.serialize(tickets);
        List<String> dataList = List.of(msgIdString, ticketsSerialized, "\n");

        String sendMessage = String.join(" ", dataList);
        sendMessage(sendMessage);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        //Send message
        String sendMessage = String.join(" ", List.of(MessageId.CHOOSE_INITIAL_TICKETS.name(), "\n"));
        sendMessage(sendMessage);

        //Receive message
        String receivedMessage = receiveMessage();

        return Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(receivedMessage);
    }

    @Override
    public TurnKind nextTurn() {
        //Send message
        String sendMessage = String.join(" ", List.of(MessageId.NEXT_TURN.name(), "\n"));
        sendMessage(sendMessage);

        //Receive message
        String receiveMessage = receiveMessage();

        return Serdes.TURN_KIND_SERDE.deserialize(receiveMessage);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        //Send message
        String msgIdString = MessageId.CHOOSE_TICKETS.name();
        String optionsSerialized = Serdes.SORTED_BAG_TICKETS_SERDE.serialize(options);
        String sendMessage = String.join(" ", List.of(msgIdString, optionsSerialized, "\n"));
        sendMessage(sendMessage);

        //Receive message
        String receivedMessage = receiveMessage();

        return Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(receivedMessage);
    }

    @Override
    public int drawSlot() {
        //Send message
        String sendMessage = String.join(" ", List.of(MessageId.DRAW_SLOT.name(), "\n"));
        sendMessage(sendMessage);

        //Receive message
        String receivedMessage = receiveMessage();

        return Serdes.INTEGER_SERDE.deserialize(receivedMessage);
    }

    @Override
    public Route claimedRoute() {
        //Send message
        String sendMessage = String.join(" ", MessageId.ROUTE.name(), "\n");
        sendMessage(sendMessage);

        //Receive message
        String receivedMessage = receiveMessage();

        return Serdes.ROUTE_SERDE.deserialize(receivedMessage);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        //Send message
        String sendMessage = String.join(" ", MessageId.CARDS.name(), "\n");
        sendMessage(sendMessage);

        //Receive message
        String receivedMessage = receiveMessage();
        return Serdes.SORTED_BAG_CARD_SERDE.deserialize(receivedMessage);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String msgIdString = MessageId.CHOOSE_ADDITIONAL_CARDS.name();
        String optionsSerialized = Serdes.LIST_SORTED_BAG_CARD_SERDE.serialize(options);
        String sendMessage = String.join(" ", msgIdString, optionsSerialized, "\n");
        sendMessage(sendMessage);

        //Receive message
        String receiveMessage = receiveMessage();

        return Serdes.SORTED_BAG_CARD_SERDE.deserialize(receiveMessage);
    }
    //Send a message to the player that isn't on the same machine as the server
    //parameter is the serialized message (instruction) to send
    private void sendMessage(String msg){
        try{
            BufferedWriter sender = new BufferedWriter(
                    new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.US_ASCII));
            sender.write(msg);
            sender.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    //Receive a message from the player
    //returns the serialization of the message (instruction) received
    private String receiveMessage() {
        try {
            BufferedReader receiver = new BufferedReader(
                    new InputStreamReader(this.socket.getInputStream(), StandardCharsets.US_ASCII));
            return receiver.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
