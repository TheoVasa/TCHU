package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * generic class representing the state of the cards in the game, immutable, extends PublicCardState
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class CardState extends PublicCardState{

    /**
     * Attributes
     */
    private final Deck<Card> deck;
    private final SortedBag<Card> discard;

    /**
     * Private constructor
     * @param faceUpCards the faced up card that every body can see
     * @param deck the deck of the came
     * @param discard the discard of the game
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discard) {
        super(faceUpCards, deck.size(), discard.size());

        //Init vars
        this.deck = deck;
        this.discard = discard;
    }

    /**
     * Create a new CardState with the given deck and a empty discard,
     * the first five cards of the given deck will be the faced up cards
     * @param deck the deck for the new state
     * @return a new CardState with zero discard cards
     */
    public CardState of (Deck<Card> deck){
        //Check correctness of argument
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(),
                deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * Create a new CardState with the indexed faced card replaced with the first card of the deck
     * @param slot the index of the faced card we wand to replace
     * @return a new CardState with the updated deck and faced cards
     */
    public CardState withDrawnFaceUpCard(int slot){
        //Check correctness of the argument
        Preconditions.checkArgument(slot <= Constants.FACE_UP_CARDS_COUNT && slot >= 0);

        //Set the new face up cards
        List<Card> faceUpCards = this.faceUpCards();
        faceUpCards.set(slot, deck.topCard());

        return new CardState(faceUpCards, deck.withoutTopCard(), discard);
    }

    /**
     * Getter for the first card of the deck
     * @return the first card of the deck
     */
    public Card topDeckCard(){
        //Check correctness of argument
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }

    /**
     * Generate a new CardState without the first card of the deck
     * @return a new CardState without the first card of the deck
     */
    public CardState withoutTopDeckCard(){
        //Check correctness of argument
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(this.faceUpCards(), deck.withoutTopCard(), discard);
    }

    /**
     * Shuffel the discard and the deck to generate a new deck
     * @param rng the random shuffle
     * @return a new CardState with the shuffled new deck
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        //Check correctness of argument
        Preconditions.checkArgument(deck.isEmpty());

        //Create the new list of cards for the deck (containing the discards cards)
        SortedBag.Builder<Card> newDeckBuilder = new SortedBag.Builder<>();
        newDeckBuilder.add(deck.topCards(deck.size()));
        newDeckBuilder.add(discard);

        return new CardState(faceUpCards(), Deck.of(newDeckBuilder.build(), rng), discard);
    }

    /**
     * Add new discardedCards
     * @param additionalDiscards the cards we want to discard
     * @return a new CardState with the added discardCards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        //Create the new discard
        SortedBag.Builder<Card> newDiscard = new SortedBag.Builder<>();
        newDiscard.add(discard);
        newDiscard.add(additionalDiscards);

        return new CardState(faceUpCards(), deck, newDiscard.build());
    }
}