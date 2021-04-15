package ch.epfl.tchu.game;

/**
 * Represent the "connectivity network" of the player
 *
 * @author Theo Vasarino (313191)
 */
public interface StationConnectivity {
    /**
     * Determine if two given stations are connected
     * @param s1 first station
     * @param s2 second station
     * @return true if the station are connected (boolean)
     */
    public boolean connected(Station s1, Station s2);
}
