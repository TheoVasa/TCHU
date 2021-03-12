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
    Deck<Card> deck = Deck.of(nonEmptyCardsBuilder.build(), random);
    Deck<Card> emptyDeck = Deck.of(emptyCardsBuilder.build(), random);

    //No need to test the constructor on error
    //No need to test the methode "of" because will be tested through the other methods


//isEmpty()
    @Test
    public void isEmptyWorksOnEmptyDeck(){
        assertTrue(Deck.of(emptyCardsBuilder.build(), random).isEmpty());
    }

    @Test
    public void isEmptyWorksOnNonEmptyDeck(){
        assertFalse(Deck.of(nonEmptyCardsBuilder.build(), random).isEmpty());
    }


//topCard()
    @Test
    public  void topCardThrowsIllegalArgumentOnEmptyDeck(){
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.topCard();
        });
    }

    @Test
    public void topCardWorkOnNonEmptyDeck(){
        assertEquals(Card.BLACK, deck.topCard());
    }

//withoutTopCard()
    @Test
    public void withoutTopCardWorksOnIllegalArgument(){
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.withoutTopCard();
        });
    }

    @Test
    public void withoutTopCardWorksNonEmptyDeck(){
        var cardsBuilder = new SortedBag.Builder<Card>();
        cardsBuilder.add(Card.BLUE);
        cardsBuilder.add(Card.ORANGE);
        cardsBuilder.add(3 ,Card.YELLOW);
        assertArrayEquals(cardsBuilder.build().toList().toArray(),deck.withoutTopCard().topCards(5).toList().toArray());
    }

//topCards(int count)
    @Test
    public  void topCardsThrowsIllegalArgumentOnEmptyDeck(){
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.topCards(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.topCards(emptyDeck.size()+1);
        });
    }

    @Test
    public void topCardsWorkOnNonEmptyDeck(){
        assertArrayEquals(SortedBag.of(1,Card.BLACK,1,Card.BLUE).toList().toArray(), deck.topCards(2).toList().toArray());
    }

//withoutTopCards(int count)
    @Test
    public void withoutTopCardsThrowsIllegalArgumentOnEmptyDeck(){
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.withoutTopCards(0);
        });
    }

    @Test
    public void withoutTopCardsThrowsIllegalArgumentOnCountOutOfRange(){
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCards(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCards(nonEmptyCardsBuilder.build().size()+1);
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

        //!!!!Could have used assertArrayEquals
        for (int i = 0; i < expectedDeck1.size(); ++i ){
            assertEquals(expectedDeck1.topCards(expectedDeck1.size()).get(i),
                    nonEmptyDeck.withoutTopCard().topCards(expectedDeck1.size()).get(i));
        }
        for (int i = 0; i < expectedDeck2.size(); ++i ){
            assertEquals(expectedDeck2.topCards(expectedDeck2.size()).get(i),
                    nonEmptyDeck.withoutTopCards(2).topCards(expectedDeck2.size()).get(i));
        }
    }

//size()
    @Test
    public void sizeWorks(){
        var emptyDeck = Deck.of(emptyCardsBuilder.build(), random);
        var nonEmptyDeck = Deck.of(nonEmptyCardsBuilder.build(), random);
        assertEquals(0, emptyDeck.size());
        assertEquals(6, nonEmptyDeck.size());
        assertEquals(5, nonEmptyDeck.withoutTopCard().size());
    }
}