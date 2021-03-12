package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public final class DeckTest {

    /**
     * Attributes used multiple times
     */
    private SortedBag.Builder<Card> emptyCardsBuilder = new SortedBag.Builder<>();
    //Create non empty SortedBag.Builder
    private SortedBag.Builder<Card> nonEmptyCardsBuilder = new SortedBag.Builder<>();
    {
        nonEmptyCardsBuilder.add(Card.BLACK);
        nonEmptyCardsBuilder.add(Card.BLUE);
        nonEmptyCardsBuilder.add(Card.ORANGE);
        nonEmptyCardsBuilder.add(3 ,Card.YELLOW);
    }
    private Deck<Card> deck = Deck.of(nonEmptyCardsBuilder.build(), TestRandomizer.newRandom());
    Deck<Card> emptyDeck = Deck.of(emptyCardsBuilder.build(), TestRandomizer.newRandom());

    //No need to test the constructor on error
    //No need to test the methode "of" because will be tested through the other methods


//isEmpty()
    @Test
    public void isEmptyWorksOnEmptyDeck(){
        assertTrue(Deck.of(emptyCardsBuilder.build(), TestRandomizer.newRandom()).isEmpty());
    }

    @Test
    public void isEmptyWorksOnNonEmptyDeck(){
        assertFalse(Deck.of(nonEmptyCardsBuilder.build(), TestRandomizer.newRandom()).isEmpty());
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
        var expectedShuffled = nonEmptyCardsBuilder.build();
        Collections.shuffle(expectedShuffled.toList(), TestRandomizer.newRandom());
        assertEquals(expectedShuffled.get(0), deck.topCard());
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
        Collections.shuffle(cardsBuilder.build().toList(), TestRandomizer.newRandom());
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
        var expectedShuffled = nonEmptyCardsBuilder.build().toList();
        Collections.shuffle(expectedShuffled, TestRandomizer.newRandom());
        assertArrayEquals(List.of(expectedShuffled.get(0), expectedShuffled.get(1)).toArray(), deck.topCards(2).toList().toArray());
    }

//withoutTopCards(int count)
    @Test
    public void withoutTopCardsThrowsIllegalArgumentOnEmptyDeck(){
        //Be careful to test first IllegalArgument before IndexOUtOfBound (with Objects.checkIndex(...)) !!!!
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeck.withoutTopCards(-1);
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
        var expectedShuffle = nonEmptyCardsBuilder.build().toList();
        Collections.shuffle(expectedShuffle, TestRandomizer.newRandom());

        //!!!!Could have used assertArrayEquals
        for (int i = 0; i < expectedShuffle.size()-2; ++i ){
            assertEquals(expectedShuffle.get(i+2),
                    deck.withoutTopCards(2).topCards(4).toList().get(i));
        }
    }

//size()
    @Test
    public void sizeWorks(){
        var emptyDeck = Deck.of(emptyCardsBuilder.build(), TestRandomizer.newRandom());
        var nonEmptyDeck = Deck.of(nonEmptyCardsBuilder.build(), TestRandomizer.newRandom());
        assertEquals(0, emptyDeck.size());
        assertEquals(6, nonEmptyDeck.size());
        assertEquals(5, nonEmptyDeck.withoutTopCard().size());
    }
}