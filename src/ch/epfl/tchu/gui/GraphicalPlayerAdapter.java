package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;

public class GraphicalPlayerAdapter implements Player {
    /**
     * Communicate at the player his own identity and the name of the different player, stocked in the given Map.
     *
     * @param ownId       the id of the player
     * @param playerNames the names of the different player of the game
     */
    @Override public void initPlayers(PlayerId ownId,
            Map<PlayerId, String> playerNames) {

    }

    /**
     * Communicate information during the game.
     *
     * @param info the information we want to communicate
     */
    @Override public void receiveInfo(String info) {

    }

    /**
     * Used to change the state and communicate the player the new game state and his own state.
     *
     * @param newState the new state of the game
     * @param ownState the state of the player
     */
    @Override public void updateState(PublicGameState newState,
            PlayerState ownState) {

    }

    /**
     * Used to communicate to the player the tickets he choose to begin the game
     *
     * @param tickets the tickets the player choose
     */
    @Override public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

    }

    /**
     * Ask the player to choose the initial tickets.
     *
     * @return a SortedBag of tickets (SortedBag< Tickets >>)
     */
    @Override public SortedBag<Ticket> chooseInitialTickets() {
        return null;
    }

    /**
     * Used to know the kind of action the player want to do during his turn.
     *
     * @return a type of action the player wants to do for the turn (TurnKind)
     */
    @Override public TurnKind nextTurn() {
        return null;
    }

    /**
     * Used when the player decide to choose additional tickets during the game,
     * communicate the drawn ticket and the chosen ones.
     *
     * @param options the additional tickets he can choose
     * @return the tickets the player chose (SortedBag< Tickets >>)
     */
    @Override public SortedBag<Ticket> chooseTickets(
            SortedBag<Ticket> options) {
        return null;
    }

    /**
     * Used to know where the player want to draw a new Card, meaning the face up cards or the top of the deck.
     *
     * @return the slot of the draw, meaning 0 to 4 in the case the player draw a face up cards,
     * or Constants.DECK_SLOT if he wants to pick up the top deck card
     */
    @Override public int drawSlot() {
        return 0;
    }

    /**
     * When a player decide to try to claim a route, used to know which route it is.
     *
     * @return the route the player tries to claim (Route)
     */
    @Override public Route claimedRoute() {
        return null;
    }

    /**
     * When a player decide to try to claim a route, used to know which initial he use to do it.
     *
     * @return the initial cards he used to try to claim an route (SortedBag< Card >>)
     */
    @Override public SortedBag<Card> initialClaimCards() {
        return null;
    }

    /**
     * When a player decide to try to claim a route,
     * used to ask the player which additional cards he choose (if there is some).
     *
     * @param options the possibility of additional cards
     * @return the cards the player want play additional,
     * return a void SortedBag if he decide to not claim the route (SortedBag< Tickets >>)
     */
    @Override public SortedBag<Card> chooseAdditionalCards(
            List<SortedBag<Card>> options) {
        return null;
    }
}
