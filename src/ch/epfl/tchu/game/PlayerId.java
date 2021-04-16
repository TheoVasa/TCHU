package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enum representing the identity of the players.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public enum PlayerId {
    PLAYER_1, PLAYER_2;

    /**
     * Attributes
     */
    //the two players
    public final static List<PlayerId> ALL = List.of(PlayerId.values());
    //number of players
    public final static int COUNT = ALL.size();

    /**
     * Gives the PlayerId of the next Player
     * @return the PlayerId of the next player in the enum. (PlayerId)
     */
    public PlayerId next() {
        return ALL.get((ALL.indexOf(this) + 1) % ALL.size());
    }
}
