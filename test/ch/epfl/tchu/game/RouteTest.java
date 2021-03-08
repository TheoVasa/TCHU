package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    @Test
    void constructorFailsOnEmptyId(){
        assertThrows(NullPointerException.class, () -> {
            new Route(null, ChMap.stations().get(0), ChMap.stations().get(1), 3, Route.Level.UNDERGROUND, Color.ORANGE);
        });
    }

    @Test
    void constructorFailsOnEmptyStations(){
        //Station1
        assertThrows(NullPointerException.class, () -> {
            new Route("ID", null, ChMap.stations().get(1), 3, Route.Level.UNDERGROUND, Color.ORANGE);
        });

        //Station2
        assertThrows(NullPointerException.class, () -> {
            new Route("ID", ChMap.stations().get(0), null, 3, Route.Level.UNDERGROUND, Color.ORANGE);
        });

    }
    @Test
    void constructorFailsOnEmptyLevel(){
        assertThrows(NullPointerException.class, () -> {
            new Route("ID", ChMap.stations().get(0), ChMap.stations().get(1), 3, null, Color.ORANGE);
        });
    }

    @Test
    void constructorFailsOnIdenticalStations(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("ID", ChMap.stations().get(0), ChMap.stations().get(0), 3, Route.Level.OVERGROUND, Color.ORANGE);
        });
    }

    @Test
    void constructorFailsLengthOutOfRange(){

        //Out of range small number
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("ID", ChMap.stations().get(0), ChMap.stations().get(1), -2, Route.Level.UNDERGROUND, Color.ORANGE);
        });

        //Out of range big number
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("ID", ChMap.stations().get(0), ChMap.stations().get(1), 10, Route.Level.UNDERGROUND, Color.ORANGE);
        });
    }


    @Test
    void stationsAreCorrectOnNonEmptyRoad() {

        Station s1 = ChMap.stations().get(0);
        Station s2 = ChMap.stations().get(1);
        Route r = new Route ("ID", s1, s2,5, Route.Level.UNDERGROUND, Color.ORANGE );
        assertEquals(s1, r.station1());
        assertEquals(s2, r.station2());
    }

    @Test
    void lengthIsCorrectOnNonEmptyRoad() {

        Station s1 = ChMap.stations().get(0);
        Station s2 = ChMap.stations().get(1);
        Route r = new Route ("ID", s1, s2,5, Route.Level.UNDERGROUND, Color.ORANGE );
        assertEquals(5, r.length());

    }

    @Test
    void levelIsCorrectOnNonEmptyRoad() {
        Station s1 = ChMap.stations().get(0);
        Station s2 = ChMap.stations().get(1);
        Route r = new Route ("ID", s1, s2,5, Route.Level.UNDERGROUND, Color.ORANGE );
        assertEquals(Route.Level.UNDERGROUND, r.level());
    }

    @Test
    void colorIsCorrectOnNonEmptyRoad() {
        Station s1 = ChMap.stations().get(0);
        Station s2 = ChMap.stations().get(1);
        Route r = new Route ("ID", s1, s2,5, Route.Level.UNDERGROUND, Color.ORANGE );
        assertEquals(Color.ORANGE, r.color());
    }

    @Test
    void stationOppositeWorksOnNonEmptyRoad() {
        Station s1 = ChMap.stations().get(0);
        Station s2 = ChMap.stations().get(1);
        Route r = new Route ("ID", s1, s2,5, Route.Level.UNDERGROUND, Color.ORANGE );
        assertEquals(s2, r.stationOpposite(s1));
        assertEquals(s1, r.stationOpposite(s2));
    }

    @Test
    void possibleClaimCardsWorksOnColorOvergroundRoad() {

        Station s1 = ChMap.stations().get(0);
        Station s2 = ChMap.stations().get(1);
        Route r = new Route ("ID", s1, s2,5, Route.Level.OVERGROUND, Color.ORANGE );

        List<SortedBag<Card>> possibleCard = r.possibleClaimCards();
        List<SortedBag<Card>> expectedCard = new ArrayList<>();

        expectedCard.add(SortedBag.of(5, Card.ORANGE));

        for(int i = 0; i<possibleCard.size(); i++){
            assertEquals(possibleCard.get(i), expectedCard.get(i));
        }

    }

    @Test
    void possibleClaimCardsWorksOnNonColorOvergroundRoad() {

        Station s1 = ChMap.stations().get(0);
        Station s2 = ChMap.stations().get(1);
        Route r = new Route ("ID", s1, s2,5, Route.Level.OVERGROUND, null);

        List<Card> allCards = Card.CARS;
        List<SortedBag<Card>> possibleCard = r.possibleClaimCards();
        List<SortedBag<Card>> expectedCard = new ArrayList<>();

        for (Card card : allCards){
            expectedCard.add(SortedBag.of(5, card));
        }

        for(int i = 0; i<possibleCard.size(); i++){
            assertEquals(possibleCard.get(i), expectedCard.get(i));
        }

    }

    @Test
    void additionalClaimCardsCountWorksOnUndergroundRoad() {
        SortedBag.Builder d = new SortedBag.Builder();
        d.add(Card.LOCOMOTIVE);
        d.add(Card.BLACK);
        d.add(Card.BLUE);
        SortedBag<Card> draw = d.build();

        SortedBag.Builder c = new SortedBag.Builder();
        c.add(Card.LOCOMOTIVE);
        c.add(Card.BLACK);
        SortedBag<Card> claim = c.build();

        int expectedCards = 2;
        assertEquals(expectedCards, ChMap.routes().get(0).additionalClaimCardsCount(claim, draw));
    }

    @Test
    void additionalClaimCardsCountWorksOnlyLocomotives() {
        SortedBag.Builder d = new SortedBag.Builder();
        d.add(Card.LOCOMOTIVE);
        d.add(Card.LOCOMOTIVE);
        d.add(Card.LOCOMOTIVE);
        SortedBag<Card> draw = d.build();

        SortedBag.Builder c = new SortedBag.Builder();
        c.add(Card.LOCOMOTIVE);
        c.add(Card.BLACK);
        SortedBag<Card> claim = c.build();

        int expectedCards = 3;
        assertEquals(expectedCards, ChMap.routes().get(0).additionalClaimCardsCount(claim, draw));
    }

    @Test
    void additionalClaimCardsCountWorksOnlyLocomotivesInPlayerHand() {
        SortedBag.Builder d = new SortedBag.Builder();
        d.add(Card.BLUE);
        d.add(Card.BLUE);
        d.add(Card.BLUE);
        SortedBag<Card> draw = d.build();

        SortedBag.Builder c = new SortedBag.Builder();
        c.add(Card.LOCOMOTIVE);
        c.add(Card.LOCOMOTIVE);
        SortedBag<Card> claim = c.build();

        int expectedCards = 0;
        assertEquals(expectedCards, ChMap.routes().get(0).additionalClaimCardsCount(claim, draw));
    }

    @Test
    void additionalClaimCardsCountWorksOnNonUndergroundRoad() {
       /* assertThrows(IllegalArgumentException.class, () -> {
            ChMap.routes().get(3).additionalClaimCardsCount();
        });*/
    }

    @Test
    void additionalClaimCardsCountWorksOnFalseArgument() {

        SortedBag.Builder d = new SortedBag.Builder();
        d.add(Card.BLUE);
        d.add(Card.BLUE);
        d.add(Card.BLUE);
        d.add(Card.BLUE);
        SortedBag<Card> draw = d.build();

        SortedBag.Builder c = new SortedBag.Builder();
        c.add(Card.LOCOMOTIVE);
        c.add(Card.LOCOMOTIVE);
        SortedBag<Card> claim = c.build();

        assertThrows(IllegalArgumentException.class, () -> {
            ChMap.routes().get(0).additionalClaimCardsCount(claim, draw);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ChMap.routes().get(0).additionalClaimCardsCount(draw, claim);
        });
    }
    @Test
    void claimPointsWorksForNonEmptyRoad() {

        Station s1 = ChMap.stations().get(0);
        Station s2 = ChMap.stations().get(1);
        Route r = new Route ("ID", s1, s2,5, Route.Level.OVERGROUND, null);

        int normalPoints = 10;

        assertEquals(normalPoints, r.claimPoints());
    }
}