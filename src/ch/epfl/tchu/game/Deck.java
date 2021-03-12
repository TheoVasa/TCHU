package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * generic class representing a deck of cards, immutable
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */

public final class Deck <C extends Comparable<C>>{

    /**
     * the cards in the deck
     */
    private final SortedBag<C> cards;

    /**
     * private constructor
     * @param cards in the deck
     */
    private Deck(SortedBag<C> cards) {
        this.cards = cards;
    }

    /**
     * generate a new shuffle deck from a set of cards
     * @param cards we want in the future deck
     * @param rng for the shuffle
     * @param <C> type
     * @return a new deck, with the cards we want, shuffle
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        //Shuffle the cards of the deck
        List<C> shuffleList = cards.toList();
        Collections.shuffle(shuffleList, rng);
        SortedBag<C> shuffleCards = SortedBag.of(shuffleList);

        return new Deck<>(shuffleCards);
    }

    /**
     * Give the empty state of the cards in the deck
     * @return true if the deck is empty
     */
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    /**
     * Getter for the first card of the deck
     * @return the top card of the deck
     */
    public C topCard(){
        //Check correctness of the argument
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.get(0);
    }

    /**
     * Getter for the first n cards of the top of the deck
     * @param count the number of cards we want to get on the top
     * @return a collection of top cards from the deck
     */
    public SortedBag<C> topCards(int count){
        //Check correctness of the argument
        Preconditions.checkArgument(countIsGood(count));

        //Get the top cards in a new SortedBag
        SortedBag.Builder<C> builder = new SortedBag.Builder<>();
        for(int i=0; i<count; i++)
            builder.add(cards.get(i));

        return builder.build();
    }

    /**
     * Gives a new deck without the first cards of the deck
     * @param count the number of cards we want to extract from the deck
     * @return a new deck without the top cards
     */
    public Deck<C> withoutTopCards(int count){
        //Check correctness of the argument
        Preconditions.checkArgument(countIsGood(count));
        Preconditions.checkArgument(!cards.isEmpty());

        return new Deck<>(cards.difference(this.topCards(count)));
    }

    /**
     * Gives a new deck without the first card of the deck
     * @return a new deck without the top card
     */

    public Deck<C> withoutTopCard(){
        return withoutTopCards(1);
    }

    /**
     * Getter for the size of the deck
     * @return the size of the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Check that the count(index) is in the range
     * @param count the count(index) we want to check
     * @return true if the count is within the range
     */
    private boolean countIsGood(int count){
        return count>=0 && count<= cards.size();
    }
}
