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

    private List<Route> routesOfThePlayer = new ArrayList<>();
    {
        routesOfThePlayer.add(ChMap.routes().get(0));
        routesOfThePlayer.add(ChMap.routes().get(2));
        routesOfThePlayer.add(ChMap.routes().get(4));
        routesOfThePlayer.add(ChMap.routes().get(5));
        routesOfThePlayer.add(ChMap.routes().get(10));
    }

    private List<Ticket> ticketsOfThePlayer = new ArrayList<>();
    {
        ticketsOfThePlayer.add(ChMap.tickets().get(0));
        ticketsOfThePlayer.add(ChMap.tickets().get(3));
        ticketsOfThePlayer.add(ChMap.tickets().get(5));
        ticketsOfThePlayer.add(ChMap.tickets().get(8));
        ticketsOfThePlayer.add(ChMap.tickets().get(9));
    }


    private List<Card> cardsOfThePlayer = new ArrayList<>();
    {
        cardsOfThePlayer.add(Card.BLUE);
        cardsOfThePlayer.add(Card.BLUE);
        cardsOfThePlayer.add(Card.BLUE);
        cardsOfThePlayer.add(Card.LOCOMOTIVE);
        cardsOfThePlayer.add(Card.ORANGE);
    }




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
    void possibleClaimCardsThrowsIllegalArgument(){
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
        var routeTarget = new Route("DAZ_IT1_1", BRU, COI, 6, Route.Level.UNDERGROUND, null);


        //List et SortedBag
        var routes = List.of(route1, route2, route3, route4, route5, route6);
        var cards = SortedBag.of(6, Card.BLUE);
        var tickets = SortedBag.of(ChMap.tickets());

        //PlayerState to test
        var playerState = new PlayerState(tickets, cards, routes);

        assertThrows(IllegalArgumentException.class, () -> {
           playerState.possibleClaimCards(routeTarget);
        });

    }

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

                //Cards for playerStates
                var cards1 = SortedBag.of(l, card);
                var cards2 = SortedBag.of(l, Card.CARS.get((i+1)%CAR_CARDS.size()));
                var cards3 = SortedBag.of(l, Card.LOCOMOTIVE);

                //1.Same Cars, 2.Other cards, 3. Locomotives cards, 4. Empty cards,
                var playerState1 = new PlayerState(SortedBag.of(), cards1, List.of());
                var playerState2 = new PlayerState(SortedBag.of(), cards2, List.of());
                var playerState3 = new PlayerState(SortedBag.of(), cards3, List.of());
                var playerState4 = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());

                //Tests
                assertEquals(List.of(SortedBag.of(l, card)), playerState1.possibleClaimCards(r));
                assertEquals(List.of(), playerState2.possibleClaimCards(r));
                assertEquals(List.of(), playerState3.possibleClaimCards(r));
                assertEquals(List.of(), playerState4.possibleClaimCards(r));
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
            var allPossibleClaimCards = List.of(
                    SortedBag.of(l, Card.BLACK),
                    SortedBag.of(l, Card.VIOLET),
                    SortedBag.of(l, Card.BLUE),
                    SortedBag.of(l, Card.GREEN),
                    SortedBag.of(l, Card.YELLOW),
                    SortedBag.of(l, Card.ORANGE),
                    SortedBag.of(l, Card.RED),
                    SortedBag.of(l, Card.WHITE));

            var cards1Builder = new SortedBag.Builder();
            for (int i = 0; i < allPossibleClaimCards.size(); ++i)
                cards1Builder.add(allPossibleClaimCards.get(i));


            //PlayerStates cards : 1. All cards, 2. Only one type of cards, 3 Locomotive cards
            var cards1 = cards1Builder.build();
            var cards2 = SortedBag.of(l, Card.YELLOW, 2, Card.LOCOMOTIVE);
            var cards3 = SortedBag.of(l, Card.LOCOMOTIVE);

            //PlayerStates
            var playerState1 = new PlayerState(SortedBag.of(), cards1,List.of());
            var playerState2 = new PlayerState(SortedBag.of(), cards2, List.of());
            var playerState3 = new PlayerState(SortedBag.of(), cards3,List.of());
            var playerState4 = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());

            //Tests
            assertEquals(allPossibleClaimCards, playerState1.possibleClaimCards(r));
            assertEquals(List.of(SortedBag.of(l, Card.YELLOW)), playerState2.possibleClaimCards(r));
            assertEquals(List.of(), playerState3.possibleClaimCards(r));
            assertEquals(List.of(), playerState4.possibleClaimCards(r));
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

                //Build the expected of full possibilities
                var expected1 = new ArrayList<SortedBag<Card>>();
                var cards1Builder = new SortedBag.Builder<Card>();
                for (var locomotives = 0; locomotives <= l; locomotives++) {
                    var cars = l - locomotives;
                    expected1.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                    cards1Builder.add(SortedBag.of(Card.ALL));
                }

                //Expected with always one locomotive less
                var expected2 = new ArrayList<SortedBag<Card>>();
                for(var locomotives = 0; locomotives <= l-1; locomotives++){
                    var cars = l - locomotives;
                    expected2.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }

                //Expected with only colors
                var expected3 = List.of(SortedBag.of(l, card));

                //Expected with only loco
                var expected4 = List.of(SortedBag.of(Card.LOCOMOTIVE));

                //PlayerStates cards : 1. All cards, 2. Only one type of cards, 3 Locomotive cards
                var cards1 = cards1Builder.build();
                var cards2 = SortedBag.of(l, card, l-1, Card.LOCOMOTIVE);
                var cards3 = SortedBag.of(l, card);
                var cards4 = SortedBag.of(l, Card.LOCOMOTIVE);

                //PlayerStates
                var playerState1 = new PlayerState(SortedBag.of(), cards1, List.of());
                var playerState2 = new PlayerState(SortedBag.of(), cards2, List.of());
                var playerState3 = new PlayerState(SortedBag.of(), cards3, List.of());
                var playerState4 = new PlayerState(SortedBag.of(), cards4, List.of());
                var playerState5 = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());

                //Tests
                assertEquals(expected1, playerState1.possibleClaimCards(r));
                assertEquals(expected2, playerState2.possibleClaimCards(r));
                assertEquals(expected3, playerState3.possibleClaimCards(r));
                assertEquals(expected4, playerState4.possibleClaimCards(r));
                assertEquals(List.of(), playerState5.possibleClaimCards(r));
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

            //Expected1 with all possibilites
            var cards1Builder = new SortedBag.Builder<Card>();
            var expected1 = new ArrayList<SortedBag<Card>>();
            for (var locomotives = 0; locomotives <= l; locomotives++) {
                var cars = l - locomotives;
                if (cars == 0)
                    expected1.add(SortedBag.of(locomotives, Card.LOCOMOTIVE));
                else {
                    for (var card : CAR_CARDS)
                        expected1.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
                cards1Builder.add(SortedBag.of(Card.ALL));
            }

            //Expected with always one locomotive less
            var expected2 = new ArrayList<SortedBag<Card>>();
            var cards2Builder = new SortedBag.Builder<Card>();
            for(var locomotives = 0; locomotives <= l-1; locomotives++){
                var cars = l - locomotives;
                for (var card : CAR_CARDS) {
                    expected2.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                    cards2Builder.add(l, card);
                }
                if (locomotives > 0)
                    cards2Builder.add(locomotives, Card.LOCOMOTIVE);
            }

            //Expected with only colors
            var expected3 = List.of(SortedBag.of(l, Card.YELLOW));

            //Expected with only loco
            var expected4 = List.of(SortedBag.of(Card.LOCOMOTIVE));

            //PlayerStates cards : 1. All cards, 2. Only one type of cards, 3 Locomotive cards
            var cards1 = cards1Builder.build();
            var cards2 = cards2Builder.build();
            var cards3 = SortedBag.of(l, Card.YELLOW);
            var cards4 = SortedBag.of(l, Card.LOCOMOTIVE);

            //PlayerStates
            var playerState1 = new PlayerState(SortedBag.of(), cards1, List.of());
            var playerState2 = new PlayerState(SortedBag.of(), cards2, List.of());
            var playerState3 = new PlayerState(SortedBag.of(), cards3, List.of());
            var playerState4 = new PlayerState(SortedBag.of(), cards4, List.of());
            var playerState5 = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());

            System.out.println("Length of Loco : " + l);

            //Tests
            assertEquals(expected1, playerState1.possibleClaimCards(r));
            assertEquals(expected2, playerState2.possibleClaimCards(r));
            assertEquals(expected3, playerState3.possibleClaimCards(r));
            assertEquals(expected4, playerState4.possibleClaimCards(r));
            assertEquals(List.of(), playerState5.possibleClaimCards(r));
        }
    }

