package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * This class adapt a graphicalPlayer to the interface Player, implements Player.
 *
 * @author Théo Vasarino (313191)
 * @author Selien Wicki (314357)
 *
 *
 */
public class GraphicalPlayerAdapter implements Player {
    //Constant
    private final int BLOCKING_QUEUE_CAPACITY = 1;

    //Attributes
    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    private final BlockingQueue<SortedBag<Card>> claimCardQueue;
    private final BlockingQueue<TurnKind> turnKindQueue;
    private final BlockingQueue<Integer> cardSlotQueue;
    private final BlockingQueue<Route> claimRouteQueue;
    private final BlockingQueue<String> lastChatQueue;

    /**
     * constructs a new GraphicalPlayerAdapter and initialize all the different queue.
     */
    public GraphicalPlayerAdapter(){
        ticketsQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        claimCardQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        turnKindQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        cardSlotQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        claimRouteQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        lastChatQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        Platform.runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * get the lastChat that the player send.
     *
     * @return the chat. (String)
     */
    @Override
    public String lastChat() {
        Platform.runLater(() -> putTryCatch(lastChatQueue, graphicalPlayer.lastChat()));
        return takeTryCatch(lastChatQueue);
    }

    @Override
    public void receiveChat(String chat) {
        Platform.runLater(() -> graphicalPlayer.receiveChat(chat));
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
                () -> putTryCatch(turnKindQueue, TurnKind.DRAW_TICKETS),
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
            graphicalPlayer.chooseTickets(options, ticketChoice -> putTryCatch(ticketsQueue, ticketChoice))
        );
        return  takeTryCatch(ticketsQueue);
    }

    @Override
    public int drawSlot() {
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

    @Override
    public String receivePlayerName() {
        return null;
    }

    //Generic try catch for to handle the method ".put" for BlockingQueue
    private <E> void putTryCatch(BlockingQueue<E> queue, E element){
        try {
            queue.put(element);
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    //Generic try catch for to handle the method ".take" for BlockingQueue
    private <E> E takeTryCatch(BlockingQueue<E> queue){
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
