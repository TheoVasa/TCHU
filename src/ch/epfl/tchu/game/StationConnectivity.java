package ch.epfl.tchu.game;

/**
 * Station that the player manage to connect.
 *
 * @author Theo Vasarino (313191)
 */
public interface StationConnectivity {
    /**
     * Determine if two given station are connected by a players trail.
     *
     * @param s1 first station
     * @param s2 second station
     * @return true iff the station are connected (boolean)
     */
    boolean connected(Station s1, Station s2);
}
