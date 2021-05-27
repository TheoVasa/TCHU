package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * Represent the public state of a game of tCHu.
 * It is public and immutable
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public class PublicGameState {

    //The amount of tickets in the ticket deck
    private final int ticketsCount;
    //The public information about the cards of the game
    private final PublicCardState publicCardState;
    //The id of the current player
    private final PlayerId currentPlayerId;
    //The public information of the two players of the game
    private final Map<PlayerId, PublicPlayerState> playerState;
    //The player that will finish the game (can be null if unknown)
    private final PlayerId lastPlayer;

    /**
     * Create a PublicGameState.
     *
     * @param ticketsCount    number of tickets        int totalCardSize = gameState.cardState().deckSize() + gameState.cardState().discardsSize() + playerState.cardCount()
     * @param cardState       public state of the cards
     * @param currentPlayerId the current player
     * @param playerState     the public state of the players
     * @param lastPlayer      the last player of the game
     * @throws NullPointerException     if one of the objects (except lastPlayer) is null
     * @throws IllegalArgumentException if <code>ticketCount</code> is strictly negative or
     *                                  if <code>playerState</code> does not contain exactly two pair of key/value
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        //Check the correctness of the arguments
        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);

        //init attributs and checking if they're null
        this.publicCardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(Objects.requireNonNull(playerState));
        this.lastPlayer = lastPlayer;
        this.ticketsCount = ticketsCount;
    }

    /**
     * Getter for the count of the tickets.
     *
     * @return the tickets count (int)
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Method to know if we can draw new ticket.
     *
     * @return true if the deck of tickets is not empty (boolean)
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }

    /**
     * Getter for the state of the cards
     *
     * @return the state of the cards (PublicCardState)
     */
    public PublicCardState cardState() {
        return publicCardState;
    }

    /**
     * method to know if we can draw new cards,
     * meaning if there is 5 cards or more in the deck and the discard.
     *
     * @return true if we can draw new cards (boolean)
     */
    public boolean canDrawCards() {
        return (publicCardState.deckSize() + publicCardState.discardsSize()) >= Constants.FACE_UP_CARDS_COUNT;
    }

    /**
     * Getter for the id of the current player
     *
     * @return the id of the current player (PlayerId)
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Getter for the public state of the given player
     *
     * @param playerId the id of the player
     * @return the public state of the <code>playerId</code> (PublicPlayerState)
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * to get the public state of the current player
     *
     * @return the PublicPlayerState
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * to get all routes claimed by the players during the game
     *
     * @return a List of all claimed Routes
     */
    public List<Route> claimedRoutes() {
        List<Route> claimedRoute = new ArrayList<>();
        for (PublicPlayerState m : playerState.values())
            claimedRoute.addAll(m.routes());
        return claimedRoute;
    }

    /**
     * to get the lastPlayer Id
     *
     * @return the Id of the lastPlayer or null if she's unknown
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
