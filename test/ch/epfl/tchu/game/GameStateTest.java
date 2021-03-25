package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

//initial(...)
    @Test
    void initialWorksOnCopy(){
        var tickets = SortedBag.of(2, ChMap.tickets().get(0), 2, ChMap.tickets().get(1));
        var gameState = GameState.initial(tickets, TestRandomizer.newRandom());

        var copyBuilder = new SortedBag.Builder<Ticket>();
        copyBuilder.add(tickets);

        //Try to clear the ticket
        try {
            tickets.toList().clear();
        }catch (Exception e){
            // do nothing
        }

        assertEquals(copyBuilder.build() ,gameState.topTickets(gameState.ticketsCount()));
    }

//playerState(...)
    @Test
    void playerStateWorks(){

        //Init cards
        var deck = Deck.of(Constants.ALL_CARDS, TestRandomizer.newRandom());
        var cardsPlayer1 = deck.topCards(Constants.INITIAL_CARDS_COUNT);
        deck = deck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        var cardsPlayer2 = deck.topCards(Constants.INITIAL_CARDS_COUNT);
        deck = deck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        var cardState = CardState.of(deck);

        //Init playerState
        var playerState1 = PlayerState.initial(cardsPlayer1);
        var playerState2 = PlayerState.initial(cardsPlayer2);

        //Init gameState
        var gameState = GameState.initial(SortedBag.of(ChMap.tickets()), TestRandomizer.newRandom());

        //Test playerState1
        assertEquals(SortedBag.of(playerState1.tickets()).toList(), gameState.playerState(PlayerId.PLAYER_1).tickets().toList());
        assertEquals(playerState1.routes(), gameState.playerState(PlayerId.PLAYER_1).routes());
        assertEquals(playerState1.ticketPoints(), gameState.playerState(PlayerId.PLAYER_1).ticketPoints());
        assertEquals(playerState1.claimPoints(), gameState.playerState(PlayerId.PLAYER_1).claimPoints());
        assertEquals(playerState1.cards(), gameState.playerState(PlayerId.PLAYER_1).cards());

        //Test playerState2
        assertEquals(playerState2.cards(), gameState.playerState(PlayerId.PLAYER_2).cards());
        assertEquals(playerState2.tickets(), gameState.playerState(PlayerId.PLAYER_2).tickets());
        assertEquals(playerState2.routes(), gameState.playerState(PlayerId.PLAYER_2).routes());
        assertEquals(playerState2.ticketPoints(), gameState.playerState(PlayerId.PLAYER_2).ticketPoints());
        assertEquals(playerState2.claimPoints(), gameState.playerState(PlayerId.PLAYER_2).claimPoints());
    }


//currentPlayerState()
    @Test
    void currentPlayerStateWorks(){
        //Init cards
        var deck = Deck.of(Constants.ALL_CARDS, TestRandomizer.newRandom());
        var cardsPlayer1 = deck.topCards(Constants.INITIAL_CARDS_COUNT);
        deck = deck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        var cardsPlayer2 = deck.topCards(Constants.INITIAL_CARDS_COUNT);
        deck = deck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        var cardState = CardState.of(deck);

        //Init playerState
        var playerState1 = PlayerState.initial(cardsPlayer1);
        var playerState2 = PlayerState.initial(cardsPlayer2);

        //Init gameState
        var gameState = GameState.initial(SortedBag.of(ChMap.tickets()), TestRandomizer.newRandom());
        var currentPlayer = PlayerId.ALL.get(TestRandomizer.newRandom().nextInt(PlayerId.COUNT));

        if (currentPlayer == PlayerId.PLAYER_1) {
            //Test playerState1
            assertEquals(playerState1.cards(), gameState.playerState(PlayerId.PLAYER_1).cards());
            assertEquals(playerState1.tickets(), gameState.playerState(PlayerId.PLAYER_1).tickets());
            assertEquals(playerState1.routes(), gameState.playerState(PlayerId.PLAYER_1).routes());
            assertEquals(playerState1.ticketPoints(), gameState.playerState(PlayerId.PLAYER_1).ticketPoints());
            assertEquals(playerState1.claimPoints(), gameState.playerState(PlayerId.PLAYER_1).claimPoints());
        } else if (currentPlayer == PlayerId.PLAYER_2) {
            //Test playerState2
            assertEquals(playerState2.cards(), gameState.playerState(PlayerId.PLAYER_2).cards());
            assertEquals(playerState2.tickets(), gameState.playerState(PlayerId.PLAYER_2).tickets());
            assertEquals(playerState2.routes(), gameState.playerState(PlayerId.PLAYER_2).routes());
            assertEquals(playerState2.ticketPoints(), gameState.playerState(PlayerId.PLAYER_2).ticketPoints());
            assertEquals(playerState2.claimPoints(), gameState.playerState(PlayerId.PLAYER_2).claimPoints());
        }

    }


