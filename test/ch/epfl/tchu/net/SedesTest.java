package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CancellationException;

import static org.junit.jupiter.api.Assertions.*;

public class SedesTest {
//Integer
    @Test
    void integerSerdeWorksOnSerialization(){
        assertEquals("2021", Serdes.INTEGER_SERDE.serialize(2021));
        assertEquals("-2021", Serdes.INTEGER_SERDE.serialize(-2021));
    }

    @Test
    void integerSerdeWorksOnDeserialization(){
        assertEquals(2021, Serdes.INTEGER_SERDE.deserialize("2021"));
        assertEquals(-2021, Serdes.INTEGER_SERDE.deserialize("-2021"));
    }

//String
    @Test
    void stringSerdeWorksOnSerialization(){
        assertEquals( "Q2hhcmxlcw==", Serdes.STRING_SERDE.serialize("Charles"));
    }

    @Test
    void stringSerdeWorksOnDeserialization(){
        assertEquals( "Charles", Serdes.STRING_SERDE.deserialize("Q2hhcmxlcw=="));
    }

//PlayerId
    @Test
    void playerIdSerdeWorksOnSerialization(){
        assertEquals( "0", Serdes.PLAYER_ID_SERDE.serialize(PlayerId.PLAYER_1));
        assertEquals( "1", Serdes.PLAYER_ID_SERDE.serialize(PlayerId.PLAYER_2));
    }

    @Test
    void playerIdSerdeWorksOnDeserialization(){
        assertEquals( PlayerId.PLAYER_1, Serdes.PLAYER_ID_SERDE.deserialize("0"));
        assertEquals(PlayerId.PLAYER_2, Serdes.PLAYER_ID_SERDE.deserialize("1"));
    }

//TurnKind
    @Test
    void turnKindSerdeWorksOnSerialization(){
        assertEquals("0", Serdes.TURN_KIND_SERDE.serialize(Player.TurnKind.DRAW_TICKETS));
        assertEquals("1", Serdes.TURN_KIND_SERDE.serialize(Player.TurnKind.DRAW_CARDS));
        assertEquals("2", Serdes.TURN_KIND_SERDE.serialize(Player.TurnKind.CLAIM_ROUTE));
    }

    @Test
    void turnKindSerdeWorksOnDeserialization(){
        assertEquals(Player.TurnKind.DRAW_TICKETS, Serdes.TURN_KIND_SERDE.deserialize("0"));
        assertEquals(Player.TurnKind.DRAW_CARDS, Serdes.TURN_KIND_SERDE.deserialize("1"));
        assertEquals(Player.TurnKind.CLAIM_ROUTE, Serdes.TURN_KIND_SERDE.deserialize("2"));
    }

//Card
    @Test
    void cardSerdeWorksOnSerialization(){
        assertEquals("0", Serdes.CARD_SERDE.serialize(Card.BLACK));
        assertEquals("1", Serdes.CARD_SERDE.serialize(Card.VIOLET));
        assertEquals("2", Serdes.CARD_SERDE.serialize(Card.BLUE));
        assertEquals("3", Serdes.CARD_SERDE.serialize(Card.GREEN));
        assertEquals("4", Serdes.CARD_SERDE.serialize(Card.YELLOW));
        assertEquals("5", Serdes.CARD_SERDE.serialize(Card.ORANGE));
        assertEquals("6", Serdes.CARD_SERDE.serialize(Card.RED));
        assertEquals("7", Serdes.CARD_SERDE.serialize(Card.WHITE));
        assertEquals("8", Serdes.CARD_SERDE.serialize(Card.LOCOMOTIVE));
    }

