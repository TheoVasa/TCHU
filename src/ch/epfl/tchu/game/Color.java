package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumeration for the different color of the cards of the game
 *
 * @author Theo Vasarino (313191)
 */
public enum Color {

    /**
     * Color used in the game, with a given order
     */

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * Attributes
     */
    public static final List<Color> ALL = List.of(Color.values());
    public static final int COUNT = ALL.size();
}
