package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * Represent the public state of a game of tCHu, immutable
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public class PublicGameState {

    /**
     * attributs
     */
    private final int ticketsCount;
    private final PublicCardState publicCardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Constructor of PublicGameState, public
     * @param ticketsCount number of tickets
     * @param cardState public state of the cards/ cars
     * @param currentPlayerId the current player
     * @param playerState public state of the players
     * @param lastPlayer of the game
     * @throws NullPointerException if one of the objects is null (except lastPlayer)
     * @throws IllegalArgumentException if ticketCount is strictly negative
     * @throws IllegalArgumentException if playerState dont contain exactly two pair of key/value
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        //Check the correctness of the arguments
        Preconditions.checkArgument(ticketsCount>=0);
        Preconditions.checkArgument(playerState.size()==2);

        //init attributs and checking if they're null
        this.publicCardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(Objects.requireNonNull(playerState));
        this.lastPlayer = lastPlayer;
        this.ticketsCount = ticketsCount;
    }

    /**
     * getter of ticketCount
     * @return the ticketCount
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * method to know if we can draw new tickets
     * @return true if the deck of tickets is empty
     */
    public boolean canDrawTickets(){
        return ticketsCount!=0;
    }

    /**
     * getter for the cardState
     * @return the cardState
     */
    public PublicCardState cardState(){
        return publicCardState;
    }

    /**
     * method to know if we can draw new cards (meaning 5 more)
     * @return if we can draw new cards
     */
    public boolean canDrawCards(){
        return (publicCardState.deckSize()+ publicCardState.discardsSize()) >= 5;
    }

    /**
     * getter for currentPlayerId
     * @return the currentPlayerId
     */
    public PlayerId currentPlayerId(){
        return currentPlayerId;
    }

    /**
     * return the public state of the given Player Id
     * @param playerId of the player
     * @return the publicPlayerState
     */
    public PublicPlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     * to get the public state of the current player
     * @return the PublicPlayerState
     */
    public PublicPlayerState currentPlayerState(){
      return playerState.get(currentPlayerId);
    }

    /**
     * to get all routes claimed by the players during the game
     * @return a List of all claimed Routes
     */
    public List<Route> claimedRoutes(){
        List<Route> claimedRoute = new ArrayList<>();
        for(PublicPlayerState m : playerState.values())
            claimedRoute.addAll(m.routes());
        return claimedRoute;
    }

    /**
     * to get the lastPlayer Id
     * @return the Id of the lastPlayer or null if she's unknown
     */
    public PlayerId lastPlayer(){
       return lastPlayer;
    }
}
