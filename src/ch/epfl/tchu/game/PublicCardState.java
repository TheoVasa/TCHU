package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.lang.reflect.Array;
import java.util.*;

/**
 * generic class representing the public state of the cards, immutable
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public class PublicCardState {

    /**
     * Attributes
     */
    private final int deckSize;
    private final int discardsSize;
    private final int totalSize;
    private final List<Card> faceUpCards;

    /**
     * Constructor
     * @param faceUpCards  List of the face up cards
     * @param deckSize     size of the deck
     * @param discardsSize size of the discards
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        //Check correctness of arguments
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0);

        //Init vars
        this.faceUpCards = new ArrayList<>(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
        this.totalSize = faceUpCards.size() + deckSize + discardsSize;
    }


    /**
     * Gives the total amount of cards that aren't on any players hand, meaning the size of the discards and the deck
     * @return the total of public cards
     */
    public int totalSize() {
        return totalSize;
    }

    /**
     * Gives the five faces up cards
     * @return a list the faced up cards
     */
    public List<Card> faceUpCards() {
        return List.copyOf(faceUpCards);
    }


    /**
     * Gives the card of the specified index of the list of faced up cards
     * @param slot the index of the card in the list
     * @return the card corresponding to the index
     */
    public Card faceUpCard(int slot) {
        //Check correctness of index and return the corresponding card of the index
        return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
    }

    /**
     * Getter for the deck size
     * @return the size of the deck
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Gives the empty state of the deck
     * @return true iff the deck is empty
     */
    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

    /**
     * Getter for the size of the discards
     * @return the size of the discards
     */
    public int discardsSize() {
        return discardsSize;
    }
}
