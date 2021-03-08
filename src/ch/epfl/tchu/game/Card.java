package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumeration of the different cards of the game
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    /**
     * Attributes
     */
    private final Color colorType;
    private static final Card tabOfEnum[]       = Card.values();
    private static final int locoPositionInEnum = Card.LOCOMOTIVE.ordinal();
    public static final List<Card> ALL          = List.of(tabOfEnum);
    public static final List<Card> CARS        = ALL.subList(0, locoPositionInEnum);
    public static final int COUNT               = ALL.size();

    /**
     * Constructor
     * @param color the color of the card
     */
    Card(Color color) {
        colorType = color;
    }

    /**
     * Getter of the color of the card
     * @return the color of the card
     */
    public Color color() {
        return colorType;
    }

    /**
     * Get the card type according by a given color
     * @param color color
     */
    public static Card of(Color color) {
        for (int i = 0; i < COUNT; ++i) {
            if (tabOfEnum[i].color() == color)
                return tabOfEnum[i];
        }
        return null;
    }
}