    @Test
    void cardSerdeWorksOnDeserialization(){
        assertEquals(Card.BLACK, Serdes.CARD_SERDE.deserialize("0"));
        assertEquals(Card.VIOLET, Serdes.CARD_SERDE.deserialize("1"));
        assertEquals(Card.BLUE, Serdes.CARD_SERDE.deserialize("2"));
        assertEquals(Card.GREEN, Serdes.CARD_SERDE.deserialize("3"));
        assertEquals(Card.YELLOW, Serdes.CARD_SERDE.deserialize("4"));
        assertEquals(Card.ORANGE, Serdes.CARD_SERDE.deserialize("5"));
        assertEquals(Card.RED, Serdes.CARD_SERDE.deserialize("6"));
        assertEquals(Card.WHITE, Serdes.CARD_SERDE.deserialize("7"));
        assertEquals(Card.LOCOMOTIVE, Serdes.CARD_SERDE.deserialize("8"));
    }

//Route
    @Test
    void routeSerdeWorksOnSerialization(){
        for (int i = 0; i < ChMap.routes().size(); ++i){

            String expected = String.valueOf(i);
            assertEquals(expected, Serdes.ROUTE_SERDE.serialize(ChMap.routes().get(i)));
        }
    }

    @Test
    void routeSerdeWorksOnDeserialization(){
        for (int i = 0; i < ChMap.routes().size(); ++i){
            Route expected = ChMap.routes().get(i);
            assertEquals(expected, Serdes.ROUTE_SERDE.deserialize(String.valueOf(i)));
        }
    }

//Ticket
    @Test
    void ticketSerdeWorksOnSerialization(){
        for (int i = 0; i < ChMap.tickets().size(); ++i){
            String expected = String.valueOf(i);
            assertEquals(expected, Serdes.TICKET_SERDE.serialize(ChMap.tickets().get(i)));
            if (i >= 38)
                ++i;
        }
    }

    @Test
    void ticketSerdeWorksOnDeserialization(){
        for (int i = 0; i < ChMap.tickets().size(); ++i){
            Ticket expected = ChMap.tickets().get(i);
            assertEquals(expected, Serdes.TICKET_SERDE.deserialize(String.valueOf(i)));
        }
    }

//List<String>
    @Test
    void listStringSerdeWorksOnSerialization(){
        //Empty list
        assertEquals("", Serdes.LIST_STRING_SERDE.serialize(List.of()));

        //Non empty list
        List<String> l = List.of("Theo", "Shin", "e s p a c e", "nombres 65");
        String expected = "VGhlbw==,U2hpbg==,ZSBzIHAgYSBjIGU=,bm9tYnJlcyA2NQ==";
        assertEquals(expected, Serdes.LIST_STRING_SERDE.serialize(l));
    }

    @Test
    void listStringSerdeWorksOnDeserialization(){
        //Empty list
        assertEquals(List.of(), Serdes.LIST_STRING_SERDE.deserialize(""));

        //Non empty list
        String data = "VGhlbw==,U2hpbg==,ZSBzIHAgYSBjIGU=,bm9tYnJlcyA2NQ==";
        List<String> expected = List.of("Theo", "Shin", "e s p a c e", "nombres 65");
        assertEquals(expected, Serdes.LIST_STRING_SERDE.deserialize(data));
    }

//List<Card>
    @Test
    void listCardSerdeWorksOnSerialization(){
        //Empty list
        assertEquals("", Serdes.LIST_CARD_SERDE.serialize(List.of()));

        //Non empty list
        String expected = "0,1,2,3,4,5,6,7,8";
        assertEquals(expected, Serdes.LIST_CARD_SERDE.serialize(Card.ALL));
    }

    @Test
    void listCardSerdeWorksOnDeserialization(){
        //Empty list
        assertEquals(List.of(), Serdes.LIST_CARD_SERDE.deserialize(""));

        //Non empty list
        String data = "0,1,2,3,4,5,6,7,8";
        List<Card> expected = Card.ALL;
        assertEquals(expected, Serdes.LIST_CARD_SERDE.deserialize(data));
    }

//List<Route>
    @Test
    void listRouteCardSerdeWorksOnSerialization(){
        //Empty list
        assertEquals("", Serdes.LIST_ROUTE_SERDE.serialize(List.of()));

        //Non empty list
        List<String> indexList = new ArrayList<>();
        for (Route r: ChMap.routes())
            indexList.add(String.valueOf(ChMap.routes().indexOf(r)));
        String expected = String.join(",", indexList);
        assertEquals(expected, Serdes.LIST_ROUTE_SERDE.serialize(ChMap.routes()));
    }

