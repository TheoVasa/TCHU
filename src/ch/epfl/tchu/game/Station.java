package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * The station of the game, public, final, immutable
 *
 * @author Theo Vasarino (313191)
 */
public final class Station {

    /**
     * Attributes
     */
    private final String name;
    private final int id;

    /**
     * Constructor
     * @param name the name of the station
     * @param id the id of the station
     */
    public Station(int id, String name) {
        //Check the correctness of the arguments
        Preconditions.checkArgument( id >= 0);

        //Init the attributes
        this.name = name;
        this.id = id;
    }

    /**
     * Getter for the name of the station
     * @return the name of the station (String)
     */
    public String name() {
        return name;
    }

    /**
     * Getter for the id of the station
     * @return the id of the station (int)
     */
    public int id() {
        return id;
    }

    @Override
    public String toString(){
        return name;
    }
}
