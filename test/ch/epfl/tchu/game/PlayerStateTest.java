package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {

    /**
     * Attributes
     */
    private static final List<Color> COLORS =
            List.of(
                    Color.BLACK,
                    Color.VIOLET,
                    Color.BLUE,
                    Color.GREEN,
                    Color.YELLOW,
                    Color.ORANGE,
                    Color.RED,
                    Color.WHITE);
    private static final List<Card> CAR_CARDS =
            List.of(
                    Card.BLACK,
                    Card.VIOLET,
                    Card.BLUE,
                    Card.GREEN,
                    Card.YELLOW,
                    Card.ORANGE,
                    Card.RED,
                    Card.WHITE);

//Constructor(...)
    @Test
    void constructorCopiesLists(){
        var routes = new ArrayList<>(ChMap.routes());
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
        var routes = new ArrayList<>(ChMap.routes());
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

        var routes = new ArrayList<>(ChMap.routes());
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
    @Test
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
    @Test
    void canClaimRouteOnTrue(){
        //Station
        var COI = new Station(0, "Coire");
        var WAS = new Station(1, "Wassen");
        var DAV = new Station(2, "Davos");
        var AT3 = new Station(3, "Vienne");
        var IT1 = new Station(4, "Milan");
        var BRU = new  Station(5, "Brosio");
        //Routes
        var route1 = new Route("COI_WAS_1", COI, WAS, 5, Route.Level.UNDERGROUND, null);
        var route2 = new Route("DAV_AT3_1", DAV, AT3, 3, Route.Level.UNDERGROUND, null);
        var route3= new Route("DAV_IT1_1", DAV, IT1, 3, Route.Level.UNDERGROUND, null);
        var routeTarget = new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE);

        //List et SortedBag
        var routes = List.of(route1, route2, route3);
        var cards = SortedBag.of(5, Card.BLUE);
        var tickets = SortedBag.of(ChMap.tickets());


        var playerState = new PlayerState(tickets, cards, routes);
        assertTrue(playerState.canClaimRoute(routeTarget));
    }

    @Test
    void canClaimRouteOnTrueOrFalseBecauseCars(){
        //Station
        var COI = new Station(0, "Coire");
        var WAS = new Station(1, "Wassen");
        var DAV = new Station(2, "Davos");
        var AT3 = new Station(3, "Vienne");
        var IT1 = new Station(4, "Milan");
        var BRU = new  Station(5, "Brosio");
        //Routes
        var route1 = new Route("COI_WAS_1", COI, WAS, 6, Route.Level.UNDERGROUND, null);
        var route2 = new Route("DAV_AT3_1", DAV, AT3, 6, Route.Level.UNDERGROUND, null);
        var route3 = new Route("DAV_IT1_1", DAV, IT1, 6, Route.Level.UNDERGROUND, null);
        var route4 = new Route("DAS_IT1_1", BRU, IT1, 6, Route.Level.UNDERGROUND, null);
        var route5 = new Route("DAO_IT1_1", DAV, COI, 6, Route.Level.UNDERGROUND, null);
        var route6 = new Route("DAE_IT1_1", AT3, COI, 6, Route.Level.UNDERGROUND, null);


        //List et SortedBag
        var routes = List.of(route1, route2, route3, route4, route5, route6);
        var cards = SortedBag.of(6, Card.BLUE);
        var tickets = SortedBag.of(ChMap.tickets());


        var playerState = new PlayerState(tickets, cards, routes);

        //Should have Jus enough cards -----> Should be true
        var routeTarget1 = new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE);
        assertTrue(playerState.canClaimRoute(routeTarget1));

        //Shouldn't have enough cards ------> Should be false
        var routeTarget2 = new Route("BRU_DAV_1", BRU, DAV, 6, Route.Level.UNDERGROUND, Color.BLUE);
        assertFalse(playerState.canClaimRoute(routeTarget2));
    }

    @Test
    void canClaimRouteOnFalseBecauseCards(){
        //Station
        var COI = new Station(0, "Coire");
        var WAS = new Station(1, "Wassen");
        var DAV = new Station(2, "Davos");
        var AT3 = new Station(3, "Vienne");
        var IT1 = new Station(4, "Milan");
        var BRU = new  Station(5, "Brosio");
        //Routes
        var route1 = new Route("COI_WAS_1", COI, WAS, 5, Route.Level.UNDERGROUND, null);
        var route2 = new Route("DAV_AT3_1", DAV, AT3, 3, Route.Level.UNDERGROUND, null);
        var route3= new Route("DAV_IT1_1", DAV, IT1, 3, Route.Level.UNDERGROUND, null);


        //List et SortedBag
        var routes = List.of(route1, route2, route3);
        var cards = SortedBag.of(5, Card.RED);
        var tickets = SortedBag.of(ChMap.tickets());


        var playerState = new PlayerState(tickets, cards, routes);

        //Need 6 RED cards but the playedState contains only 5 RED cards !
        var routeTarget1 = new Route("BRU_DAV_1", BRU, DAV, 6, Route.Level.UNDERGROUND, Color.RED);
        assertFalse(playerState.canClaimRoute(routeTarget1));

        //Need 4 BLUE cards but the playedState contains only 5 RED cards !
        var routeTarget2 = new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE);
        assertFalse(playerState.canClaimRoute(routeTarget2));
    }


//possibleClaimCards(...)
    @Test
    void possibleClaimCardsWorksForOvergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Route.Level.OVERGROUND, color);
                assertEquals(List.of(SortedBag.of(l, card)), PlayerState.possibleClaimCards(r));
            }
        }
    }

    @Test
    void possibleClaimCardsWorksOnOvergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Route.Level.OVERGROUND, null);
            var expected = List.of(
                    SortedBag.of(l, Card.BLACK),
                    SortedBag.of(l, Card.VIOLET),
                    SortedBag.of(l, Card.BLUE),
                    SortedBag.of(l, Card.GREEN),
                    SortedBag.of(l, Card.YELLOW),
                    SortedBag.of(l, Card.ORANGE),
                    SortedBag.of(l, Card.RED),
                    SortedBag.of(l, Card.WHITE));
            assertEquals(expected, PlayerState.possibleClaimCards(r));
        }
    }

    @Test
    void possibleClaimCardsWorksOnUndergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, color);

                var expected = new ArrayList<SortedBag<Card>>();
                for (var locomotives = 0; locomotives <= l; locomotives++) {
                    var cars = l - locomotives;
                    expected.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
                assertEquals(expected, PlayerState.possibleClaimCards(r));
            }
        }
    }

    @Test
    void possibleClaimCardsWorksOnUndergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, null);

            var expected = new ArrayList<SortedBag<Card>>();
            for (var locomotives = 0; locomotives <= l; locomotives++) {
                var cars = l - locomotives;
                if (cars == 0)
                    expected.add(SortedBag.of(locomotives, Card.LOCOMOTIVE));
                else {
                    for (var card : CAR_CARDS)
                        expected.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
            }
            assertEquals(expected, PlayerState.possibleClaimCards(r));
        }
    }

//possibleAdditionalCards(...)
    @Test
    void possibleAdditionalClaimCardsThrowsIllegalArgument(){

    }

    @Test
    void possibleAdditionalClaimCardsWorks(){

    }

//withClaimedRoute(...)
    @Test
    void withClaimedRouteWorks(){

    }

//ticketPoints()
    @Test
    void ticketPointsWorks(){

    }

//finalPoints()
    @Test
    void finalPointsWorks(){

    }
}
