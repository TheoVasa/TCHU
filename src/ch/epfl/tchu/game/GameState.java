package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Represent the state of a game of tCHu, final, immutable
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */

public final class GameState extends PublicGameState {

    /**
     * attributs
     */
    private final Deck<Ticket> ticketDeck;
    public final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;

    /**
     * private constructor of a gameState
     *
     * @param ticketDeck      the tickets of the game
     * @param cardState       the cards of the game
     * @param playerState     the state of the different player in the game
     * @param currentPlayerId the ID of the current player
     * @param lastPlayer      the last player that played
     */
    private GameState(Deck<Ticket> ticketDeck, CardState cardState, Map<PlayerId, PlayerState> playerState, PlayerId currentPlayerId, PlayerId lastPlayer) {
        super(ticketDeck.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.ticketDeck = ticketDeck;
        this.cardState = cardState;
        this.playerState = Collections.unmodifiableMap(playerState);
    }

    /**
     * construction method of a gameState
     *
     * @param tickets we want to put in the intial state
     * @param rng     number for the shuffle
     * @return a new initial gameState
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        //initialise the tickets
        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);

        //initialise the cards
        Deck<Card> allCardsDeck = Deck.of(Constants.ALL_CARDS, rng);

        SortedBag<Card> cardsForPlayer1 = allCardsDeck.topCards(Constants.INITIAL_CARDS_COUNT);
        SortedBag<Card> cardsForPlayer2 = allCardsDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT).topCards(Constants.INITIAL_CARDS_COUNT);
        CardState cardState = CardState.of(allCardsDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT * PlayerId.COUNT));

        //initialise the playerStates and create the Map
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(PlayerId.class);
        PlayerState playerState1 = PlayerState.initial(cardsForPlayer1);
        PlayerState playerState2 = PlayerState.initial(cardsForPlayer2);
        playerStateMap.put(PlayerId.PLAYER_1, playerState1);
        playerStateMap.put(PlayerId.PLAYER_2, playerState2);

        //initialise the currentPlayer (last player is null)
        PlayerId currentPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));

        return new GameState(ticketDeck, cardState, playerStateMap, currentPlayer, null);
    }

    /**
     * Override methods
     */

    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * public methods
     */

    /**
     * return a given number of tickets from the top of the deck
     *
     * @param count we want to extract from the deck
     * @return a SortedBag of tickets
     * @throws IllegalArgumentException if count is nit included between 0 and the size of the tickets deck (included)
     */
    public SortedBag<Ticket> topTickets(int count) {
        //check the correctness of the argument
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return ticketDeck.topCards(count);
    }

    /**
     * generate a new gameState without a given number of tickets
     *
     * @param count we want to put off the deck
     * @return a new GameState
     * @throws IllegalArgumentException if count is nit included between 0 and the size of the tickets deck (included)
     */
    public GameState withoutTopTickets(int count) {
        //check the correctness of the argument
        Preconditions.checkArgument(count >= 0 && count < ticketsCount());
        return new GameState(ticketDeck.withoutTopCards(count), cardState, playerState, currentPlayerId(), lastPlayer());
    }

    /**
     * return the top deck card
     *
     * @return the cards
     * @throws IllegalArgumentException if the deck is empty
     */
    public Card topCard() {
        //check the correctness of the argument
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * generate a new GameState without the topDeck card
     *
     * @return the new gameState
     * @throws IllegalArgumentException if the deck is empty
     */
    public GameState withoutTopCard() {
        //check the correctness of the argument
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(ticketDeck, cardState.withoutTopDeckCard(), playerState, currentPlayerId(), lastPlayer());
    }

    /**
     * generate a new GameState with more discarded cards
     *
     * @param discardedCards we want to put in
     * @return the new game state
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(ticketDeck, cardState.withMoreDiscardedCards(discardedCards), playerState, currentPlayerId(), lastPlayer());
    }

    /**
     * generate a new gameState with a new deck recreated from the discard if needed (the deck is empty)
     *
     * @param rng for shuffling the new deck
     * @return this if the deck is empty, else return the new GameState with the new deck
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return (cardState.isDeckEmpty())
                ? new GameState(ticketDeck, cardState.withDeckRecreatedFromDiscards(rng), playerState, currentPlayerId(), lastPlayer())
                : this;
    }

    /**
     * generate a new GameState with the given ticket added tho the playerState of the given playerId
     *
     * @param playerId      we want to modify the state
     * @param chosenTickets we want to add to the given playerState
     * @return a new GameState
     * @throws IllegalArgumentException if the player already have at least one ticket
     **/
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        //Check the correctness of the argument
        Preconditions.checkArgument(playerState.get(playerId).ticketCount() == 0);
        //recreate the new playerState map
        PlayerState newPlayer = playerState.get(playerId).withAddedTickets(chosenTickets);
        return new GameState(ticketDeck, cardState, generateNewPlayerMap(newPlayer), currentPlayerId(), lastPlayer());
    }

    /**
     * generate a new GameState where the player draw drawnTicket and choose the chosenTickets
     *
     * @param drawnTickets  the player draw
     * @param chosenTickets the player choose
     * @return a new GameState
     * @throws IllegalArgumentException if the chosenTicket are not included in the drawnTickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        //Check the correctness of the argument
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        //modify the player
        PlayerState newPlayer = this.currentPlayerState().withAddedTickets(chosenTickets);
        //create new Deck of tickets
        Deck<Ticket> newTicketDeck = ticketDeck.withoutTopCards(drawnTickets.size());

        return new GameState(newTicketDeck, cardState, generateNewPlayerMap(newPlayer), currentPlayerId(), lastPlayer());
    }

    /**
     * generate a new GameState where the player took a given card among the faceUpCards
     *
     * @param slot of the faceUpCards
     * @return a new GameState
     * @throws IllegalArgumentException if we can't draw new cards form the deck
     */
    public GameState withDrawnFaceUpCard(int slot) {
        //Check correctness of the argument
        Preconditions.checkArgument(canDrawCards());
        //create new playerState
        PlayerState newPlayer = playerState.get(currentPlayerId()).withAddedCard(
                cardState.faceUpCard(slot));
        //modify the cards
        CardState newCardState = cardState.withDrawnFaceUpCard(slot);
        return new GameState(ticketDeck, newCardState, generateNewPlayerMap(newPlayer), currentPlayerId(), lastPlayer());
    }

    /**
     * generate a new GameState where the top card of the deck is given to the current player
     *
     * @return a new GameState
     * @throws IllegalArgumentException if we can't draw new cards
     */
    public GameState withBlindlyDrawnCard() {
        //Check correctness of the argument
        Preconditions.checkArgument(canDrawCards());
        //create new playerState
        PlayerState newPlayer = playerState.get(currentPlayerId()).withAddedCard(
                cardState.topDeckCard());
        //modify the cardsCardState newCards = cardState.withDrawnFaceUpCard(slot);
        CardState newCards = cardState.withoutTopDeckCard();
        return new GameState(ticketDeck, newCards, generateNewPlayerMap(newPlayer), currentPlayerId(), lastPlayer());
    }

    /**
     * generate a new GameState where the player claimed the given route with the given cards
     *
     * @param route claimed by the player
     * @param cards used by the player to claim the route
     * @return a new GameState
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        //modify the player state
        PlayerState newPlayer = playerState.get(currentPlayerId()).withClaimedRoute(route, cards);
        return new GameState(ticketDeck, cardState, generateNewPlayerMap(newPlayer), currentPlayerId(), lastPlayer());
    }

    /**
     * method to know if the last turn begins, meaning the current player has 2 cards or less and the lastPlayer is unknown
     *
     * @return true if the last turn begins
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && playerState.get(currentPlayerId()).carCount() <= 2;
    }

    /**
     * End the turn of the player, indeed this method change the current player and if the last turn begins, set the last player as the current player
     *
     * @return the new GameState
     */
    public GameState forNextTurn() {
        PlayerId newCurrentPlayer = currentPlayerId().next();
        return (lastTurnBegins())
                ? new GameState(ticketDeck, cardState, playerState, newCurrentPlayer, currentPlayerId())
                : new GameState(ticketDeck, cardState, playerState, newCurrentPlayer, lastPlayer());
    }

    /**
     * private method
     */

    /**
     * modify the playerState of the current player by the given playerState
     *
     * @param player we want to replace in the map
     * @return the new playerState map
     */
    private Map<PlayerId, PlayerState> generateNewPlayerMap(PlayerState player) {
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(playerState);
        newMap.replace(currentPlayerId(), player);
        return newMap;
    }
}
