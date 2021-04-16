package ch.epfl.tchu.game;

import java.util.List;

/**
 * This class represent the enumeration of the different cards of the game.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public enum Card {

    //The elements of the enumeration (order important).
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    //The color of the card (can be null for locomotives).
    private final Color color;

    /**
     * The list of all the cards (in the correct order) of <code>this</code> enumeration.
     */
    public static final List<Card> ALL = List.of(Card.values());

    /**
     * The list of all the cards without the locomotive (only cars).
     */
    public static final List<Card> CARS = ALL.subList(0, Card.LOCOMOTIVE.ordinal());

    /**
     * The amount of cards.
     */
    public static final int COUNT = ALL.size();

    /**
     * Create a card associated to the given color.
     *
     * @param color the color of the card
     */
    Card(Color color) {
        this.color = color;
    }

    /**
     * Getter of the color of the card.
     *
     * @return the color of the card
     */
    public Color color() {
        return color;
    }

    /**
     * Get the card type corresponding to the given color.
     *
     * @param color the given color
     * @return the card corresponding to <code>color</code> (Card)
     */
    public static Card of(Color color) {
        Card correspondingCard = null;
        for (Card crd : ALL) {
            if (crd.color() == color)
                correspondingCard = crd;
        }
        return correspondingCard;
    }
}
