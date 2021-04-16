package ch.epfl.tchu;

/**
 * Help to handle the errors that may occur in the game, used to check some necessary conditions before using a method.
 *
 * @author Theo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Throws an IllegalArgumentException if <code>shouldBeTrue</code> is false,
     * if <code>shouldBeTrue</code> is true do nothing
     *
     * @param shouldBeTrue the boolean value of argument we want to check
     * @throws IllegalArgumentException if the boolean value of <code>shouldBeTrue</code> is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