    @Test
    void listRouteSerdeWorksOnDeserialization(){
        //Empty list
        assertEquals(List.of(),Serdes.LIST_ROUTE_SERDE.deserialize(""));

        //Non empty list
        List<String> indexList = new ArrayList<>();
        for (Route r: ChMap.routes())
            indexList.add(String.valueOf(ChMap.routes().indexOf(r)));
        String data = String.join(",", indexList);
        List<Route> expected = ChMap.routes();
        assertEquals(expected, Serdes.LIST_ROUTE_SERDE.deserialize(data));
    }


//SortedBag<Card>
    @Test
    void sortedBagCardSerdeWorksOnSerialization(){
        //Empty bag
        SortedBag<Card> emptyBag = SortedBag.of();
        assertEquals("", Serdes.SORTED_BAG_CARD_SERDE.serialize(emptyBag));

        //Non empty bag
        SortedBag<Card> data = SortedBag.of(Card.ALL);
        List<String> indexList = new ArrayList<>();
        for (Card c: data)
            indexList.add(String.valueOf(Card.ALL.indexOf(c)));
        String expected = String.join(",",indexList);
        assertEquals(expected, Serdes.SORTED_BAG_CARD_SERDE.serialize(data));
    }

    @Test
    void sortedBagCardSerdeWorksOnDeserialization(){
        //Empty bag
        assertEquals(SortedBag.of(), Serdes.SORTED_BAG_CARD_SERDE.deserialize(""));

        //Non empty bag
        List<Card> expectedList = SortedBag.of(Card.ALL).toList();
        List<String> indexList = new ArrayList<>();
        for (Card c: expectedList)
            indexList.add(String.valueOf(Card.ALL.indexOf(c)));
        String data = String.join(",",indexList);
        SortedBag<Card> expected = SortedBag.of(expectedList);
        assertEquals(expected, Serdes.SORTED_BAG_CARD_SERDE.deserialize(data));
    }

//SortedBag<Ticket>
    @Test
    void sortedBagTicketSerdeWorksOnSerialization(){
        //Empty bag
        SortedBag<Ticket> emptyBag = SortedBag.of();
        assertEquals("", Serdes.SORTED_BAG_TICKETS_SERDE.serialize(emptyBag));

        //Non empty bagserialize
        SortedBag<Ticket> data = SortedBag.of(ChMap.tickets());
        List<String> indexList = new ArrayList<>();
        for (Ticket c: data)
            indexList.add(String.valueOf(ChMap.tickets().indexOf(c)));
        String expected = String.join(",",indexList);
        assertEquals(expected, Serdes.SORTED_BAG_TICKETS_SERDE.serialize(data));
    }

    @Test
    void sortedBagTicketSerdeWorksOnDeserialization(){
        //Empty bag
        assertEquals(SortedBag.of(), Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(""));

        //Non empty bag
        List<Ticket> expectedList = SortedBag.of(ChMap.tickets()).toList();
        List<String> indexList = new ArrayList<>();
        for (Ticket c: expectedList)
            indexList.add(String.valueOf(ChMap.tickets().indexOf(c)));
        String data = String.join(",",indexList);
        SortedBag expected = SortedBag.of(expectedList);
        assertEquals(expected, Serdes.SORTED_BAG_TICKETS_SERDE.deserialize(data));

    }

//List<SortedBag<Card>>
    @Test
    void listSortedBagCardSerdeWorksOnSerialization(){
        //Empty list
        assertEquals("", Serdes.LIST_SORTED_BAG_CARD_SERDE.serialize(List.of()));

        //Non empty list
        List<SortedBag<Card>> data = List.of(
                SortedBag.of(2, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.WHITE),
                SortedBag.of(1, Card.YELLOW, 1, Card.ORANGE)
        );
        List<String> indexList = new ArrayList<>();
        for (SortedBag<Card> s: data){
            List<String> indexListForBag = new ArrayList<>();
            for (Card c: s.toList())
                indexListForBag.add(String.valueOf(Card.ALL.indexOf(c)));
            indexList.add(String.join(",", indexListForBag));
        }
        String expected = String.join(";", indexList);

        assertEquals(expected, Serdes.LIST_SORTED_BAG_CARD_SERDE.serialize(data));
    }

