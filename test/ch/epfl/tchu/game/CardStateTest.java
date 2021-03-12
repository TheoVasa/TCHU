package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTest {

//of(Deck<Card>)
    @Test
    public void ofThrowsIllegalArgumentOnSmallDeck(){
        var cards = SortedBag.of(1, Card.BLUE,3, Card.YELLOW);
        var deck = Deck.of(cards, new Random(0L));
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(deck);
        });
    }

    @Test
    public  void ofWorksFacedUpCards(){
        var cards = SortedBag.of(3, Card.BLUE,3, Card.YELLOW);
        var deck = Deck.of(cards, new Random(0L));
        assertEquals(CardState.of(deck).faceUpCards(), List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.YELLOW, Card.YELLOW));
    }

//withDrawnFaceUpCard(int slot)
    @Test
    public void withDrawnFaceUpCardThrowsIndexOutOfBound(){
        var cardsBuilder = new SortedBag.Builder<Card>();
        cardsBuilder.add(2, Card.YELLOW);
        cardsBuilder.add(Card.LOCOMOTIVE);
        cardsBuilder.add(Card.ORANGE);
        cardsBuilder.add(3,Card.BLACK);

        var cards = cardsBuilder.build();
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            cardState.withDrawnFaceUpCard(5);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            cardState.withDrawnFaceUpCard(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            cardState.withDrawnFaceUpCard(500);
        });
    }

    @Test
    public void withDrawnFacedUpCardThrowsIllegalArgument(){
        var cards = SortedBag.of(5,Card.BLUE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck);
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withDrawnFaceUpCard(2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withDrawnFaceUpCard(1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withDrawnFaceUpCard(0);
        });
    }

    @Test
    public void withDrawnFacedUpCardWorks(){
        var cards = SortedBag.of(5,Card.BLUE, 2, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck);
        assertEquals(Card.LOCOMOTIVE,cardState.withDrawnFaceUpCard(2).faceUpCard(2));
        assertEquals(1, cardState.withDrawnFaceUpCard(2).deckSize());
    }

//topDeckCard()
    @Test
    public void topDeckCardThrowsIllegalArgument(){
        var cards = SortedBag.of(3,Card.BLUE, 2, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck);
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.topDeckCard();
        });
    }

    @Test
    public void topDeckCardWorks(){
        var cards = SortedBag.of(4,Card.BLUE, 3, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck);
        assertEquals(Card.LOCOMOTIVE, cardState.topDeckCard());
    }

//withoutTopDeckCard()
    @Test
    public void withoutTopDeckCardThrowsIllegalArgument(){
        var cards = SortedBag.of(3,Card.BLUE, 2, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck);
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withoutTopDeckCard();
        });
    }

    @Test
    public void withoutTopDeckCardWorks(){
        var cards = SortedBag.of(6,Card.BLUE, 1, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck).withoutTopDeckCard();
        assertEquals(Card.LOCOMOTIVE, cardState.topDeckCard());
    }

//withRecreatedFromDiscards(Random rng)
    @Test
    public void withDeckRecreatedFromDiscardsThrowsIllegalArgument(){
        var cards = SortedBag.of(6,Card.BLUE, 1, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck).withoutTopDeckCard();
        assertThrows(IllegalArgumentException.class, () -> {
           cardState.withDeckRecreatedFromDiscards(new Random(0L));
        });
    }

    @Test
    public void withDeckRecreatedFromDiscardsWorks(){
        var cards = SortedBag.of(5,Card.BLUE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck).withMoreDiscardedCards(SortedBag.of(2, Card.BLUE, 3, Card.LOCOMOTIVE));
        cardState = cardState.withDeckRecreatedFromDiscards(new Random(0L));
        assertEquals(5, cardState.deckSize());
        assertEquals(0, cardState.discardsSize());
    }

//withMoreDiscardedCards(SortedBag<Cards> additionalDiscards)
    @Test
    public void withMoreDiscardedCardsWorksOnNonEmptyAdditionalCards(){
        var cards = SortedBag.of(5,Card.BLUE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck).withMoreDiscardedCards(SortedBag.of(2, Card.BLUE, 3, Card.LOCOMOTIVE));
        assertEquals(5, cardState.discardsSize());
        cardState = cardState.withMoreDiscardedCards(SortedBag.of(2, Card.BLUE, 3, Card.LOCOMOTIVE));
        assertEquals(10, cardState.discardsSize());
    }

    @Test
    public void withMoreDiscardedCardsWorksOnEmptyAddictionalCards(){
        var cards = SortedBag.of(5,Card.BLUE);
        var deck = Deck.of(cards, new Random(0L));
        var cardState = CardState.of(deck).withMoreDiscardedCards(SortedBag.of());
        assertEquals(0, cardState.discardsSize());
    }
}
