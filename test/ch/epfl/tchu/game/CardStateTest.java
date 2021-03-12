package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import com.sun.tools.jconsole.JConsoleContext;
import org.junit.jupiter.api.Test;

import java.nio.channels.ShutdownChannelGroupException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CardStateTest {

    /**
     * Attriutes
     */
    //Lists
    private SortedBag.Builder bagBuilder= new SortedBag.Builder();
    {
        bagBuilder.add(1,Card.BLACK);
        bagBuilder.add(1,Card.BLUE);
        bagBuilder.add(1,Card.ORANGE);
        bagBuilder.add(3,Card.YELLOW);
        bagBuilder.add(2,Card.LOCOMOTIVE);
    }
    private SortedBag cards = bagBuilder.build();
    private List<Card> shuffledCards = cards.toList();
    {
        Collections.shuffle(shuffledCards, TestRandomizer.newRandom());
    }


//of(Deck<Card> deck)
    @Test
    public void ofThrowsIllegalArgumentOnSmallDeck(){
        var cards = SortedBag.of(1, Card.BLUE,3, Card.YELLOW);
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(deck);
        });
    }

    @Test
    public  void ofWorksFacedUpCards(){
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var faceUpCards = SortedBag.of(CardState.of(deck).faceUpCards());
        var expectedFacedUpCardsBuilder = new SortedBag.Builder<Card>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i)
            expectedFacedUpCardsBuilder.add(shuffledCards.get(i));
        var expectedFacedUpCards = expectedFacedUpCardsBuilder.build();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i)
            assertTrue(expectedFacedUpCards.contains(faceUpCards) && faceUpCards.contains(expectedFacedUpCards));
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
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var cardState = CardState.of(deck);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            cardState.withDrawnFaceUpCard(Constants.FACE_UP_CARDS_COUNT);
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
        var deck = Deck.of(cards, TestRandomizer.newRandom());
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
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var cardState = CardState.of(deck);
        var expectedCard = shuffledCards.get(Constants.FACE_UP_CARDS_COUNT);
        assertEquals(expectedCard, cardState.withDrawnFaceUpCard(2).faceUpCard(2));
        assertEquals(shuffledCards.size() -(Constants.FACE_UP_CARDS_COUNT + 1), cardState.withDrawnFaceUpCard(2).deckSize());
    }

//topDeckCard()
    @Test
    public void topDeckCardThrowsIllegalArgument(){
        var cards = SortedBag.of(3,Card.BLUE, 2, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var cardState = CardState.of(deck);
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.topDeckCard();
        });
    }

    @Test
    public void topDeckCardWorks(){
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var cardState = CardState.of(deck);
        var expectedCard = shuffledCards.get(Constants.FACE_UP_CARDS_COUNT);
        assertEquals(expectedCard, cardState.topDeckCard());
        assertEquals(shuffledCards.size() - (Constants.FACE_UP_CARDS_COUNT), cardState.deckSize());
    }

//withoutTopDeckCard()
    @Test
    public void withoutTopDeckCardThrowsIllegalArgument(){
        var cards = SortedBag.of(3,Card.BLUE, 2, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var cardState = CardState.of(deck);
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withoutTopDeckCard();
        });
    }

    @Test
    public void withoutTopDeckCardWorks(){
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        for (int i = Constants.FACE_UP_CARDS_COUNT+1; i < shuffledCards.size()-1; ++i) {
            var cardState = CardState.of(deck).withoutTopDeckCard();
            assertEquals(shuffledCards.get(i), cardState.topDeckCard());
            assertEquals(shuffledCards.size() - (Constants.FACE_UP_CARDS_COUNT+1), cardState.deckSize());
        }
    }

//withRecreatedFromDiscards(Random rng)
    @Test
    public void withDeckRecreatedFromDiscardsThrowsIllegalArgument(){
        var cards = SortedBag.of(6,Card.BLUE, 1, Card.LOCOMOTIVE);
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var cardState = CardState.of(deck).withoutTopDeckCard();
        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withDeckRecreatedFromDiscards(TestRandomizer.newRandom());
        });
    }

    @Test
    public void withDeckRecreatedFromDiscardsWorks(){
        var cards = SortedBag.of(5, Card.BLUE);
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var discard = List.of(Card.BLUE,Card.BLUE, Card.LOCOMOTIVE, Card.LOCOMOTIVE, Card.LOCOMOTIVE);
        var cardState = CardState.of(deck).withMoreDiscardedCards(SortedBag.of(discard));

        cardState = cardState.withDeckRecreatedFromDiscards(TestRandomizer.newRandom());

        assertEquals(discard.size(), cardState.deckSize());

        //Checker si les elements sont bien ajoutées
        for (int i = 0; i < discard.size(); ++i) {
            assertTrue(discard.contains(cardState.topDeckCard()));
            if (cardState.deckSize()>0)
                cardState = cardState.withoutTopDeckCard();
        }
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
    public void withMoreDiscardedCardsWorksOnEmptyAdditionalCards(){
        var cards = SortedBag.of(5,Card.BLUE);
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var cardState = CardState.of(deck).withMoreDiscardedCards(SortedBag.of());
        assertEquals(0, cardState.discardsSize());
        assertEquals(0, cardState.deckSize());
    }
}