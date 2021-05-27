package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * This class represent the ticket of a trip.
 * It is public, final, immutable and implements Comparable.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class Ticket implements Comparable<Ticket> {

    //The trips of the tickets (contains one element if the ticket is town to town)
    private final List<Trip> trips;
    //The text of the ticket (from - to (points))
    private final String text;

    /**
     * Create a ticket with all the given trips.
     *
     * @param trips list of all the trips that requires a ticket
     * @throws IllegalArgumentException if <code>trips</code> is empty or
     *                                  if the starting station of all the trips are not the same
     */
    public Ticket(List<Trip> trips) {
        //Check the correctness of the arguments
        Preconditions.checkArgument(trips.size() > 0);
        for (int i = 1; i < trips.size(); ++i)
            Preconditions.checkArgument(trips.get(i).from().toString().equals(trips.get(i - 1).from().toString()));

        //Init the attributes
        text = computeText(trips);
        this.trips = List.copyOf(trips);
    }

    /**
     * Create a ticket with a single trip.
     *
     * @param from   the starting station of the trip
     * @param to     the final station of the trip
     * @param points the amount of points the trip provides
     */
    public Ticket(Station from, Station to, int points) {
        this(Collections.singletonList(new Trip(from, to, points)));
    }


    //Build the text of the ticket.
    private static String computeText(List<Trip> trips) {
        String from = trips.get(0).from().name();
        TreeSet<String> destinations = new TreeSet<>();

        //Get all the possible destinations (useful for neighbor countries)
        for (Trip t : trips) {
            String strDestination = new StringBuilder()
                    .append(t.to().name())
                    .append(" (")
                    .append(t.points())
                    .append(")")
                    .toString();
            destinations.add(strDestination);
        }

        //Assemble the text considering if its a town-town ticket or town-country/country-country ticket
        return (destinations.size() > 1)
                ? String.format("%s - {%s}", from, String.join(", ", destinations))
                : String.format("%s - %s", from, String.join(", ", destinations));
    }


    /**
     * Getter for the text of the ticket.
     *
     * @return a text with the trip information of <code>this</code> (String)
     */
    public String text() {
        return text;
    }

    /**
     * Gives the amount of point that the player win/lose with this ticket.
     * Depends on the stations that the player managed to connect.
     *
     * @param connectivity the connectivity of the trip
     * @return the amount of points the player win/lose depending on <code>connectivity</code> (int)
     */
    public int points(StationConnectivity connectivity) {
        //We take the highest score if the ticket is of type town-country or country-country
        int points = trips.get(0).points(connectivity);
        for (Trip t : trips) {
            if (points < t.points(connectivity))
                points = t.points(connectivity);
        }
        return points;
    }

    @Override
    public int compareTo(Ticket ticket) {
        return text.compareTo(ticket.toString());
    }

    @Override
    public String toString() {
        return text;
    }
}