//possibleAdditionalCards(...)
    @Test
    void possibleAdditionalClaimCardsThrowsIllegalArgument(){

            //Expected with only loco
            var expected4 = List.of(SortedBag.of(Card.LOCOMOTIVE));

            //PlayerStates cards : 1. All cards, 2. Only one type of cards, 3 Locomotive cards
            var cards = cardsBuilder.build();

            //PlayerStates
            var playerState1 = new PlayerState(SortedBag.of(), cards1, List.of());
            var playerState2 = new PlayerState(SortedBag.of(), cards2, List.of());
            var playerState3 = new PlayerState(SortedBag.of(), cards3, List.of());
            var playerState4 = new PlayerState(SortedBag.of(), cards4, List.of());
            var playerState5 = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());

            //Tests
            assertEquals(expected1, playerState1.possibleClaimCards(r));
            assertEquals(expected2, playerState2.possibleClaimCards(r));
            assertEquals(expected3, playerState3.possibleClaimCards(r));
            assertEquals(expected4, playerState4.possibleClaimCards(r));
            assertEquals(List.of(SortedBag.of()), playerState5.possibleClaimCards(r));
        }
    }
/*
    @Test
    void possibleAdditionalClaimCardsWorks(){
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, null);

            //Expected1 with all possibilites
            var cardsBuilder = new SortedBag.Builder<Card>();
            var initList1 = new ArrayList<SortedBag<Card>>();
            for (var locomotives = 0; locomotives <= l; locomotives++) {
                var cars = l - locomotives;
                if (cars == 0)
                    initList1.add(SortedBag.of(locomotives, Card.LOCOMOTIVE));
                else {
                    for (var card : CAR_CARDS)
                        initList1.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
                cardsBuilder.add(SortedBag.of(Card.ALL));
            }

            //Expected with only colors
            var expected3 = List.of(SortedBag.of(l, Card.YELLOW));

    }
    */

