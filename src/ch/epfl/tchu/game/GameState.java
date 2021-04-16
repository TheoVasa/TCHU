package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Represent the private state of a game of tCHu.
 * It is public, final, immutable and extends PublicGameState.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */

public final class GameState extends PublicGameState {
    //The deck of th tickets of the game
    private final Deck<Ticket> ticketDeck;
    //The state of the cards of the game
    private final CardState cardState;
    //The state of the two player of the game
    private final Map<PlayerId, PlayerState> playerState;

    //Create a GameState
    private GameState(Deck<Ticket> ticketDeck, CardState cardState, Map<PlayerId, PlayerState> playerState, PlayerId currentPlayerId, PlayerId lastPlayer) {
        super(ticketDeck.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.ticketDeck = ticketDeck;
        this.cardState = cardState;
        this.playerState = Map.copyOf(playerState);
    }

    /**
     * Initialize the state of the game.
     * Useful for the beginning of the game.
     *
     * @param tickets the tickets we want to put in the initial state of the game
     * @param rng     a random number for the shuffle
     * @return a new initial gameState (GameState)
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

    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * Gives the given amount of tickets from the top of the ticket deck.
     *
     * @param count the amount of thickets we want from the top of the ticket deck
     * @return the top tickets of the ticket deck (SortedBag)
     * @throws IllegalArgumentException if <code>count</code> is not between 0 and the size of the tickets deck (included)
     */
    public SortedBag<Ticket> topTickets(int count) {
        //check the correctness of the argument
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return ticketDeck.topCards(count);
    }

    /**
     * Generate a new gameState without a certain number of tickets on the top of tickets deck.
     *
     * @param count the amount of tickets we want to remove from the top of the deck of tickets
     * @return a game state without the top tickets (GameState)
     * @throws IllegalArgumentException if <code>count</code> is not between 0 and the size of the tickets deck (included)
     */
    public GameState withoutTopTickets(int count) {
        //check the correctness of the argument
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return new GameState(ticketDeck.withoutTopCards(count), cardState, playerState, currentPlayerId(), lastPlayer());
    }

    /**
     * Gives the top deck card.
     *
     * @return the top card of the deck of cards (Card)
     * @throws IllegalArgumentException if the card deck is empty
     */
    public Card topCard() {
        //check the correctness of the argument
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * Generate a new GameState without the top card of the deck of cards.
     *
     * @return the new gameState without the top card of the deck of cards (GameState)
     * @throws IllegalArgumentException if the card deck is empty
     */
    public GameState withoutTopCard() {
        //check the correctness of the argument
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(ticketDeck, cardState.withoutTopDeckCard(), playerState, currentPlayerId(), lastPlayer());
    }

    /**
     * Generate a new GameState with more discarded cards.
     *
     * @param discardedCards the cards we want to put in the discard
     * @return the new game state (GameState)
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(ticketDeck, cardState.withMoreDiscardedCards(discardedCards), playerState, currentPlayerId(), lastPlayer());
    }

    /**
     * Generate a new gameState with a new deck recreated from the discard if needed (the deck is empty).
     *
     * @param rng a random number for shuffling the new deck
     * @return <code>this</code> if the deck is empty, else return the new GameState with the new deck (GameState)
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return (cardState.isDeckEmpty())
                ? new GameState(ticketDeck, cardState.withDeckRecreatedFromDiscards(rng), playerState, currentPlayerId(), lastPlayer())
                : this;
    }

    /**
     * Generate a new GameState with the given ticket added tho the playerState of the given playerId.
     * Useful at the beginning of the game
     *
     * @param playerId      the id of the player we want to add the tickets
     * @param chosenTickets the tickets that the player chose at the begining of the game
     * @return A game state with tickets added to the given player (GameState)
     * @throws IllegalArgumentException if the player already has at least one ticket
     **/
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        //Check the correctness of the argument
        Preconditions.checkArgument(playerState.get(playerId).ticketCount() == 0);
        //recreate the new playerState map
        PlayerState newPlayer = playerState.get(playerId).withAddedTickets(chosenTickets);
        return new GameState(ticketDeck, cardState, generateNewPlayerMap(newPlayer, playerId), currentPlayerId(), lastPlayer());
    }

    /**
     * Generate a new GameState where the player draw some tickets and choose some of them.
     *
     * @param drawnTickets  the drawn tickets
     * @param chosenTickets the tickets the player has chosen
     * @return a game state with the chosen tickets added to the given player (GameState)
     * @throws IllegalArgumentException if the <code>chosenTicket</code> are not included in the <code>drawnTickets</code>
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        //Check the correctness of the argument
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        //modify the player
        PlayerState newPlayer = this.currentPlayerState().withAddedTickets(chosenTickets);
        //create new Deck of tickets
        Deck<Ticket> newTicketDeck = ticketDeck.withoutTopCards(drawnTickets.size());

        return new GameState(newTicketDeck, cardState, generateNewPlayerMap(newPlayer, currentPlayerId()), currentPlayerId(), lastPlayer());
    }

    /**
     * Generate a new GameState where the player draw a given card among the faced up cards
     *
     * @param slot the position of the faced up card that is drawn
     * @return a game state where the drawn faced up card is given to the player,
     * the empty slot is replaced by the top card of the deck of cards (GameState)
     * @throws IllegalArgumentException if we can not draw new cards form the deck
     */
    public GameState withDrawnFaceUpCard(int slot) {
        //Check correctness of the argument
        Preconditions.checkArgument(canDrawCards());
        //create new playerState
        PlayerState newPlayer = playerState.get(currentPlayerId()).withAddedCard(
                cardState.faceUpCard(slot));
        //modify the cards
        CardState newCardState = cardState.withDrawnFaceUpCard(slot);
        return new GameState(ticketDeck, newCardState, generateNewPlayerMap(newPlayer, currentPlayerId()), currentPlayerId(), lastPlayer());
    }

    /**
     * Generate a new GameState where the top card of the deck is given to the current player.
     *
     * @return a game state with the top card of the deck of cards given to the current player (GameState)
     * @throws IllegalArgumentException if we can not draw a new card from the deck of cards
     */
    public GameState withBlindlyDrawnCard() {
        //Check correctness of the argument
        Preconditions.checkArgument(canDrawCards());
        //create new playerState
        PlayerState newPlayer = playerState.get(currentPlayerId()).withAddedCard(
                cardState.topDeckCard());
        //modify the cardsCardState newCards = cardState.withDrawnFaceUpCard(slot);
        CardState newCards = cardState.withoutTopDeckCard();
        return new GameState(ticketDeck, newCards, generateNewPlayerMap(newPlayer, currentPlayerId()), currentPlayerId(), lastPlayer());
    }

    /**
     * Generate a new GameState where the player claimed the given route with the given cards.
     *
     * @param route the route the player wants to claim
     * @param cards the cards used by the player to claim the route
     * @return a game state where the player has claimed <code>route</code>
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        //modify the player state
        PlayerState newPlayer = playerState.get(currentPlayerId()).withClaimedRoute(route, cards);
        return new GameState(ticketDeck, cardState.withMoreDiscardedCards(cards), generateNewPlayerMap(newPlayer, currentPlayerId()), currentPlayerId(), lastPlayer());
    }

    /**
     * Method to know if the last turn begins,
     * meaning the current player has 2 cards or less and the lastPlayer is unknown
     *
     * @return true if the current player has 2 cards or less and the lastPlayer is unknown
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && playerState.get(currentPlayerId()).carCount() <= 2;
    }

    /**
     * End the turn of the player,
     * indeed this method changes the current player,
     * if the last turn begins then set the last player as the current player
     *
     * @return the game state with a new new current player. additionally,
     * if the last turn begins then the current player is set to be the last player
     */
    public GameState forNextTurn() {
        PlayerId newCurrentPlayer = currentPlayerId().next();
        return (lastTurnBegins())
                ? new GameState(ticketDeck, cardState, playerState, newCurrentPlayer, currentPlayerId())
                : new GameState(ticketDeck, cardState, playerState, newCurrentPlayer, lastPlayer());
    }


    //Modify the map of the player
    //Only the given player will be modified
    private Map<PlayerId, PlayerState> generateNewPlayerMap(PlayerState player, PlayerId id) {
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(playerState);
        newMap.replace(id, player);
        return newMap;
    }
}
