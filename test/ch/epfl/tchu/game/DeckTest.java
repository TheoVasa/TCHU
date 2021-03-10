package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public final class DeckTest {

    /**
     * Attributes used multiple times
     */
    public Random random = new Random(0L);
    public SortedBag.Builder<Card> emptyCardsBuilder = new SortedBag.Builder<>();
    //Create non empty SortedBag.Builder
    public SortedBag.Builder<Card> nonEmptyCardsBuilder = new SortedBag.Builder<>();
    {
        nonEmptyCardsBuilder.add(Card.BLACK);
        nonEmptyCardsBuilder.add(Card.BLUE);
        nonEmptyCardsBuilder.add(Card.ORANGE);
        nonEmptyCardsBuilder.add(3 ,Card.YELLOW);
    }

    //No need to test the constructor on error
    //No need to test the methode "of" because will be tested troth the other methods

    @Test
    public void isEmptyWorksOnEmptyDeck(){
        assertTrue(Deck.of(emptyCardsBuilder.build(), random).isEmpty());
    }

    @Test
    public void isEmptyWorksOnNonEmptyDeck(){
        assertFalse(Deck.of(nonEmptyCardsBuilder.build(), random).isEmpty());
    }

    @Test
    public  void topCardThrowsThowsIllegalArgumentOnEmptyDeck(){
        var emptyDeck = Deck.of(emptyCardsBuilder.build(), random);
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.topCard();
        });
    }

    @Test
    public void topCardWorkOnNonEmptyDeck(){
        assertEquals(Card.BLACK, Deck.of(nonEmptyCardsBuilder.build(), random).topCard());
    }

    //Test en meme temps la methode withoutTopCard()
    @Test
    public void withoutTopCardsThrowsIllegalArgumentOnEmptyDeck(){
        var emptyDeck = Deck.of(emptyCardsBuilder.build(), random);
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.withoutTopCard();
        });
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.withoutTopCards(0);
        });
    }

    @Test
    public void withoutTopCardsThrowsIllegalArgumentOnCountOutOfRange(){
        var nonEmptyDeck = Deck.of(nonEmptyCardsBuilder.build(), random);
        assertThrows(IllegalArgumentException.class, () -> {
            nonEmptyDeck.withoutTopCards(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            nonEmptyDeck.withoutTopCards(nonEmptyCardsBuilder.build().size()+1);
        });
    }

    @Test
    public void withoutTopCardsWorksOnNonEmptyDeck(){
        var expectedBag1 = new SortedBag.Builder<Card>();
        var expectedBag2 = new SortedBag.Builder<Card>();

        expectedBag1.add(Card.BLUE);
        expectedBag1.add(Card.ORANGE);
        expectedBag1.add(3 ,Card.YELLOW);
        expectedBag2.add(Card.ORANGE);
        expectedBag2.add(3 ,Card.YELLOW);

        var nonEmptyDeck = Deck.of(nonEmptyCardsBuilder.build(), random);
        var expectedDeck1 = Deck.of(expectedBag1.build(), random);
        var expectedDeck2 = Deck.of(expectedBag2.build(), random);

        for (int i = 0; i < expectedBag1.build().size(); ++i ){
            assertEquals(expectedDeck1.topCards(expectedDeck1.size()).get(i),
                    nonEmptyDeck.withoutTopCard().topCards(expectedDeck1.size()).get(i));
        }
        for (int i = 0; i < expectedBag2.build().size(); ++i ){
            assertEquals(expectedDeck2.topCards(expectedDeck2.size()).get(i),
                    nonEmptyDeck.withoutTopCards(2).topCards(expectedDeck2.size()).get(i));
        }
    }

    @Test
    public void sizeWorks(){
        var emptyDeck = Deck.of(emptyCardsBuilder.build(), random);
        var nonEmptyDeck = Deck.of(nonEmptyCardsBuilder.build(), random);
        assertEquals(0, emptyDeck.size());
        assertEquals(6, nonEmptyDeck.size());
        assertEquals(5, nonEmptyDeck.withoutTopCard().size());
    }
}