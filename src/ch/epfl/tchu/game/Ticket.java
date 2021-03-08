package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Ticket of a trip
 *
 * @author Selien Wicki (314357)
 */
public final class Ticket implements Comparable<Ticket> {

    /**
     * Attributes
     */
    private final List<Trip> trips;
    private final String text;

    /**
     * Constructor (principale)
     * @param trips list of all the trips that requires a ticket
     */
    public Ticket(List<Trip> trips){
        //Check the correctness of the arguments
        Preconditions.checkArgument(trips.size() > 0);
        for (int i = 1; i < trips.size(); ++i){
            Preconditions.checkArgument(trips.get(i).from().toString().equals(trips.get(i-1).from().toString()));
        }

        //Init the attributes
        text = computeText(trips);
        this.trips = trips;
    }

    /**
     * Constructor (secondaire)
     * @param from the starting station of the trip
     * @param to the final station of the trip
     * @param points the amount of points the trip provides
     */
    public Ticket(Station from, Station to, int points){
        this(Arrays.asList(new Trip(from, to, points)));
    }



    /**
     * Built the text of the ticket
     * @return the text of the ticket
     */
    private static String computeText(List<Trip> trips){
        TreeSet<String> destinations = new TreeSet<>();

        //Get all the possible destination (useful for neighbor countries
        for (Trip trip : trips) {
            destinations.add(trip.to().name() + " (" + trip.points() + ")");
        }

        //Assemble the text considering if its a town-town ticket or town-country/country-country ticket
        String finalText = "";
        if (destinations.size() > 1)
            finalText = String.format("%s - {%s}", trips.get(0).from().name(), String.join(", ", destinations));
        else if (destinations.size() == 1)
            finalText = String.format("%s - %s", trips.get(0).from().name(), destinations.first());

        return finalText;
    }


    /**
     * Getter for the trip name of the ticket
     * @return the trip information
     */
    public String text(){
        return text;
    }

    /**
     * Gives the amount of point that the player achieved with this ticket
     * @param connectivity the connectivity of the trip
     * @return the amount of point of the trip of the ticket that the player managed to connect
     */
    public int points(StationConnectivity connectivity){
        //We take the highest score if the ticket is of type town-country or country-country
        int points = trips.get(0).points(connectivity);
        for (Trip trip : trips){
            if (points < trip.points(connectivity))
                points = trip.points(connectivity);
        }
        return points;
    }

    @Override
    public int compareTo(Ticket ticket) {
        return text.compareTo(ticket.toString()) ;
    }

    @Override
    public String toString(){
        return text;
    }
}
