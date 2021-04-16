package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * This class represent the public state of the cards, generic and immutable.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public class PublicCardState {

    /**
     * Attributes
     */

    //the size of the deck
    private final int deckSize;
    //the size of the discard
    private final int discardsSize;
    //the total size, namely the deck and the discard
    private final int totalSize;
    //the face up cards in the game
    private final List<Card> faceUpCards;

    /**
     * Construct all the public states of the cards in the game.
     * @param faceUpCards  List of the face up cards
     * @param deckSize     size of the deck
     * @param discardsSize size of the discards
     * @throws IllegalArgumentException if the number of faceUpCards don't respect the standard given in ch.epfl.tchu.game.Constants, or if the deck size or discard size are negative
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
     * @return the total of public cards (int)
     */
    public int totalSize() {
        return totalSize;
    }

    /**
     * Gives the faces up cards
     * @return a list of the faced up cards (List)
     */
    public List<Card> faceUpCards() {
        return List.copyOf(faceUpCards);
    }

    /**
     * Gives the card of the specified index of the list of faced up cards
     * @param slot the index of the card in the list
     * @return the card corresponding to the index (Card)
     * @throws IndexOutOfBoundsException if the wanted slot isn't positive or if he's bigger than the number of faced up cards
     */
    public Card faceUpCard(int slot) {
        //Check correctness of index and return the corresponding card of the index
        return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
    }

    /**
     * Getter for the deck size
     * @return the size of the deck (int)
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Gives the empty state of the deck
     * @return true iff the deck is empty (boolean)
     */
    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

    /**
     * Getter for the size of the discards
     * @return the size of the discards (int)
     */
    public int discardsSize() {
        return discardsSize;
    }
}
