package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;




public class PublicCardStateTest {

    private List<Card> faceUpCards = new ArrayList<>(Arrays.asList(Card.LOCOMOTIVE, Card.YELLOW, Card.YELLOW, Card.BLUE, Card.BLACK));
    private int deckSize = 5;
    private int discardSize=10;

    @Test
    public void constructorThrowsIllegalArgumentExceptionOnTooBigFaceUpCards(){
        faceUpCards.add(Card.BLUE);

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(faceUpCards, deckSize, discardSize);
        });
    }

    @Test
    public void constructorThrowsIllegalArgumentExceptionOnNegativeDeckSize(){
        deckSize = -1;

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(faceUpCards, deckSize, discardSize);
        });
    }

    @Test
    public void constructorThrowsIllegalArgumentExceptionOnNegativeDiscardSize(){
        discardSize=-2;

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(faceUpCards, deckSize, discardSize);
        });
    }

    @Test
    public void totalSizeWorks(){

        var test =  new PublicCardState(faceUpCards, deckSize, discardSize);

        assertEquals(discardSize+deckSize+faceUpCards.size(),test.totalSize());
    }

    @Test
    public void faceUpCardsWorks(){

        var test =  new PublicCardState(faceUpCards, deckSize, discardSize);

        assertEquals(faceUpCards,test.faceUpCards());
    }

    @Test
    public void faceUpCardThrowsIndexOUtOfBound(){

        var test =  new PublicCardState(faceUpCards, deckSize, discardSize);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            test.faceUpCard(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            test.faceUpCard(5);
        });
    }

    @Test
    public void faceUpCardWorks(){

        var test =  new PublicCardState(faceUpCards, deckSize, discardSize);

        assertEquals(Card.YELLOW, test.faceUpCard(2));
        assertEquals(Card.LOCOMOTIVE, test.faceUpCard(0));

    }

    @Test
    public void deckSizeWorks(){
        deckSize= 50;

        var test =  new PublicCardState(faceUpCards, deckSize, discardSize);

      assertEquals(deckSize, test.deckSize());
    }


    @Test
    public void isDeckEmptyWorksOnEmptyDeck(){
        deckSize= 0;

        var test =  new PublicCardState(faceUpCards, deckSize, discardSize);

        assertTrue(test.isDeckEmpty());
    }

    @Test
    public void isDeckEmptyWorksOnNonEmptyDeck(){

        var test =  new PublicCardState(faceUpCards, deckSize, discardSize);

        assertFalse(test.isDeckEmpty());
    }

    @Test
    public void discardSizeWorks(){

        var test =  new PublicCardState(faceUpCards, deckSize, discardSize);

        assertEquals(discardSize, test.discardsSize());
    }
}