//topTickets(...)
    @Test
    void topTicketsThrowsIllegalArgument(){
        var gameState = GameState.initial(SortedBag.of(ChMap.tickets()), TestRandomizer.newRandom());
        assertThrows(IllegalArgumentException.class, () ->{
           gameState.topTickets(ChMap.tickets().size()+1);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            gameState.topTickets(-1);
        });
    }

    @Test
    void topTicketsWorks(){
        var gameState = GameState.initial(SortedBag.of(ChMap.tickets()), TestRandomizer.newRandom());
        var tickets = SortedBag.of(ChMap.tickets());
        var expected = tickets.toList();
        Collections.shuffle(expected, TestRandomizer.newRandom());

        assertEquals(SortedBag.of(expected), gameState.topTickets(expected.size()));
        assertEquals(SortedBag.of(1, expected.get(0)), gameState.topTickets(1));
    }

//withMoreDiscardedCards(...)
    @Test
    void withMoreDiscardedCardsWorks(){

        GameState initialGameState = GameState.initial(SortedBag.of(1, TestMap.LAU_STG), TestRandomizer.newRandom());
        GameState testedGameState1 = initialGameState.withMoreDiscardedCards(SortedBag.of(5, Card.LOCOMOTIVE,
                11, Card.BLUE));

        assertTrue(!initialGameState.equals(testedGameState1));

        GameState testedGameState2 = initialGameState.withMoreDiscardedCards(SortedBag.of());

        assertTrue(!initialGameState.equals(testedGameState2));
    }

//withCardsDeckRecreatedIfNeeded()
    @Test
    void withCardsDeckRecreatedIfNeededWorksWhenDeckIsNotEmpty(){

        GameState initialGameState = GameState.initial(SortedBag.of(1, TestMap.LAU_STG), TestRandomizer.newRandom());

        assertEquals(initialGameState, initialGameState.withCardsDeckRecreatedIfNeeded(TestRandomizer.newRandom()));

    }

    @Test
    void withCardsDeckRecreatedIfNeededWorksWhenDeckIsEmpty(){

        GameState initialGameState = GameState.initial(SortedBag.of(1, TestMap.LAU_STG), TestRandomizer.newRandom());

        while(!initialGameState.cardState().isDeckEmpty()){
            initialGameState = initialGameState.withoutTopCard();
        }

        initialGameState = initialGameState.withMoreDiscardedCards(SortedBag.of(11, Card.LOCOMOTIVE));
        initialGameState = initialGameState.withCardsDeckRecreatedIfNeeded(TestRandomizer.newRandom());

        assertEquals(11, initialGameState.cardState().deckSize());
    }

//withInitiallyChosenTickets(...)
    @Test
    void withInitiallyChosenTicketsWorksWithInitialGameState(){
        GameState initialGameState = GameState.initial(SortedBag.of(1, TestMap.LAU_STG), TestRandomizer.newRandom());
        GameState withTicketGameState = initialGameState.withInitiallyChosenTickets(PlayerId.PLAYER_1,
                SortedBag.of(TestMap.LAU_BER));
        GameState withTicketGameState2 = initialGameState.withInitiallyChosenTickets(PlayerId.PLAYER_1,
                SortedBag.of());

        assertFalse(initialGameState.equals(withTicketGameState));
        assertEquals(initialGameState.playerState(PlayerId.PLAYER_1).tickets(),
                withTicketGameState2.playerState(PlayerId.PLAYER_1).tickets());
    }

    @Test
    void withInitiallyChosenTicketsWorksWithNonInitialGameState(){
        GameState initialGameState = GameState.initial(SortedBag.of(1, TestMap.LAU_STG), TestRandomizer.newRandom());
        GameState withTicketGameState = initialGameState.withInitiallyChosenTickets(PlayerId.PLAYER_1,
                SortedBag.of(TestMap.LAU_BER));

        assertThrows(IllegalArgumentException.class,
                () -> withTicketGameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of()));
        assertThrows(IllegalArgumentException.class,
                () -> withTicketGameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(TestMap.LAU_BER)));

    }

