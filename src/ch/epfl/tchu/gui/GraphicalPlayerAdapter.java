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

    private GraphicalPlayer graphicalPlayer;
    private BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    private BlockingQueue<SortedBag<Card>> claimCardQueue;
    private BlockingQueue<TurnKind> turnKindQueue;
    private BlockingQueue<Integer> cardSlotQueue;
    private BlockingQueue<Route> claimRouteQueue;

    public GraphicalPlayerAdapter(){
        ticketsQueue = new ArrayBlockingQueue<>(CAPACITY);
        claimCardQueue = new ArrayBlockingQueue<>(CAPACITY);
        turnKindQueue = new ArrayBlockingQueue<>(CAPACITY);
        cardSlotQueue = new ArrayBlockingQueue<>(CAPACITY);
        claimRouteQueue = new ArrayBlockingQueue<>(CAPACITY);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames);
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
        Platform.runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketChoice -> {
            try {
                ticketsQueue.put(ticketChoice);
            }catch (InterruptedException e){
                throw new Error();
            }
        }));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try{
            return ticketsQueue.take();
        }catch(InterruptedException e){
            throw new Error();
        }
    }

    @Override
        public TurnKind nextTurn() {
        graphicalPlayer.startTurn(
                () -> {
                    putTryCatch(turnKindQueue, TurnKind.DRAW_TICKETS);
                },
                i -> {
                    putTryCatch(turnKindQueue, TurnKind.DRAW_CARDS);
                    putTryCatch(cardSlotQueue, i);
                },
                (r, c) ->{
                    putTryCatch(turnKindQueue, TurnKind.CLAIM_ROUTE);
                }
        );
        return takeTryCatch(turnKindQueue);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        BlockingQueue<SortedBag<Ticket>> chosenTicket = new ArrayBlockingQueue<>(1);
        Platform.runLater(() -> graphicalPlayer.chooseTickets(options, ticketChoice -> {
            putTryCatch(chosenTicket, options);
        }));
        try{
            return chosenTicket.take();
        }catch (InterruptedException e){
            throw new Error();
        }
    }

    @Override
    public int drawSlot() {
        if (cardSlotQueue.isEmpty())
            graphicalPlayer.drawCard((i) -> putTryCatch(cardSlotQueue, i));
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
        graphicalPlayer.chooseAdditionalCards(options, (cards) -> putTryCatch(claimCardQueue, cards));
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
