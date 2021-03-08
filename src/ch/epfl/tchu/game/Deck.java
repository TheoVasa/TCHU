package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * generic class representing a deck of cards, immutable
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 *
 *
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

    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){

        List<C> shuffleList = cards.toList();
        Collections.shuffle(shuffleList, rng);

        SortedBag<C> shuffleCards = SortedBag.of(shuffleList);

        return new Deck<>(shuffleCards);

    }

    /**
     *
     * @return if the deck is empty or not
     */
    public boolean isEmpty(){

        return cards.isEmpty();
    }

    /**
     *
     * @return the top card of the deck
     */
    public C topCard(){

        Preconditions.checkArgument(!cards.isEmpty());

        return cards.get(0);

    }

    /**
     *
     * @param count the number of cards we want to get on the top
     * @return a collection of top cards from the deck
     */
    public SortedBag<C> topCards(int count){

        Preconditions.checkArgument(countIsGood(count));

        SortedBag.Builder<C> builder = new SortedBag.Builder<>();

        for(int i=0; i<count; i++)
            builder.add(cards.get(i));


        return builder.build();

    }

    /**
     *
     * @param count the number of cards we want to extract from the deck
     * @return a new deck without the top cards
     */
    public Deck<C> withoutTopCards(int count){

        Preconditions.checkArgument(countIsGood(count));

        SortedBag<C> topCards = this.topCards(count);

        return new Deck<>(cards.difference(topCards));
    }


    /**
     *
     * @return a new deck without the top card
     */

    public Deck<C> withoutTopCard(){

        return withoutTopCards(1);
    }


    /**
     *
     * @return the size of the deck
     */
    public int size() {
        return cards.size();
    }

    private boolean countIsGood(int count){

        return count>=0 && count<= cards.size();

    }

    private void test(){
        System.out.println("Theo le bg !!");
    }


}
