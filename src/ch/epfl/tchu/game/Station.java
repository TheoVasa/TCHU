package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * This class represents the station of the game.
 * It is final and immutable.
 *
 * @author Theo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public final class Station {

    /**
     * The name of the station
     */
    private final String name;

    /**
     * The id of the station
     */
    private final int id;

    /**
     * Create a new station with the given id and name
     *
     * @param name the name of the station
     * @param id   the id of the station
     * @throws IllegalArgumentException if the <code>id</code> is strictly negative
     */
    public Station(int id, String name) {
        //Check the correctness of the arguments
        Preconditions.checkArgument(id >= 0);

        //Init the attributes
        this.name = name;
        this.id = id;
    }

    /**
     * Getter for the name of the station.
     *
     * @return the name of the station (String)
     */
    public String name() {
        return name;
    }

    /**
     * Getter for the id of the station.
     *
     * @return the id of the station (int)
     */
    public int id() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