//withClaimedRoute(...)
    @Test
    void withClaimedRouteWorks(){
        Route routeTest = new Route("id", ChMap.stations().get(0), ChMap.stations().get(1), 5, Route.Level.OVERGROUND, Color.BLUE);
        SortedBag<Card> cardsTest = SortedBag.of(cardsOfThePlayer);
        PlayerState playerTest1 = new PlayerState( SortedBag.of(ticketsOfThePlayer), cardsTest, routesOfThePlayer);

        routesOfThePlayer.add(routeTest);
        cardsOfThePlayer.remove(Card.BLUE);
        SortedBag<Card> cardsTest2 = SortedBag.of(cardsOfThePlayer);
        PlayerState playerTest2 = new PlayerState( SortedBag.of(ticketsOfThePlayer), cardsTest2, routesOfThePlayer);

        assertEquals(playerTest2.routes(), playerTest1.withClaimedRoute(routeTest, SortedBag.of(Card.BLUE)).routes());
        assertEquals(playerTest2.cards(), playerTest1.withClaimedRoute(routeTest, SortedBag.of(Card.BLUE)).cards());
    }

//ticketPoints()
    @Test
    void ticketPointsWorks(){

        Ticket ticketTest1 = new Ticket(ChMap.stations().get(1), ChMap.stations().get(0), 3);
        Ticket ticketTest2 = new Ticket(ChMap.stations().get(2), ChMap.stations().get(5), 7);
        List<Ticket> ticketList = new ArrayList<>();
            ticketList.add(ticketTest1);
            ticketList.add(ticketTest2);

        List<Route> routes = new ArrayList<>();
            routes.add( new Route("id", ChMap.stations().get(0), ChMap.stations().get(1), 5, Route.Level.OVERGROUND, Color.BLUE));
            routes.add(new Route("id2", ChMap.stations().get(2), ChMap.stations().get(8), 5, Route.Level.OVERGROUND, Color.BLUE));
            routes.add(new Route("id3", ChMap.stations().get(8), ChMap.stations().get(4), 5, Route.Level.OVERGROUND, Color.BLUE));

        PlayerState playerTest1 = new PlayerState( SortedBag.of(ticketList), SortedBag.of(Card.ALL), routes);

        assertEquals(-4, playerTest1.ticketPoints());
    }


//finalPoints()
    @Test
    void finalPointsWorks(){
        PlayerState playerTest1 = new PlayerState( SortedBag.of(ticketsOfThePlayer), SortedBag.of(cardsOfThePlayer), routesOfThePlayer);
        assertEquals(playerTest1.claimPoints() + playerTest1.ticketPoints(), playerTest1.finalPoints());
    }
}
