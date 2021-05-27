package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enum representing the identity of the players.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public enum PlayerId {
    //The values of the enumeration
    PLAYER_1,
    PLAYER_2;

    /**
     * All the values of the enumeration (ordered)
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * The amount of elements in the enumeration
     */
    public final static int COUNT = ALL.size();

    /**
     * Gives the PlayerId of the next player.
     *
     * @return the PlayerId of the next player in the enum. (PlayerId)
     */
    public PlayerId next() {
        return ALL.get((this.ordinal()+1) % ALL.size());
    }
}
