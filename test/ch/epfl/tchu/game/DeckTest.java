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
    //Lists
    private SortedBag.Builder bagBuilder= new SortedBag.Builder();
    {
        bagBuilder.add(1,Card.BLACK);
        bagBuilder.add(1,Card.BLUE);
        bagBuilder.add(1,Card.ORANGE);
        bagBuilder.add(3,Card.YELLOW);
    }
    private SortedBag<Card> emptyCards = SortedBag.of();
    private SortedBag<Card> nonEmptyCards = bagBuilder.build();
    private List<Card> expectedShuffled = SortedBag.of(nonEmptyCards).toList();
    {
        Collections.shuffle(expectedShuffled, TestRandomizer.newRandom());
        for (int i = 0; i< expectedShuffled.size(); ++i)
            System.out.println(expectedShuffled.get(i) + " ...");
    }

    //Mes deck
    private Deck<Card> deck = Deck.of(SortedBag.of(nonEmptyCards), TestRandomizer.newRandom());
    private Deck<Card> emptyDeck = Deck.of(SortedBag.of(emptyCards), TestRandomizer.newRandom());

    //No need to test the constructor on error
    //No need to test the methode "of" because will be tested through the other methods


//isEmpty()
    @Test
    public void isEmptyWorksOnEmptyDeck(){
        assertTrue(emptyDeck.isEmpty());
    }

    @Test
    public void isEmptyWorksOnNonEmptyDeck(){
        assertFalse(deck.isEmpty());
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
        var expectedWithoutTopCardsBuilder = new SortedBag.Builder<Card>();
        for (int count = 1; count < expectedShuffled.size(); ++count){
            expectedWithoutTopCardsBuilder.add(1, expectedShuffled.get(count));
        }
        var expectedWithoutTopCards = expectedWithoutTopCardsBuilder.build();
        assertArrayEquals(expectedWithoutTopCards.toList().toArray(),
                            deck.withoutTopCard().topCards(5).toList().toArray());
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
        for (int i = 0; i < expectedShuffled.size(); ++i){
            assertEquals(SortedBag.of(expectedShuffled).get(i), deck.topCards(6).get(i));
        }
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
            deck.withoutTopCards(nonEmptyCards.size()+1);
        });
    }

    @Test
    public void withoutTopCardsWorksOnNonEmptyDeck(){
        var expectedWithoutTopCardsBuilder = new SortedBag.Builder<Card>();
        for (int i = 4; i < expectedShuffled.size(); ++i){
            expectedWithoutTopCardsBuilder.add(1, expectedShuffled.get(i));
        }
        var expectedWithoutTopCards = expectedWithoutTopCardsBuilder.build();
        assertArrayEquals(expectedWithoutTopCards.toList().toArray(),
                            deck.withoutTopCards(4).topCards(expectedWithoutTopCards.size()).toList().toArray());
    }

//size()
    @Test
    public void sizeWorks(){
        assertEquals(0, emptyDeck.size());
        assertEquals(6, deck.size());
        assertEquals(5, deck.withoutTopCard().size());
    }
}