    @Test
    void listSortedBagCardSerdeWorksOnDeserialization(){
        //Empty list
        assertEquals(List.of(), Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(""));

        //Non empty list
        List<SortedBag<Card>> expected = List.of(
                SortedBag.of(2, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.WHITE),
                SortedBag.of(1, Card.YELLOW, 1, Card.ORANGE)
        );
        List<String> indexList = new ArrayList<>();
        for (SortedBag<Card> s: expected){
            List<String> indexListForBag = new ArrayList<>();
            for (Card c: s.toList())
                indexListForBag.add(String.valueOf(Card.ALL.indexOf(c)));
            indexList.add(String.join(",", indexListForBag));
        }
        String data = String.join(";", indexList);

        assertEquals(expected, Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(data));
    }

//PublicCardState
    @Test
    void publicCardStateSerdeWorksOnSerializationEmptyCase(){
        //Null attributes
        List<Card> facedUpCard = List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.GREEN, Card.VIOLET);
        int deckSize = 0;
        int discardSize = 0;
        PublicCardState data = new PublicCardState(facedUpCard,deckSize,discardSize);
        String expected = "2,6,8,3,1;0;0";
        assertEquals(expected, Serdes.PUBLIC_CARD_STATE_SERDE.serialize(data));
    }

    @Test
    void publicCardStateSerdeWorksOnSerializationSimpleCase(){
        //Non null example
        List<Card> facedUpCard = List.of(Card.BLACK, Card.WHITE, Card.BLACK, Card.GREEN, Card.ORANGE);
        int deckSize = 26;
        int discardSize = 6;
        PublicCardState data = new PublicCardState(facedUpCard, deckSize, discardSize);
        String expected = "0,7,0,3,5;26;6";
        assertEquals(expected, Serdes.PUBLIC_CARD_STATE_SERDE.serialize(data));
    }

    @Test
    void publicCardStateSerdeWorksOnDeserializationEmptyCase(){
        //Null attributes
        List<Card> facedUpCard = List.of(Card.ORANGE, Card.RED, Card.LOCOMOTIVE, Card.YELLOW, Card.VIOLET);
        String data = "5,6,8,4,1;0;0";
        PublicCardState expected = new PublicCardState(facedUpCard, 0, 0);
        PublicCardState actual = Serdes.PUBLIC_CARD_STATE_SERDE.deserialize(data);
        assertEquals(expected.deckSize(), actual.deckSize());
        assertEquals(expected.discardsSize(), actual.discardsSize());

        List<Card> actualFacedUpCards = List.of(
                actual.faceUpCard(0),
                actual.faceUpCard(1),
                actual.faceUpCard(2),
                actual.faceUpCard(3),
                actual.faceUpCard(4)
        );
        assertEquals(facedUpCard, actualFacedUpCards);
    }

    @Test
    void publicCardStateSerdeWorksOnDeserializationSimpleCase(){
        //Non null example
        List<Card> facedUpCard = List.of(Card.RED, Card.BLUE, Card.BLACK, Card.LOCOMOTIVE, Card.ORANGE);
        int deckSize = 26;
        int discardSize = 6;
        String data = "6,2,0,8,5;26;6";
        PublicCardState expected = new PublicCardState(facedUpCard, deckSize, discardSize);
        PublicCardState actual = Serdes.PUBLIC_CARD_STATE_SERDE.deserialize(data);
        assertEquals(facedUpCard, List.of(
                actual.faceUpCard(0),
                actual.faceUpCard(1),
                actual.faceUpCard(2),
                actual.faceUpCard(3),
                actual.faceUpCard(4)
        ));
        assertEquals(expected.deckSize(), actual.deckSize());
        assertEquals(expected.discardsSize(), actual.discardsSize());
    }

