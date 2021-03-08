package ch.epfl.tchu.game;

/**
 * Station that the player manage to connect
 *
 * @author Theo Vasarino (313191)
 */
public interface StationConnectivity {
    /**
     * Determine if two given station are connected
     * @param s1 first station
     * @param s2 second station
     * @return if the station are connected
     */
    public boolean connected(Station s1, Station s2);
}
