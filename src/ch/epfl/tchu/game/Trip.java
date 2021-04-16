package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represent a trip between two stations.
 * There are 3 type of trips:
 * From town to town, from town to country and from country to country.
 * <p>
 * This class is public, final and immutable.
 *
 * @author Theo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public final class Trip {

    //The starting station of the trip.
    final private Station from;
    //The final station of the trip.
    final private Station to;
    //The amount of points the trip gives to the player
    final private int points;

    /**
     * Create a trip with the given starting and final station of the trip
     * and the points it can give to the player.
     *
     * @param from   the starting station of the trip
     * @param to     the final station of the trip
     * @param points the amount of points the trip provides
     * @throws IllegalArgumentException if <code>points</code> is not strictly positive
     * @throws NullPointerException     if <code>from</code> of <code>to</code> is null
     */
    public Trip(Station from, Station to, int points) {
        //Check the correctness of the arguments
        Preconditions.checkArgument(points > 0);

        //Init the attributes
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Gives all possible trips between all the given stations.
     *
     * @param from   the list of the starting station of the trips
     * @param to     the list of the final station of the trips
     * @param points the amount of points each trip provides
     * @return the list of each possible trip
     * @throws IllegalArgumentException if <code>from</code> of <code>to</code> is empty
     *                                  or if <code>points</code> is not strictly positive
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        //Check the correctness of the arguments
        Preconditions.checkArgument(points > 0 && !from.isEmpty() && !to.isEmpty());

        //Find all the possible trips
        List<Trip> allTrips = new ArrayList<>();
        for (Station a : from) {
            for (Station b : to)
                allTrips.add(new Trip(a, b, points));
        }
        return allTrips;
    }

    /**
     * Getter for the starting Station of the trip.
     *
     * @return the starting position of the trip (Station)
     */
    public Station from() {
        return from;
    }

    /**
     * Getter for the final station of the trip.
     *
     * @return the final station of the trip (Station)
     */
    public Station to() {
        return to;
    }

    /**
     * Getter for the amount of points the trips provides.
     *
     * @return the amount of points the trip provides (int)
     */
    public int points() {
        return points;
    }

    /**
     * Gives the amounts of points according of the given connectivity.
     *
     * @param connectivity the connection between two stations
     * @return the amount of points for the given connection, negative if the stations are not connected (int)
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to)
                ? points
                : points * -1;
    }
}