//PublicPlayerState
    @Test
    void publicPlayerStateSerdeWorksOnSerializationEmptyCase(){
        //Null attributes
        int ticketCount = 0;
        int cardCount = 0;
        List<Route> routes = List.of();
        PublicPlayerState data = new PublicPlayerState(ticketCount, cardCount, routes);
        String expected = "0;0;";
        assertEquals(expected, Serdes.PUBLIC_PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void publicPlayerStateSerdeWorksOnSerializationSimpleCase(){
        //Some routes
        int ticketCount = 3;
        int cardCount = 12;
        List<Route> routes = List.of(ChMap.routes().get(5), ChMap.routes().get(61), ChMap.routes().get(12), ChMap.routes().get(72));
        PublicPlayerState data = new PublicPlayerState(ticketCount, cardCount, routes);
        String expected = "3;12;5,61,12,72";
        assertEquals(expected, Serdes.PUBLIC_PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void publicPlayerStateSerdeWorksOnSerializationExtremeCase(){
        //All routes
        int ticketCount = 11;
        int cardCount = 5;
        List<Route> routes = ChMap.routes();
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: ChMap.routes())
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String indexRoutes = String.join(",", indexListRoutes);
        PublicPlayerState data = new PublicPlayerState(ticketCount, cardCount, routes);
        String expected = new StringBuilder("11;5;")
                        .append(indexRoutes)
                        .toString();
        assertEquals(expected, Serdes.PUBLIC_PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void publicPlayerStateSerdeWorksOnDeserializationEmptyCase(){
        //Null attributes
        int ticketCount = 0;
        int cardCount = 0;
        List<Route> routes = List.of();
        String data = "0;0;";
        PublicPlayerState expected = new PublicPlayerState(ticketCount, cardCount, routes);
        PublicPlayerState actual = Serdes.PUBLIC_PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.ticketCount(), actual.ticketCount());
        assertEquals(expected.cardCount(),  actual.cardCount());
        assertEquals(expected.routes(), actual.routes());
    }

    @Test
    void publicPlayerStateSerdeWorksOnDeserializationSimpleCase(){
        //Some routes
        int ticketCount = 3;
        int cardCount = 12;
        List<Route> routes = List.of(ChMap.routes().get(5), ChMap.routes().get(61), ChMap.routes().get(12), ChMap.routes().get(72));
        String data = "3;12;5,61,12,72";
        PublicPlayerState expected = new PublicPlayerState(ticketCount, cardCount, routes);
        PublicPlayerState actual = Serdes.PUBLIC_PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.ticketCount(), actual.ticketCount());
        assertEquals(expected.cardCount(), actual.cardCount());
    }

    @Test
    void publicPlayerStateSerdeWorksOnDeserializationExtremeCase(){
        //All routes
        int ticketCount = 11;
        int cardCount = 12;
        List<Route> routes = ChMap.routes();
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: ChMap.routes())
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String indexRoutes = String.join(",", indexListRoutes);
        String data = new StringBuilder("11;12;")
                .append(indexRoutes)
                .toString();
        PublicPlayerState expected = new PublicPlayerState(ticketCount, cardCount, routes);
        PublicPlayerState actual = Serdes.PUBLIC_PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.ticketCount(), actual.ticketCount());
        assertEquals(expected.cardCount(), actual.cardCount());
        assertEquals(expected.routes(), actual.routes());
    }

//PlayerState
    @Test
    void playerStateSerdeWorksOnSerializationEmptyCase() {
        //with empty attributes
        SortedBag<Ticket> tickets = SortedBag.of();
        SortedBag<Card> cards = SortedBag.of();
        List<Route> routes = List.of();
        PlayerState data = new PlayerState(tickets, cards, routes);
        String expected = ";;";
        assertEquals(expected, Serdes.PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void playerStateSerdeWorksOnSerializationSimpleCase(){
        //With non empty attributes
        List<Ticket> ticketsBuffer = List.of(
                ChMap.tickets().get(1),
                ChMap.tickets().get(14),
                ChMap.tickets().get(9),
                ChMap.tickets().get(22),
                ChMap.tickets().get(19),
                ChMap.tickets().get(2)
        );
        SortedBag<Ticket> tickets = SortedBag.of(ticketsBuffer);
        SortedBag<Card> cards = SortedBag.of(1, Card.WHITE, 1, Card.RED);
        List<Route> routes = List.of(
                ChMap.routes().get(5),
                ChMap.routes().get(14),
                ChMap.routes().get(9),
                ChMap.routes().get(25),
                ChMap.routes().get(39),
                ChMap.routes().get(2),
                ChMap.routes().get(66)
        );
        List<String> indexListTickets = new ArrayList<>();
        for (Ticket t: tickets.toList())
            indexListTickets.add(String.valueOf(ChMap.tickets().indexOf(t)));
        List<String> indexListCards = new ArrayList<>();
        for (Card c: cards.toList())
            indexListCards.add(String.valueOf(Card.ALL.indexOf(c)));
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: routes)
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String strIndexTickets = String.join(",", indexListTickets);
        String strIndexCards = String.join(",", indexListCards);
        String strIndexRoutes = String.join(",", indexListRoutes);

        PlayerState data = new PlayerState(tickets, cards, routes);
        String expected = String.join(";", List.of(strIndexTickets, strIndexCards, strIndexRoutes));
        assertEquals(expected, Serdes.PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void playerStateSerdeWorksOnSerializationExtremeCase(){
        //With full list attributes
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        SortedBag<Card> cards = SortedBag.of(Card.ALL);
        List<Route> routes = ChMap.routes();
        List<String> indexListTickets = new ArrayList<>();
        for (Ticket t: tickets.toList())
            indexListTickets.add(String.valueOf(ChMap.tickets().indexOf(t)));
        List<String> indexListCards = new ArrayList<>();
        for (Card c: cards.toList())
            indexListCards.add(String.valueOf(Card.ALL.indexOf(c)));
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: routes)
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String strIndexTickets = String.join(",", indexListTickets);
        String strIndexCards = String.join(",", indexListCards);
        String strIndexRoutes = String.join(",", indexListRoutes);

        PlayerState data = new PlayerState(tickets, cards, routes);
        String expected = String.join(";", List.of(strIndexTickets, strIndexCards, strIndexRoutes));
        assertEquals(expected, Serdes.PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void playerStateSerdeWorksOnDeserializationEmptyCase(){
        //with empty attributes
        SortedBag<Ticket> tickets = SortedBag.of();
        SortedBag<Card> cards = SortedBag.of();
        List<Route> routes = List.of();
        String data = ";;";
        PlayerState expected = new PlayerState(tickets, cards, routes);
        PlayerState actual = Serdes.PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.tickets(), actual.tickets());
        assertEquals(expected.cards(), actual.cards());
        assertEquals(expected.routes(), actual.routes());
    }

    @Test
    void playerStateSerdeWorksOnDeserializationSimpleCase(){
        //With non empty attributes
        List<Ticket> ticketsBuffer = List.of(
                ChMap.tickets().get(0),
                ChMap.tickets().get(15),
                ChMap.tickets().get(10),
                ChMap.tickets().get(24),
                ChMap.tickets().get(3),
                ChMap.tickets().get(9)
        );
        SortedBag<Ticket> tickets = SortedBag.of(ticketsBuffer);
        SortedBag<Card> cards = SortedBag.of(1, Card.BLACK, 1, Card.LOCOMOTIVE);
        List<Route> routes = List.of(
                ChMap.routes().get(40),
                ChMap.routes().get(1),
                ChMap.routes().get(59),
                ChMap.routes().get(3),
                ChMap.routes().get(33),
                ChMap.routes().get(23),
                ChMap.routes().get(71)
        );
        List<String> indexListTickets = new ArrayList<>();
        for (Ticket t: tickets.toList())
            indexListTickets.add(String.valueOf(ChMap.tickets().indexOf(t)));
        List<String> indexListCards = new ArrayList<>();
        for (Card c: cards.toList())
            indexListCards.add(String.valueOf(Card.ALL.indexOf(c)));
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: routes)
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String strIndexTickets = String.join(",", indexListTickets);
        String strIndexCards = String.join(",", indexListCards);
        String strIndexRoutes = String.join(",", indexListRoutes);

        String data = String.join(";", List.of(strIndexTickets, strIndexCards, strIndexRoutes));
        PlayerState expected = new PlayerState(tickets, cards, routes);
        PlayerState actual = Serdes.PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.tickets(), actual.tickets());
        assertEquals(expected.cards(), actual.cards());
        assertEquals(expected.routes(), actual.routes());
    }

    @Test
    void playerStateSerdeWorksOnDeserializationExtremeCase(){
        //With full list attributes
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        SortedBag<Card> cards = SortedBag.of(Card.ALL);
        List<Route> routes = ChMap.routes();
        List<String> indexListTickets = new ArrayList<>();
        for (Ticket t: tickets.toList())
            indexListTickets.add(String.valueOf(ChMap.tickets().indexOf(t)));
        List<String> indexListCards = new ArrayList<>();
        for (Card c: cards.toList())
            indexListCards.add(String.valueOf(Card.ALL.indexOf(c)));
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: routes)
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String strIndexTickets = String.join(",", indexListTickets);
        String strIndexCards = String.join(",", indexListCards);
        String strIndexRoutes = String.join(",", indexListRoutes);
        String data = String.join(";", strIndexTickets, strIndexCards, strIndexRoutes);
        PlayerState expected = new PlayerState(tickets, cards, routes);
        PlayerState actual = Serdes.PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.tickets(), actual.tickets());
        assertEquals(expected.cards(), actual.cards());
        assertEquals(expected.routes(), actual.routes());
    }

