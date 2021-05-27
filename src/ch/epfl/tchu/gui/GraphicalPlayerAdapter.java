package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GraphicalPlayerAdapter implements Player {
    //Constant
    private final int CAPACITY = 1;

    //Attributes
    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    private final BlockingQueue<SortedBag<Card>> claimCardQueue;
    private final BlockingQueue<TurnKind> turnKindQueue;
    private final BlockingQueue<Integer> cardSlotQueue;
    private final BlockingQueue<Route> claimRouteQueue;


    public GraphicalPlayerAdapter(){
        ticketsQueue = new ArrayBlockingQueue<>(CAPACITY);
        claimCardQueue = new ArrayBlockingQueue<>(CAPACITY);
        turnKindQueue = new ArrayBlockingQueue<>(CAPACITY);
        cardSlotQueue = new ArrayBlockingQueue<>(CAPACITY);
        claimRouteQueue = new ArrayBlockingQueue<>(CAPACITY);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        Platform.runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    @Override
    public void receiveInfo(String info) {
        Platform.runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        Platform.runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        Platform.runLater(() ->
            graphicalPlayer.chooseTickets(tickets, ticketChoice -> putTryCatch(ticketsQueue, ticketChoice))
        );
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return takeTryCatch(ticketsQueue);
    }

    @Override
    public TurnKind nextTurn() {
        Platform.runLater(() ->
            graphicalPlayer.startTurn(
                () -> {
                    putTryCatch(turnKindQueue, TurnKind.DRAW_TICKETS);
                },
                i -> {
                    putTryCatch(turnKindQueue, TurnKind.DRAW_CARDS);
                    putTryCatch(cardSlotQueue, i);
                },
                (r, c) ->{
                    putTryCatch(claimCardQueue, c);
                    putTryCatch(claimRouteQueue, r);
                    putTryCatch(turnKindQueue, TurnKind.CLAIM_ROUTE);
                }
            )
        );
        return takeTryCatch(turnKindQueue);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        Platform.runLater(() ->
            graphicalPlayer.chooseTickets(options, ticketChoice -> putTryCatch(ticketsQueue, options))
        );
        return  takeTryCatch(ticketsQueue);
    }

    @Override
    public int drawSlot() {
        System.out.println("drawSlot");
        if (cardSlotQueue.isEmpty())
            Platform.runLater(() -> graphicalPlayer.drawCard((i) -> putTryCatch(cardSlotQueue, i)));
        return takeTryCatch(cardSlotQueue);
    }

    @Override
    public Route claimedRoute() {
        return takeTryCatch(claimRouteQueue);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return takeTryCatch(claimCardQueue);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        Platform.runLater(() -> graphicalPlayer.chooseAdditionalCards(options, (cards) -> putTryCatch(claimCardQueue, cards)));
        return takeTryCatch(claimCardQueue);
    }

    private <E> void putTryCatch(BlockingQueue<E> queue, E element){
        try {
            queue.put(element);
        }catch (InterruptedException e){
            throw new Error();
        }
    }

    private <E> E takeTryCatch(BlockingQueue<E> queue){
        try {
            return queue.take();
        }catch (InterruptedException e){
            throw new Error();
        }
    }
}
