package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {

//Constructor(...)
    @Test
    void constructorCopiesLists(){
        var routes = new ArrayList<Route>(ChMap.routes());
        var tickets = SortedBag.of(ChMap.tickets());
        var cards = SortedBag.of(Card.ALL);

        var playerState = new PlayerState(tickets, cards, routes);

        //Change list to check if the references ar connected (should make a copy of list in constructor)
        cards.toList().clear();
        tickets.toList().clear();
        routes.clear();

        //Check if copied
        assertArrayEquals(ChMap.routes().toArray(), playerState.routes().toArray());
        assertArrayEquals(SortedBag.of(ChMap.tickets()).toList().toArray(), playerState.tickets().toList().toArray());
        assertArrayEquals(Card.ALL.toArray(), playerState.cards().toList().toArray());
    }


//initial(...)
    @Test
    void initialThrowsIllegalArgument(){
        SortedBag<Card> emptyInitCards = SortedBag.of();
        SortedBag<Card> outOfRangeInitCards = SortedBag.of(5, Card.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState.initial(emptyInitCards);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState.initial(outOfRangeInitCards);
        });
    }

    @Test
    void initialCopiesSortedBag(){
        var initCards = SortedBag.of(4, Card.BLUE);
        var playerState = PlayerState.initial(initCards);
        initCards.toList().clear();
        assertArrayEquals(SortedBag.of(4, Card.BLUE).toList().toArray(), playerState.cards().toList().toArray());
    }

    @Test
    void initialHasEmptyTicketsAndRoutes(){
        var initCards = SortedBag.of(4, Card.LOCOMOTIVE);
        var playerState = PlayerState.initial(initCards);

        assertEquals(0, playerState.tickets().size());
        assertEquals(0, playerState.routes().size());
     }


 // tickets()
    @Test
    void ticketsReturnUnmodifiableOrCopiedSortedBag(){
        var routes = new ArrayList<Route>(ChMap.routes());
        var tickets = SortedBag.of(ChMap.tickets());
        var cards = SortedBag.of(Card.ALL);
        var playerState  = new PlayerState(tickets, cards, routes);

        playerState.tickets().toList().clear();
        assertEquals(SortedBag.of(ChMap.tickets()), playerState.tickets());
    }


//withAddedTickets(...)
    @Test
    void withAddedTicketsWorks(){
        var ticket1 = new Ticket(new Station(1, "Bâle"), new Station(3, "Berne"), 5);
        var ticket2 = new Ticket(new Station(1, "Bâle"), new Station(5, "Brig"), 10);
        var ticket3 = new Ticket(new Station(1, "Bâle"), new Station(4, "Saint Galle"), 8);
        var ticket4 = new Ticket(new Station(3, "Berne"), new Station(6, "Lauanne"), 10);

        var routes = new ArrayList<Route>(ChMap.routes());
        var cards = SortedBag.of(Card.ALL);

        //Create the list of the tickets
        var ticketsBuilder = new SortedBag.Builder<Ticket>();
        ticketsBuilder.add(ticket1);
        ticketsBuilder.add(ticket2);
        var tickets = ticketsBuilder.build();

        var playerState = new PlayerState(tickets, cards, routes);
        playerState = playerState.withAddedTickets(SortedBag.of(1,ticket3 ,1,ticket4));

        //Testif the tickets have been corrrectly added
        assertTrue(playerState.tickets().contains(ticket3));
        assertTrue(playerState.tickets().contains(ticket4));
    }

//cards()
    @Test
    void cardsReturnUnmodifiableOrCopiedSortedBag(){
        var initCards = SortedBag.of(4, Card.BLUE);
        var playerState = PlayerState.initial(initCards);

        playerState.cards().toList().clear();

        assertArrayEquals(SortedBag.of(4, Card.BLUE).toList().toArray(), playerState.cards().toList().toArray());
    }

//withAddedCard()
    void withAddedCardWorks(){
        var initCards = SortedBag.of(4, Card.BLUE);
        var playerState = PlayerState.initial(initCards);

        playerState = playerState.withAddedCard(Card.LOCOMOTIVE);
        assertTrue(playerState.cards().contains(Card.LOCOMOTIVE));
        assertEquals(5, playerState.cards().size());
    }

//withAddedCards(...)
    @Test
    void withAddedCardsWorks(){
        var initCards = SortedBag.of(4, Card.BLUE);
        var addedCards = SortedBag.of(1, Card.LOCOMOTIVE, 1, Card.RED);
        var playerState = PlayerState.initial(initCards);

        playerState = playerState.withAddedCards(addedCards);
        assertTrue(playerState.cards().contains(Card.LOCOMOTIVE) && playerState.cards().contains(Card.RED));
        assertEquals(6, playerState.cards().size());
    }

//canClaimRoute(...)

}
