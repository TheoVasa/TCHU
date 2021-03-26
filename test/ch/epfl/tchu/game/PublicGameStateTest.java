package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PublicGameStateTest {

//Constructor(...)
    @Test
    void constructorThrowIllegalArgumentOnNegativeDeckSize(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(5, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));

        assertThrows(IllegalArgumentException.class, () ->{
            new PublicGameState(-1, cardState, PlayerId.PLAYER_1, playerState, null);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            new PublicGameState(-10000, cardState, PlayerId.PLAYER_1, playerState, null);
        });
    }

    @Test
    void constructorThrowsIllegalPointerOnPlayerStateSizeOutOfRange(){ // Should contain exactly 2 elements
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(5, Card.YELLOW), TestRandomizer.newRandom()));

        //With 0 elements
        assertThrows(IllegalArgumentException.class, () ->{
            new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);
        });

        //With 1 element
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        assertThrows(IllegalArgumentException.class, () ->{
            new PublicGameState(5, cardState, PlayerId.PLAYER_1, playerState, null);
        });
    }


    @Test
    void constructorThrowsNullPointerOnNullCardState(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));

        assertThrows(NullPointerException.class, () ->{
            new PublicGameState(0, null, PlayerId.PLAYER_1, playerState, null);
        });
    }

    @Test
    void constructorThrowsNullPointerOnNullCurrentPlayerID(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(5, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));

        assertThrows(NullPointerException.class, () ->{
            new PublicGameState(0, cardState, null, playerState, null);
        });
    }


    @Test
    void constructorWorksOnCopy(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var expected = playerState;

        var publicGameState = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);
        playerState.clear();
        assertEquals(expected.get(PlayerId.PLAYER_1), publicGameState.playerState(PlayerId.PLAYER_1));
    }

//ticketCount()
    @Test
    void ticketCountWorks(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(5, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState1 = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);
        var publicGameState2 = new PublicGameState(10, cardState, PlayerId.PLAYER_1, playerState, null);
        var publicGameState3 = new PublicGameState(500, cardState, PlayerId.PLAYER_1, playerState, null);

        assertEquals(0, publicGameState1.ticketsCount());
        assertEquals(10, publicGameState2.ticketsCount());
        assertEquals(500, publicGameState3.ticketsCount());
    }

//canDrawTickets()
    @Test
    void canDrawTicketsWorks(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(5, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));

        var publicGameState1 = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);
        var publicGameState2 = new PublicGameState(10, cardState, PlayerId.PLAYER_1, playerState, null);
        var publicGameState3 = new PublicGameState(500, cardState, PlayerId.PLAYER_1, playerState, null);

        assertFalse(publicGameState1.canDrawTickets());
        assertTrue(publicGameState2.canDrawTickets());
        assertTrue(publicGameState3.canDrawTickets());
    }

//cardState()
    @Test
    void cardStateWorks(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(5, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);
        assertEquals(cardState, publicGameState.cardState());
    }

//canDrawCards()
    @Test
    void canDrawCardsWorks(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState1 = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        var cardState2 = cardState1.withoutTopDeckCard();
        var cardState3 = cardState2.withMoreDiscardedCards(SortedBag.of(1, Card.LOCOMOTIVE));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState1 = new PublicGameState(0, cardState1, PlayerId.PLAYER_1, playerState, null);
        var publicGameState2 = new PublicGameState(0, cardState2, PlayerId.PLAYER_1, playerState, null);
        var publicGameState3 = new PublicGameState(0, cardState3, PlayerId.PLAYER_1, playerState, null);

        //5 cards in the deck, 0 in discards --> true
        assertTrue(publicGameState1.canDrawCards());
        //4 cards in the deck, 0 in discards --> false
        assertFalse(publicGameState2.canDrawCards());
        //4 cards in the deck, 1 in discards --> true
        assertTrue(publicGameState3.canDrawCards());
    }

//currentPlayerId()
    @Test
    void currentPlayerIdWorks() {
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState1 = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState1 = new PublicGameState(0, cardState1, PlayerId.PLAYER_1, playerState, null);
        var publicGameState2 = new PublicGameState(0, cardState1, PlayerId.PLAYER_2, playerState, null);

        assertEquals(PlayerId.PLAYER_1, publicGameState1.currentPlayerId());
        assertEquals(PlayerId.PLAYER_2, publicGameState2.currentPlayerId());
    }