//PublicGameState
    @Test
    void publicGameStateSerdeWorksOnSerializationEmptyCase(){
        ///Null attributes and empty lists

        //ticketCount
        int ticketsCount = 0;
        //cardState
        List<Card> facedUpCard = List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.GREEN, Card.VIOLET);
        PublicCardState cardState = new PublicCardState(facedUpCard,0,0);
        String cardStateString = "2,6,8,3,1;0;0";
        //currentPlayerId
        PlayerId currentPlayerId = PlayerId.PLAYER_1;
        //playerState(PLAYER_1) && playerState(PLAYER_2)
        List<Route> routes = List.of();
        PublicPlayerState playerState1 = new PublicPlayerState(0, 0, routes);
        PublicPlayerState playerState2 = new PublicPlayerState(0, 0, routes);
        Map<PlayerId, PublicPlayerState> playerState = Map.of(
            PlayerId.PLAYER_1, playerState1,
            PlayerId.PLAYER_2, playerState2
        );
        String playerStateString = "0;0;";
        //lastPlayer--> null
        PlayerId lastPlayer = null;

        PublicGameState data = new PublicGameState(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
        List<String> expectedList = List.of("0", cardStateString, "0", playerStateString, playerStateString, "");
        String expected = String.join(":", expectedList);
        assertEquals(expected, Serdes.PUBLIC_GAME_STATE_SERDE.serialize(data));
    }

    @Test
    void publicGameStateSerdeWorksOnSerializationSimpleCase(){
        ///Example given in doc !

        //ticketsCount
        int ticketsCount = 40;
        List<Card> facedUpCards = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cardState = new PublicCardState(facedUpCards, 30, 31);
        List<Route> routes = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, routes),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of())
        );

        PublicGameState data = new PublicGameState(ticketsCount, cardState, PlayerId.PLAYER_2, ps, null);
        String expected = "40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:";
        assertEquals(expected, Serdes.PUBLIC_GAME_STATE_SERDE.serialize(data));
    }

    @Test
    void publicGameStateSerdeWorksOnDeserializationEmptyCase(){
        ///Null attributes and empty lists

        //ticketCount
        int ticketsCount = 0;
        //cardState
        List<Card> facedUpCard = List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.GREEN, Card.VIOLET);
        PublicCardState cardState = new PublicCardState(facedUpCard,0,0);
        String cardStateString = "2,6,8,3,1;0;0";
        //currentPlayerId
        PlayerId currentPlayerId = PlayerId.PLAYER_1;
        //playerState(PLAYER_1) && playerState(PLAYER_2)
        List<Route> routes = List.of();
        PublicPlayerState playerState1 = new PublicPlayerState(0, 0, routes);
        PublicPlayerState playerState2 = new PublicPlayerState(0, 0, routes);
        Map<PlayerId, PublicPlayerState> playerState = Map.of(
                PlayerId.PLAYER_1, playerState1,
                PlayerId.PLAYER_2, playerState2
        );
        String playerStateString = "0;0;";
        //lastPlayer--> null
        PlayerId lastPlayer = null;

        List<String> dataList = List.of("0", cardStateString, "0", playerStateString, playerStateString, "");
        String data = String.join(":", dataList);
        PublicGameState expected = new PublicGameState(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
        PublicGameState actual = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(data);

        //ticketsCount test
        assertEquals(expected.ticketsCount(), actual.ticketsCount());
        //cardState test
        assertEquals(facedUpCard, List.of(
                actual.cardState().faceUpCard(0),
                actual.cardState().faceUpCard(1),
                actual.cardState().faceUpCard(2),
                actual.cardState().faceUpCard(3),
                actual.cardState().faceUpCard(4)
        ));
        assertEquals(expected.cardState().deckSize(), actual.cardState().deckSize());
        assertEquals(expected.cardState().discardsSize(), actual.cardState().discardsSize());
        //currentPlayer test
        assertEquals(expected.currentPlayerId(), actual.currentPlayerId());
        //playerState
        assertEquals(expected.playerState(PlayerId.PLAYER_1).ticketCount(), actual.playerState(PlayerId.PLAYER_1).ticketCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).ticketCount(), actual.playerState(PlayerId.PLAYER_2).ticketCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_1).cardCount(), actual.playerState(PlayerId.PLAYER_1).cardCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).cardCount(), actual.playerState(PlayerId.PLAYER_2).cardCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_1).routes(), actual.playerState(PlayerId.PLAYER_1).routes());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).routes(), actual.playerState(PlayerId.PLAYER_2).routes());
        //lastPlayer
        assertEquals(expected.lastPlayer(), actual.lastPlayer());
    }

    @Test
    void publicGameStateSerdeWorksOnDeserializationSimpleCase(){
        ///Example given in doc !

        //ticketsCount
        int ticketsCount = 40;
        List<Card> facedUpCards = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cardState = new PublicCardState(facedUpCards, 30, 31);
        List<Route> routes = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, routes),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of())
        );

        String data = "40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:";
        PublicGameState expected = new PublicGameState(ticketsCount, cardState, PlayerId.PLAYER_2, ps, null);
        PublicGameState actual = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(data);
        //ticketsCount test
        assertEquals(expected.ticketsCount(), actual.ticketsCount());
        //cardState test
        assertEquals(facedUpCards, List.of(
                actual.cardState().faceUpCard(0),
                actual.cardState().faceUpCard(1),
                actual.cardState().faceUpCard(2),
                actual.cardState().faceUpCard(3),
                actual.cardState().faceUpCard(4)
        ));
        assertEquals(expected.cardState().deckSize(), actual.cardState().deckSize());
        assertEquals(expected.cardState().discardsSize(), actual.cardState().discardsSize());
        //currentPlayer test
        assertEquals(expected.currentPlayerId(), actual.currentPlayerId());
        //playerState
        assertEquals(expected.playerState(PlayerId.PLAYER_1).ticketCount(), actual.playerState(PlayerId.PLAYER_1).ticketCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).ticketCount(), actual.playerState(PlayerId.PLAYER_2).ticketCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_1).cardCount(), actual.playerState(PlayerId.PLAYER_1).cardCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).cardCount(), actual.playerState(PlayerId.PLAYER_2).cardCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_1).routes(), actual.playerState(PlayerId.PLAYER_1).routes());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).routes(), actual.playerState(PlayerId.PLAYER_2).routes());
        //lastPlayer
        assertEquals(expected.lastPlayer(), actual.lastPlayer());
    }
}
