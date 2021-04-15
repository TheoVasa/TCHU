package ch.epfl.tchu;

/**
 * Help to handle the errors that may occur in the game, used to check some necessary conditions before using a method
 *
 * @author Theo Vasarino (313191)
 */
public final class Preconditions {

    /**
     * Constructor (private to avoid to create an instance of this class)
     */
    private Preconditions() {
    }

    /**
     * Throws a IllegalArgumentException
     * @param shouldBeTrue the boolean value of argument we want to check
     * @throws IllegalArgumentException if the boolean value of argument we want to check is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
