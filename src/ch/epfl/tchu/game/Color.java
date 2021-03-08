package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumeration for the different color of the cards of the game
 *
 * @author Theo Vasarino (313191)
 */
public enum Color {

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
    private static final Color tabOfEnum[] = Color.values();
    public static final List<Color> ALL = List.of(tabOfEnum);
    public static final int COUNT = ALL.size();

}
