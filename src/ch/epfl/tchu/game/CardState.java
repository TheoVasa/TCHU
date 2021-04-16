package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * This class represent the state of the cards in the game, is generic, immutable and extends PublicCardState.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class CardState extends PublicCardState {

    /**
     * Attributes
     */

    //the deck in the game
    private final Deck<Card> deck;
    //the discard in the game
    private final SortedBag<Card> discard;

    /**
     * Private constructor
     *
     * @param faceUpCards the faced up card that every body can see.
     * @param deck        the deck of the came.
     * @param discard     the discard of the game.
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discard) {
        super(faceUpCards, deck.size(), discard.size());

        //Init vars
        this.deck = deck;
        this.discard = discard;
    }

    /**
     * Create a new CardState with a given deck.
     *
     * @param deck the deck for the new state.
     * @return a new CardState with an empty discard, the first five cards of the given deck will be the faced up cards. (CardState)
     * @throws IllegalArgumentException if the given deck don't contain at least 5 cards.
     */
    public static CardState of(Deck<Card> deck) {
        //Check correctness of argument
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        //Generate the facedUpCards (for the ordering we can't use deck.topCards(...))
        List<Card> facedUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();

        return new CardState(facedUpCards, deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * Create a new CardState with the indexed faced card replaced with the first card of the deck.
     *
     * @param slot the index of the faced card we wand to replace.
     * @return a new CardState with the updated deck and faced cards. (CardState)
     * @throws IllegalArgumentException if the deck is empty.
     * @throws IndexOutOfBoundsException if the slot is negative or is bigger than the number of faced up cards (given by Constants.FACE_UP_CARDS_COUNT).
     */
    public CardState withDrawnFaceUpCard(int slot) {
        //Check correctness of the argument
        Preconditions.checkArgument(!deck.isEmpty());

        //Set the new face up cards, throws IndexOutOfBound if  0<=sloT<=Constant.FACE_UP_CARDS_COUNT
        List<Card> faceUpCards = new ArrayList<>(super.faceUpCards());
        faceUpCards.set(Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT), deck.topCard());

        return new CardState(faceUpCards, deck.withoutTopCard(), discard);
    }

    /**
     * Getter for the first card of the deck.
     *
     * @return the first card of the deck. (Card)
     * @throws IllegalArgumentException if the deck is empty.
     */
    public Card topDeckCard() {
        //Check correctness of argument
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }

    /**
     * Generate a new CardState without the first card of the deck.
     *
     * @return a new CardState without the first card of the deck. (CardState)
     * @throws IllegalArgumentException if the deck is empty.
     */
    public CardState withoutTopDeckCard() {
        //Check correctness of argument
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(super.faceUpCards(), deck.withoutTopCard(), discard);
    }

    /**
     * Recreate a new deck from all the discarded cards, used when the deck is empty.
     *
     * @param rng the random seed for the shuffle.
     * @return a new CardState with the shuffled new deck created from the discard. (CardState)
     * @throws IllegalArgumentException if the deck isn't empty.
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        //Check correctness of argument
        Preconditions.checkArgument(deck.isEmpty());

        //Create the new list of cards for the deck (containing the discards cards)
        SortedBag.Builder<Card> newDeckBuilder = new SortedBag.Builder<>();
        newDeckBuilder.add(discard);

        return new CardState(super.faceUpCards(), Deck.of(newDeckBuilder.build(), rng), SortedBag.of());
    }

    /**
     * Used to add new discarded cards.
     *
     * @param additionalDiscards the cards we want to discard
     * @return a new CardState with the added discardCards. (CardState)
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        //Create the new discard
        SortedBag.Builder<Card> newDiscard = new SortedBag.Builder<>();
        newDiscard.add(discard);
        newDiscard.add(additionalDiscards);

        return new CardState(faceUpCards(), deck, newDiscard.build());
    }
}
