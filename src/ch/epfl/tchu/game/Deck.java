package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is a generic class representing a deck containing of comparable objects, is public, final and immutable.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */

public final class Deck <C extends Comparable<C>>{

   //the cards in the deck in the sense of what the deck is containing and not necessary Cards from the tCHu game
    private final List<C> cards;

    /**
     * private constructor
     * @param cards in the deck
     */
    private Deck(List<C> cards) {
        this.cards = new ArrayList<>(cards);
    }

    /**
     * generate a new shuffle deck from a set of a comparable objects
     *
     * @param cards we want in the future deck
     * @param rng randomizer for the shuffle
     * @param <C> type
     * @return a new deck, with the given objects, shuffle with the given rng seed (Deck)
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        //Shuffle the cards of the deck
        List<C> shuffleList = cards.toList();
        Collections.shuffle(shuffleList, rng);
        return new Deck<>(shuffleList);
    }

    /**
     * Used to know if the deck is empty or not
     * @return true if the deck is empty (boolean)
     */
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    /**
     * Getter for the first card of the deck
     * @return the top card of the deck (C)
     * @throws IllegalArgumentException if the deck is empty
     */
    public C topCard(){
        //Check correctness of the argument
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.get(0);
    }

    /**
     * Getter for a given number of cards from the top of the deck
     * @param count the number of cards we want to get on the top
     * @return a collection of top cards from the deck (SortedBag)
     * @throws IllegalArgumentException if the count isn't positive or bigger than the size of the deck
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
     * Gives a new deck without a given number of firsts cards in the deck
     * @param count the number of cards we want to extract from the deck
     * @return a new deck without the top cards (Deck)
     * @throws IllegalArgumentException if the count isn't positive or bigger than the size of the deck
     */
    public Deck<C> withoutTopCards(int count){
        //Check correctness of the argument
        Preconditions.checkArgument(countIsGood(count));

        //Generate the new cards without the topCards
        List<C> newCards = new ArrayList<>();
        for (int i = count; i < cards.size(); ++i)
            newCards.add(cards.get(i));

        return new Deck<C>(newCards);
    }

    /**
     * Gives a new deck without the first card of the deck
     * @return a new deck without the top card (Deck)
     */

    public Deck<C> withoutTopCard(){
        return withoutTopCards(1);
    }

    /**
     * Getter for the size of the deck
     * @return the size of the deck (int)
     */
    public int size() {
        return cards.size();
    }

    //Use to check if the count(index) is in the range
    private boolean countIsGood(int count){
        return count>=0 && count<= cards.size();
    }
}
