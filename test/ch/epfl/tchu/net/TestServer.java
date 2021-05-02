package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.test.TestRandomizer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;


public class TestServer {
    private final List<Ticket> alltickets = ChMap.tickets();
    private final List<Route> allRoutes = ChMap.routes();
    private final List<Card> allCards = Card.ALL;
    private final Random rng = TestRandomizer.newRandom();

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5107);
                Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");

            generateSendingMessage("initPlayer");
            playerProxy.initPlayers(PLAYER_1, playerNames);

            generateSendingMessage("update state");
                //init gameState and playerState
                int ticketsCount = 40;
                List<Ticket> ticketsBuffer = List.of(
                        ChMap.tickets().get(1),
                        ChMap.tickets().get(14),
                        ChMap.tickets().get(9),
                        ChMap.tickets().get(22),
                        ChMap.tickets().get(19),
                        ChMap.tickets().get(2)
                );
                List<Route> routes = ChMap.routes().subList(0, 2);
                Map<PlayerId, PublicPlayerState> ps = Map.of(
                        PlayerId.PLAYER_1, new PublicPlayerState(10, 11, routes),
                        PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of())
                );
                List<Card> facedUpCards = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
                PublicCardState cardState = new PublicCardState(facedUpCards, 30, 31);
                SortedBag<Ticket> tickets = SortedBag.of(ticketsBuffer);
                SortedBag<Card> cards = SortedBag.of(1, Card.WHITE, 1, Card.RED);

                PlayerState playerState = new PlayerState(tickets, cards, routes);
                PublicGameState gameState = new PublicGameState(ticketsCount, cardState, PlayerId.PLAYER_2, ps, null);
            playerProxy.updateState(gameState, playerState );

            generateSendingMessage("receive info");
            playerProxy.receiveInfo("Hey mate! That's an info!");

            generateSendingMessage("set initialTicketChoice");
            playerProxy.setInitialTicketChoice(tickets);

            generateSendingMessage("choose initial Tickets");
            SortedBag<Ticket> chosenTickets = playerProxy.chooseInitialTickets();

            generateSendingMessage("choose next turn");
            playerProxy.nextTurn();

            generateSendingMessage("choose tickets");

            generateSendingMessage("draw slot");
            playerProxy.drawSlot();

            generateSendingMessage("choose claim route");
            playerProxy.claimedRoute();

            generateSendingMessage("choose initialClaimCards");
            playerProxy.initialClaimCards();

            generateSendingMessage("choose additional Cards");
            playerProxy.chooseAdditionalCards(List.of(cards));

        }
        System.out.println("Server done!");
    }
    private static void generateSendingMessage(String s){
        System.out.printf("Server ask to player to %s\n", s);
    }
}
