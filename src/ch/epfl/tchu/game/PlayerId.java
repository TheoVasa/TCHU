package ch.epfl.tchu.game;

import java.util.List;

/**
 * represent the identity of a player
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public enum PlayerId {
    PLAYER_1, PLAYER_2;

    /**
     * Attributes
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());
    public final static int COUNT = ALL.size();

    /**
     * Gives the PlayerId of the next Player
     *
     * @return the PlayerId of the next player of the list
     */
    public PlayerId next() {
        //Iterator<PlayerId> it = ALL.iterator();
        return ALL.get((ALL.indexOf(this) + 1) % ALL.size());
    }
}
