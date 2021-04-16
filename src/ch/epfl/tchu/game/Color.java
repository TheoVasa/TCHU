package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumeration for the different color of the cards of the game.
 *
 * @author Theo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public enum Color {

    //The elements of the enumeration (order important).
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * The list of all the colors (in the correct order) of <code>this</code> enumeration.
     */
    public static final List<Color> ALL = List.of(Color.values());

    /**
     * The amount of colors of <code>this</code> enumeration.
     */
    public static final int COUNT = ALL.size();
}
