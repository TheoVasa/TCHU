package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main (String[] args){

        Card card = Card.BLACK;


        //######## Si tu change le nb de point de trip3 (different que trip2) alors le text du ticket sera faux...
        Trip trip1 = new Trip(new Station(10, "Lausanne"), new Station(11, "France"), 10);
        Trip trip2 = new Trip(new Station(10, "Lausanne"), new Station(5, "Allemagne"), 5);
        Trip trip3 = new Trip(new Station(10, "Lausanne"), new Station(13, "Allemagne"), 5);


        List<Trip> trips = new ArrayList<>();
        trips.add(trip1);
        trips.add(trip2);
        trips.add(trip3);

        Ticket ticket = new Ticket(trips);

        System.out.println(ticket.text());

        for (int i = 0; i < ChMap.routes().get(55).possibleClaimCards().size(); ++i)
            System.out.println(ChMap.routes().get(55).possibleClaimCards().get(i).toString());

        SortedBag.Builder d = new SortedBag.Builder();
        d.add(Card.LOCOMOTIVE);
        d.add(Card.BLACK);
        d.add(Card.BLUE);
        SortedBag<Card> draw = d.build();

        SortedBag.Builder c = new SortedBag.Builder();
        c.add(Card.LOCOMOTIVE);
        c.add(Card.BLACK);
        SortedBag<Card> claim = c.build();

        System.out.println(ChMap.routes().get(0).additionalClaimCardsCount(claim, draw));
        List<Route> routes = new ArrayList<>();
        /*for (int i = 0; i < 80; ++i){
            routes.add(ChMap.routes().get(i));
        }*/
        routes.add(ChMap.routes().get(66));
        routes.add(ChMap.routes().get(65));
        routes.add(ChMap.routes().get(19));
        routes.add(ChMap.routes().get(16));
        routes.add(ChMap.routes().get(18));
        routes.add(ChMap.routes().get(14));

        Trail t = Trail.longest(routes);
        System.out.println(t.toString());

        System.out.println(Info.cardName(Card.BLUE, 1));
        System.out.println(Info.cardName(Card.BLUE, 0));

        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(2, Card.BLACK);
        builder.add(1, Card.BLUE);
        builder.add(4, Card.VIOLET);
        builder.add(100, Card.LOCOMOTIVE);

        SortedBag<Card> listOfcard = builder.build();

        String playername ="PLAYER";

        Info infplayer = new Info(playername);
        System.out.println(infplayer.claimedRoute(ChMap.routes().get(10), listOfcard));


    }
}
