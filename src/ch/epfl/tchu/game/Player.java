package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Represent a player in tCHu.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public interface Player {

    /**
     * Communicate at the player his own identity and the name of the different player, stocked in the given Map.
     *
     * @param ownId       the id of the player
     * @param playerNames the names of the different player of the game
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Communicate information during the game.
     *
     * @param info the information we want to communicate
     */
    void receiveInfo(String info);

    /**
     * Used to change the state and communicate the player the new game state and his own state.
     *
     * @param newState the new state of the game
     * @param ownState the state of the player
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Used to communicate to the player the tickets he choose to begin the game
     *
     * @param tickets the tickets the player choose
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Ask the player to choose the initial tickets.
     *
     * @return a SortedBag of tickets (SortedBag< Tickets >>)
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Used to know the kind of action the player want to do during his turn.
     *
     * @return a type of action the player wants to do for the turn (TurnKind)
     */
    TurnKind nextTurn();

    /**
     * Used when the player decide to choose additional tickets during the game,
     * communicate the drawn ticket and the chosen ones.
     *
     * @param options the additional tickets he can choose
     * @return the tickets the player chose (SortedBag< Tickets >>)
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Used to know where the player want to draw a new Card, meaning the face up cards or the top of the deck.
     *
     * @return the slot of the draw, meaning 0 to 4 in the case the player draw a face up cards,
     * or Constants.DECK_SLOT if he wants to pick up the top deck card
     */
    int drawSlot();

    /**
     * When a player decide to try to claim a route, used to know which route it is.
     *
     * @return the route the player tries to claim (Route)
     */
    Route claimedRoute();

    /**
     * When a player decide to try to claim a route, used to know which initial he use to do it.
     *
     * @return the initial cards he used to try to claim an route (SortedBag< Card >>)
     */
    SortedBag<Card> initialClaimCards();

    /**
     * When a player decide to try to claim a route,
     * used to ask the player which additional cards he choose (if there is some).
     *
     * @param options the possibility of additional cards
     * @return the cards the player want play additional,
     * return a void SortedBag if he decide to not claim the route (SortedBag< Tickets >>)
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * represent all different actions the player can do during his turn, nested in Player.
     */
    enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         * All the values of the enumeration
         */
        public final static List<TurnKind> ALL = List.of(TurnKind.values());
    }
}
