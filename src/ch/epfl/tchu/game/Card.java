package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumeration of the different cards of the game
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public enum Card {

    /**
     * the cards type, associate with their respecting color, ordered
     */

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
    public static final List<Card> ALL          = List.of(Card.values());
    public static final List<Card> CARS        = ALL.subList(0, Card.LOCOMOTIVE.ordinal());
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
     * @return the color of the card (Color)
     */
    public Color color() {
        return colorType;
    }

    /**
     * Get the card type according by the given color
     * @param color color
     */
    public static Card of(Color color) {
        for (int i = 0; i < COUNT; ++i) {
            if (ALL.get(i).color() == color)
                return ALL.get(i);
        }
        return null;
    }
}