//playerState(...)
    @Test
    void playerStateWorks(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);

        assertEquals(playerState.get(PlayerId.PLAYER_1), publicGameState.playerState(PlayerId.PLAYER_1));
        assertEquals(playerState.get(PlayerId.PLAYER_2), publicGameState.playerState(PlayerId.PLAYER_2));
    }

//currentPlayerState()
    @Test
    void currentPlayerStateWorks(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState1 = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);
        var publicGameState2 = new PublicGameState(0, cardState, PlayerId.PLAYER_2, playerState, null);

        assertEquals(playerState.get(PlayerId.PLAYER_1), publicGameState1.currentPlayerState());
        assertEquals(playerState.get(PlayerId.PLAYER_2), publicGameState2.currentPlayerState());
    }

//claimedRoutes()
    @Test
    void claimedRoutesWorksOnSimpleExample(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1,
                PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE))
                .withClaimedRoute(ChMap.routes().get(0), SortedBag.of()));
        playerState.put(PlayerId.PLAYER_2,
                PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE))
                .withClaimedRoute(ChMap.routes().get(1), SortedBag.of()));
        var publicGameState = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);

        var expected = List.of(ChMap.routes().get(0), ChMap.routes().get(1));
        assertTrue(expected.containsAll(publicGameState.claimedRoutes()) &&
                    publicGameState.claimedRoutes().containsAll(expected));
    }

    @Test
    void claimedRoutesWorksOnNoRoutesClaimed(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);

        assertEquals(List.of(), publicGameState.claimedRoutes());
    }

    @Test
    void claimedRoutesWorksOnAllRoutes(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        var playerState1 = PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE));
        for (int i = 0; i < ChMap.routes().size(); ++i)
            playerState1 = playerState1.withClaimedRoute(ChMap.routes().get(i), SortedBag.of());
        var playerState2 = PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE));
        playerState.put(PlayerId.PLAYER_1, playerState1);
        playerState.put(PlayerId.PLAYER_2, playerState2);
        var publicGameState = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);


        var expected = new ArrayList<>(ChMap.routes());
        assertTrue(expected.containsAll(publicGameState.claimedRoutes()) &&
                    publicGameState.claimedRoutes().containsAll(expected));
    }

    @Test
    void claimedRoutesWorksOnCopy(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        var playerState1 = PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE));
        for (int i = 0; i < 3; ++i)// Take 3 routes
            playerState1 = playerState1.withClaimedRoute(ChMap.routes().get(i), SortedBag.of());
        var playerState2 = PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE));
        playerState.put(PlayerId.PLAYER_1, playerState1);
        playerState.put(PlayerId.PLAYER_2, playerState2);
        var publicGameState = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, null);

        //Clear to see if .claimedRoutes() return a copy and not the list itself
        var expected = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2));
        publicGameState.claimedRoutes().clear();
        assertTrue(expected.containsAll(publicGameState.claimedRoutes()) &&
                    publicGameState.claimedRoutes().containsAll(expected));
    }

//lastPlayer()
    @Test
    void lastPlayerWorks(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState1 = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);
        var publicGameState2 = new PublicGameState(0, cardState, PlayerId.PLAYER_2, playerState, PlayerId.PLAYER_1);
        var publicGameState3 = new PublicGameState(0, cardState, PlayerId.PLAYER_2, playerState, null);

        assertEquals(PlayerId.PLAYER_2, publicGameState1.lastPlayer());
        assertEquals(PlayerId.PLAYER_1, publicGameState2.lastPlayer());
        assertNull(publicGameState3.lastPlayer());
    }

    @Test
    void lastPlayerOnCopy(){
        //Required vars
        var playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        var cardState = CardState.of(Deck.of(SortedBag.of(10, Card.YELLOW), TestRandomizer.newRandom()));
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(4, Card.LOCOMOTIVE)));
        var publicGameState1 = new PublicGameState(0, cardState, PlayerId.PLAYER_1, playerState, PlayerId.PLAYER_2);
        var publicGameState2 = new PublicGameState(0, cardState, PlayerId.PLAYER_2, playerState, PlayerId.PLAYER_1);
        var publicGameState3 = new PublicGameState(0, cardState, PlayerId.PLAYER_2, playerState, null);

        publicGameState1.lastPlayer().next();

        assertEquals(PlayerId.PLAYER_2, publicGameState1.lastPlayer());
    }
}