//withChosenAdditionalTickets(...)
    @Test
    void withChosenAdditionalTicketsThrowsCorrectly(){

        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());
        GameState withTicketGameState = initialGameState.withInitiallyChosenTickets(initialGameState.currentPlayerId(),
                SortedBag.of(TestMap.LAU_STG));

        assertThrows(IllegalArgumentException.class, ()-> withTicketGameState.withChosenAdditionalTickets
                (SortedBag.of(1, TestMap.BER_STG, 1, TestMap.DE1_IT2), SortedBag.of(TestMap.LAU_STG)));
    }

    @Test
    void withChosenAdditionalTicketsWorks(){

        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());
        GameState withTicketGameState = initialGameState.withInitiallyChosenTickets(initialGameState.currentPlayerId(),
                SortedBag.of(TestMap.LAU_STG));

        withTicketGameState = withTicketGameState.withChosenAdditionalTickets
                (SortedBag.of(1, TestMap.BER_STG, 1, TestMap.DE1_IT2), SortedBag.of(TestMap.DE1_IT2));


        assertFalse(initialGameState.equals(withTicketGameState));
        assertNotEquals(initialGameState.currentPlayerState(),
                withTicketGameState.currentPlayerState());
        assertEquals(2, withTicketGameState.currentPlayerState().tickets().size());
        assertEquals(1, withTicketGameState.ticketsCount());

    }

//withDrawnFaceUpCard(...)
    @Test
    void withDrawnFaceUpCardWorks(){

        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());

        GameState changedGameState = initialGameState.withDrawnFaceUpCard(3);

        assertEquals(initialGameState.cardState().deckSize()-1, changedGameState.cardState().deckSize());
        assertNotEquals(initialGameState.currentPlayerState().cards(), changedGameState.currentPlayerState().cards());
        assertEquals(initialGameState.currentPlayerState().cards().size() + 1,
                changedGameState.currentPlayerState().cards().size());

    }

    @Test
    void withDrawnFaceUpCardThrowsIllegalArgumentExceptionWhenNeeded(){

        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());

        while(initialGameState.cardState().deckSize() > 3){
            initialGameState = initialGameState.withoutTopCard();
        }

        GameState finalGameState = initialGameState;

        assertThrows(IllegalArgumentException.class, ()-> finalGameState.withDrawnFaceUpCard(3));

    }

//withBlindlyDrawnCard()
    @Test
    void withBlindlyDrawnCardWorks(){

        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());

        GameState changedGameState = initialGameState.withBlindlyDrawnCard();

        assertEquals(initialGameState.cardState().deckSize()-1, changedGameState.cardState().deckSize());
        assertNotEquals(initialGameState.currentPlayerState().cards(), changedGameState.currentPlayerState().cards());
        assertEquals(initialGameState.currentPlayerState().cards().size() + 1,
                changedGameState.currentPlayerState().cards().size());

    }

    @Test
    void withBlindlyDrawnCardsThrowsIllegalArgumentExceptionWhenNeeded(){

        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());

        while(initialGameState.cardState().deckSize() > 3){
            initialGameState = initialGameState.withoutTopCard();
        }

        GameState finalGameState = initialGameState;

        assertThrows(IllegalArgumentException.class, ()-> finalGameState.withBlindlyDrawnCard());

    }

//withClaimedRoute(...)
    @Test
    void withClaimedRouteWorks(){

        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());

        GameState withRoadGameState = initialGameState.withClaimedRoute(TestMap.route1, SortedBag.of(4, Card.BLACK));


        assertFalse(initialGameState.equals(withRoadGameState));
        assertNotEquals(initialGameState.currentPlayerState(),
                withRoadGameState.currentPlayerState());
        assertEquals(initialGameState.currentPlayerState().withClaimedRoute(TestMap.route1, SortedBag.of(4, Card.BLACK)).routes(),
                withRoadGameState.currentPlayerState().routes());
        assertEquals(initialGameState.currentPlayerState().withClaimedRoute(TestMap.route1, SortedBag.of(4, Card.BLACK)).cards(),
                withRoadGameState.currentPlayerState().cards());

    }

