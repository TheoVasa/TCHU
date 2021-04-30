package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * class represent the distant player's client, in fact this class is used to dialog with the server proxy and do the right actions with the player.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public class RemotePlayerClient {

    //The player that is not on the same machine than the server
    private final Player player;
    //The name of the server
    private final String name;
    //The port to connect via tcpip
    private final int port;
    //The socket to handle the connection of the game
    private Socket socket;
    //BufferReader
    private BufferedReader receiver;
    //BufferWriter
    private BufferedWriter sender;

    /**
     * Construct a RemotePlayerClient and connect him to the server
     *
     * @param player represented by the client
     * @param name of the server
     * @param port to connect to properly communicate with the proxy
     */
    public RemotePlayerClient(Player player, String name, int port){
        this.player = player;
        this.name = name;
        this.port = port;

        //Connect to the server
        connect();
    }

    /**
     * Used to communicate with the server.
     * This method : - Wait a message from the proxy
     *               - In function of the type of the message, do the proper actions with the player.
     */
    public void run(){
        while (socket.isConnected()){
            String receivedMessage = receiveMessage();
            if (!receivedMessage.isEmpty())
                handleReceivedMessage(receivedMessage);
        }
    }

    //In function of the type of message, deserialize and do the proper actions with the player.
    private void handleReceivedMessage(String msg){
        Iterator<String> listOfData = Arrays.stream(msg.split(Pattern.quote(" "), -1)).iterator();
        switch (MessageId.valueOf(listOfData.next())){
            case INIT_PLAYERS :
                PlayerId ownId = Serdes.PLAYER_ID_SERDE.deserialize(listOfData.next());
                Map<PlayerId, String> playerNames = Map.of(
                        PlayerId.PLAYER_1, Serdes.STRING_SERDE.deserialize(listOfData.next()),
                        PlayerId.PLAYER_2, Serdes.STRING_SERDE.deserialize(listOfData.next())
                );
                player.initPlayers(ownId, playerNames);
                break;
            case RECEIVE_INFO :
                String info = Serdes.STRING_SERDE.deserialize(listOfData.next());
                player.receiveInfo(info);
                break;
            case UPDATE_STATE:
                PublicGameState newState = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(listOfData.next());
                PlayerState ownState = Serdes.PLAYER_STATE_SERDE.deserialize(listOfData.next());
                player.updateState(newState, ownState);
                break;
            case SET_INITIAL_TICKETS:
                SortedBag<Ticket> tickets = Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(listOfData.next());
                player.setInitialTicketChoice(tickets);
                break;
            case CHOOSE_INITIAL_TICKETS:
                SortedBag<Ticket> chosenTickets = player.chooseInitialTickets();
                String serializedChosenTickets = Serdes.SORTED_BAG_TICKETS_SERDE.serialize(chosenTickets);
                String msgToSend = new StringBuilder(serializedChosenTickets).append("\n").toString();
                sendMessage(msgToSend);
                break;
            case NEXT_TURN:
                Player.TurnKind turnKind = player.nextTurn();
                String serializedTurnKind = Serdes.TURN_KIND_SERDE.serialize(turnKind);
                msgToSend = new StringBuilder(serializedTurnKind).append("\n").toString();
                sendMessage(msgToSend);
                break;
            case CHOOSE_TICKETS:
                SortedBag<Ticket> options = Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(listOfData.next());
                SortedBag<Ticket> chosenOptions = player.chooseTickets(options);
                String serializedChosenOptions = Serdes.SORTED_BAG_TICKETS_SERDE.serialize(chosenOptions);
                msgToSend = new StringBuilder(serializedChosenOptions).append("\n").toString();
                sendMessage(msgToSend);
                break;
            case DRAW_SLOT:
                int drawSlot = player.drawSlot();
                String serializedDrawSlot = Serdes.INTEGER_SERDE.serialize(drawSlot);
                msgToSend = new StringBuilder(serializedDrawSlot).append("\n").toString();
                sendMessage(msgToSend);
                break;
            case ROUTE:
                Route route = player.claimedRoute();
                String serializedRoute = Serdes.ROUTE_SERDE.serialize(route);
                msgToSend = new StringBuilder(serializedRoute).append("\n").toString();
                sendMessage(msgToSend);
                break;
            case CARDS:
                SortedBag<Card> cards = player.initialClaimCards();
                String serializedCards = Serdes.SORTED_BAG_CARD_SERDE.serialize(cards);
                msgToSend = new StringBuilder(serializedCards).append("\n").toString();
                sendMessage(msgToSend);
                break;
            case CHOOSE_ADDITIONAL_CARDS:
                List<SortedBag<Card>> option = Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(listOfData.next());
                SortedBag<Card> chosenOption = player.chooseAdditionalCards(option);
                String serializedChoseOption = Serdes.SORTED_BAG_CARD_SERDE.serialize(chosenOption);
                msgToSend = new StringBuilder(serializedChoseOption).append("\n").toString();
                sendMessage(msgToSend);
                break;
            default:
                //do nothing
                break;
        }
    }

    //connect the client to the server.
    private void connect(){
        try{
            socket = new Socket(name, port);
            receiver = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            sender = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    //receive a message from the server.
    private String receiveMessage(){
        try {
            String message = receiver.readLine();
            return (message == null) ? "" : message;
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    //send a message to the server.
    private  void sendMessage(String msg){
        try {
            sender.write(msg);
            System.out.println(msg);
            sender.flush();
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
