package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent the public states of a player.
 * It is public and immutable.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 **/

public class PublicPlayerState {
    //the route the player claimed
    private final List<Route> routes;
    //his number of tickets
    private final int ticketCount;
    //his number of cards
    private final int cardCount;
    //his left number of cars
    private final int carCount;
    //how many claim points he have
    private final int claimPoints;

    /**
     * Construct a new PublicPlayerState from a given number of tickets, a given number of card and a list of claimed Routes.
     *
     * @param ticketCount the number of tickets of the player.
     * @param cardCount   the number of cards of the player.
     * @param routes      the  list of all the routes of the player.
     * @throws IllegalArgumentException if <code>ticketCount</code> and <code>cardCount</codes> are strictly negative.
     */
    public PublicPlayerState(int ticketCount, int cardCount,
                             List<Route> routes) {
        //Check correctness of arguments
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        //Create a copy of the list of Route given in parameters
        this.routes = new ArrayList<>(routes);

        //Init vars
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.carCount = initCarsCount();
        this.claimPoints = initClaimPoints();
    }

    /**
     * The amount of tickets of the player.
     *
     * @return the number of tickets. (int)
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * The amount of cards of a player.
     *
     * @return the number of cards. (int)
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * The routes of the player.
     *
     * @return the routes of the player. (List)
     */
    public List<Route> routes() {
        return new ArrayList<>(routes);
    }

    /**
     * The amount of cars of the player.
     *
     * @return the number of cars. (int)
     */
    public int carCount() {
        return carCount;
    }

    /**
     * The amount of the claim point of a player.
     *
     * @return the amount of claim points. (int)
     */
    public int claimPoints() {
        return claimPoints;
    }


    //initialize the cars count
    private int initCarsCount() {
        int playedCar = 0;
        for (Route r : routes)
            playedCar += r.length();
        return Constants.INITIAL_CAR_COUNT - playedCar;
    }

    //Initialize the total claimed points of the player
    private int initClaimPoints() {
        int points = 0;
        for (Route r : routes)
            points += Constants.ROUTE_CLAIM_POINTS.get(r.length());
        return points;
    }
}
