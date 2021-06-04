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

    //The player that is not on the same machine than the server
    private final Player player;
    //The name of the server
    private final String serverName;
    private final String playerName;

    //The port to connect via tcpip
    private final int gamePort;
    private final int chatPort;

    //The socket to handle the connection of the game
    private Socket gameSocket;
    private Socket chatSocket;

    //BufferReader
    private BufferedReader gameReceiver;
    private BufferedReader chatReceiver;

    //BufferWriter
    private BufferedWriter gameSender;
    private BufferedWriter chatSender;


    /**
     * Construct a RemotePlayerClient and connect him to the server
     *
     * @param player represented by the client
     * @param serverName of the server
     * @param gamePort to connect to properly communicate with the proxy
     * @param chatPort to connect to properly communicate with the proxy
     * @param playerName the name of the player that will be send to the server just after the connection
     */
    public RemotePlayerClient(Player player,
                              String serverName,
                              int gamePort,
                              int chatPort,
                              String playerName)  throws IOException{
        this.player = player;
        this.serverName = serverName;
        this.gamePort = gamePort;
        this.chatPort = chatPort;
        this.playerName = playerName;

        //Connect to the server
        connect();
    }

    /**
     * Used to communicate with the server.
     * This method : - Wait a message from the proxy
     *               - In function of the type of the message, do the proper actions with the player.
     */
    public void runGame(){
        while (gameSocket.isConnected()){
            String receivedMessage = receiveMessage(gameReceiver);
            if (!receivedMessage.isEmpty())
                handleReceivedMessage(receivedMessage);
        }
    }

    public void runChat(){
        while (chatSocket.isConnected()){
            String receivedMessage = receiveMessage(chatReceiver);
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
                sendMessage(Serdes.SORTED_BAG_TICKETS_SERDE.serialize(chosenTickets), gameSender);
                break;
            case NEXT_TURN:
                Player.TurnKind turnKind = player.nextTurn();
                sendMessage(Serdes.TURN_KIND_SERDE.serialize(turnKind), gameSender);
                break;
            case CHOOSE_TICKETS:
                SortedBag<Ticket> options = Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(listOfData.next());
                SortedBag<Ticket> chosenOptions = player.chooseTickets(options);
                sendMessage(Serdes.SORTED_BAG_TICKETS_SERDE.serialize(chosenOptions), gameSender);
                break;
            case DRAW_SLOT:
                int drawSlot = player.drawSlot();
                sendMessage(Serdes.INTEGER_SERDE.serialize(drawSlot), gameSender);
                break;
            case ROUTE:
                Route route = player.claimedRoute();
                sendMessage(Serdes.ROUTE_SERDE.serialize(route), gameSender);
                break;
            case CARDS:
                SortedBag<Card> cards = player.initialClaimCards();
                sendMessage(Serdes.SORTED_BAG_CARD_SERDE.serialize(cards), gameSender);
                break;
            case CHOOSE_ADDITIONAL_CARDS:
                List<SortedBag<Card>> option = Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(listOfData.next());
                SortedBag<Card> chosenOption = player.chooseAdditionalCards(option);
                sendMessage(Serdes.SORTED_BAG_CARD_SERDE.serialize(chosenOption), gameSender);
                break;
            case LAST_CHAT:
                sendMessage(Serdes.STRING_SERDE.serialize(player.lastChat()), chatSender);
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
    private void connect() throws IOException{
        gameSocket = new Socket(serverName, gamePort);
        chatSocket = new Socket(serverName, chatPort);

        gameReceiver = new BufferedReader(
                        new InputStreamReader(gameSocket.getInputStream(), StandardCharsets.US_ASCII));
        chatReceiver = new BufferedReader(
                        new InputStreamReader(chatSocket.getInputStream(), StandardCharsets.US_ASCII));

        gameSender = new BufferedWriter(
                        new OutputStreamWriter(gameSocket.getOutputStream(), StandardCharsets.US_ASCII));
        chatSender = new BufferedWriter(
                        new OutputStreamWriter(chatSocket.getOutputStream(), StandardCharsets.US_ASCII));
        //Send player name
        sendMessage(Serdes.STRING_SERDE.serialize(playerName), gameSender);
    }

    //receive a message from the server.
    private String receiveMessage(BufferedReader receiver){
        try {
            String message = receiver.readLine();
            return (message == null) ? "" : message;
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    //send a message to the server.
    private  void sendMessage(String msg, BufferedWriter sender){
        try {
            msg = new StringBuilder(msg).append("\n").toString();
            sender.write(msg);
            sender.flush();
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
