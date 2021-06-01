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
 * Class representing a distant player's client,
 * in fact this class is used to dialog with the server proxy and do the right actions with the player.
 * It is final and
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class RemotePlayerClient {
    //TODO rajouter une deuxieme socket pour le chat

    //The player that is not on the same machine than the server
    private final Player player;
    //The name of the server
    private final String serverName;
    private final String playerName;
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
     * @param serverName of the server
     * @param port to connect to properly communicate with the proxy
     * @param playerName the name of the player that will be send to the server just after the connection
     */
    public RemotePlayerClient(Player player, String serverName, int port, String playerName){
        this.player = player;
        this.serverName = serverName;
        this.port = port;
        this.playerName = playerName;

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
                List<String> players = Serdes.LIST_STRING_SERDE.deserialize(listOfData.next());
                Map<PlayerId, String> playerNames = Map.of(
                        PlayerId.PLAYER_1, players.get(0),
                        PlayerId.PLAYER_2, players.get(1)
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
                sendMessage(Serdes.SORTED_BAG_TICKETS_SERDE.serialize(chosenTickets));
                break;
            case NEXT_TURN:
                Player.TurnKind turnKind = player.nextTurn();
                sendMessage(Serdes.TURN_KIND_SERDE.serialize(turnKind));
                break;
            case CHOOSE_TICKETS:
                SortedBag<Ticket> options = Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(listOfData.next());
                SortedBag<Ticket> chosenOptions = player.chooseTickets(options);
                sendMessage(Serdes.SORTED_BAG_TICKETS_SERDE.serialize(chosenOptions));
                break;
            case DRAW_SLOT:
                int drawSlot = player.drawSlot();
                sendMessage(Serdes.INTEGER_SERDE.serialize(drawSlot));
                break;
            case ROUTE:
                Route route = player.claimedRoute();
                sendMessage(Serdes.ROUTE_SERDE.serialize(route));
                break;
            case CARDS:
                SortedBag<Card> cards = player.initialClaimCards();
                sendMessage(Serdes.SORTED_BAG_CARD_SERDE.serialize(cards));
                break;
            case CHOOSE_ADDITIONAL_CARDS:
                List<SortedBag<Card>> option = Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(listOfData.next());
                SortedBag<Card> chosenOption = player.chooseAdditionalCards(option);
                sendMessage(Serdes.SORTED_BAG_CARD_SERDE.serialize(chosenOption));
                break;
            case LAST_CHAT:
                sendMessage(Serdes.STRING_SERDE.serialize(player.lastChat()));
                break;
            case RECEIVE_CHAT:
                String chat = Serdes.STRING_SERDE.deserialize(listOfData.next());
                player.receiveChat(chat);
                break;
            default:
                //do nothing
                break;
        }
    }

    //connect the client to the server.
    private void connect(){
        try{
            socket = new Socket(serverName, port);
            receiver = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            sender = new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));

        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
        //Send player name
        sendMessage(Serdes.STRING_SERDE.serialize(playerName));
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
            msg = new StringBuilder(msg).append("\n").toString();
            sender.write(msg);
            sender.flush();
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