//lastTurnBegins()
    @Test
    void lastTurnBeginsWorks(){

        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());

        GameState withRoadGameState = initialGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLACK));

        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLACK));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLACK));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.RED));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLUE));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLUE));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route1, SortedBag.of(4, Card.BLUE));

        assertTrue(withRoadGameState.lastTurnBegins());
        assertFalse(initialGameState.lastTurnBegins());
    }

//forNextTurn()
    @Test
    void forNextTurnWorks(){
        GameState initialGameState = GameState.initial(SortedBag.of(2, TestMap.BER_STG, 1, TestMap.DE1_IT2), TestRandomizer.newRandom());
        GameState withRoadGameState = initialGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLACK));

        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLACK));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLACK));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.RED));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLUE));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route2, SortedBag.of(4, Card.BLUE));
        withRoadGameState = withRoadGameState.withClaimedRoute(TestMap.route1, SortedBag.of(4, Card.BLUE));


        //When lastTurnBegins = false
        assertEquals(initialGameState.currentPlayerId().next(), initialGameState.forNextTurn().currentPlayerId());
        assertNull(initialGameState.forNextTurn().lastPlayer());

        //When lastTurnBegins = true
        assertEquals(withRoadGameState.currentPlayerId().next(), withRoadGameState.forNextTurn().currentPlayerId());
        assertEquals(withRoadGameState.currentPlayerId(), withRoadGameState.forNextTurn().lastPlayer());
    }

    private static final class TestMap {

        // Stations - cities
        public static final Station BER = new Station(0, "Berne");
        public static final Station LAU = new Station(1, "Lausanne");
        public static final Station STG = new Station(2, "Saint-Gall");

        // Stations - countries
        public static final Station DE1 = new Station(3, "Allemagne");
        public static final Station DE2 = new Station(4, "Allemagne");
        public static final Station DE3 = new Station(5, "Allemagne");
        public static final Station AT1 = new Station(6, "Autriche");
        public static final Station AT2 = new Station(7, "Autriche");
        public static final Station IT1 = new Station(8, "Italie");
        public static final Station IT2 = new Station(9, "Italie");
        public static final Station IT3 = new Station(10, "Italie");
        public static final Station FR1 = new Station(11, "France");
        public static final Station FR2 = new Station(12, "France");

        // Countries
        public static final List<Station> DE = List.of(DE1, DE2, DE3);
        public static final List<Station> AT = List.of(AT1, AT2);
        public static final List<Station> IT = List.of(IT1, IT2, IT3);
        public static final List<Station> FR = List.of(FR1, FR2);

        public static final Ticket LAU_STG = new Ticket(LAU, STG, 13);
        public static final Ticket LAU_BER = new Ticket(LAU, BER, 2);
        public static final Ticket DE1_IT2 = new Ticket(DE1, IT2, 2);
        public static final Ticket BER_STG = new Ticket(BER, STG, 4);
        public Ticket BER_NEIGHBORS = ticketToNeighbors(List.of(BER), 6, 11, 8, 5);
        public Ticket FR_NEIGHBORS = ticketToNeighbors(FR, 5, 14, 11, 0);

        public static final Route route1 = new Route("AT1_STG_1",BER, LAU, 3,Route.Level.UNDERGROUND, null);
        public static final Route route2 = new Route("AT1_STG_1",LAU, STG, 6,Route.Level.UNDERGROUND, null);


        private Ticket ticketToNeighbors(List<Station> from, int de, int at, int it, int fr) {
            var trips = new ArrayList<Trip>();
            if (de != 0) trips.addAll(Trip.all(from, DE, de));
            if (at != 0) trips.addAll(Trip.all(from, AT, at));
            if (it != 0) trips.addAll(Trip.all(from, IT, it));
            if (fr != 0) trips.addAll(Trip.all(from, FR, fr));
            return new Ticket(trips);
        }
    }
}
