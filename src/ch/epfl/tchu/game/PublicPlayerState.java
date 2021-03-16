package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PublicPlayerState {

    /**
     * Attributes
     */
    private final List<Route> routes;
    private final int ticketCount;
    private final int cardCount;
    private final int carCount;
    private final int claimPoints;


    /**
     * Constructor
     * @param ticketCount the number of tickets of the player
     * @param cardCount the number of cards of the player
     * @param routes a list of all the routes of the player
     * @throws IllegalArgumentException if ticketCount and cardCount are negative
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        //Check correctness of arguments
        Preconditions.checkArgument(ticketCount>=0 && cardCount>=0);

        //Init vars
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.carCount = initCarsCount();
        this.claimPoints = initClaimPoints();


        //Create a copy of the list of Rout given in parameter
        this.routes = new ArrayList<>(routes);
    }

    /**
     * Initialize cars count of the player
     * @return number of remaining cars
     */
    private int initCarsCount(){
        int playedCar = 0;
        for (Route r: routes)
            playedCar+=r.length();
        return Constants.INITIAL_CAR_COUNT - playedCar;
    }

    /**
     * Initialize the total claimed points of the player
     * @return the claim points
     */
    private int initClaimPoints(){
        int points = 0;
        for (Route r: routes)
            points += Constants.ROUTE_CLAIM_POINTS.get(r.length());
        return points;
    }

    /**
     * Getter for the amount of tickets of the player
     * @return the number of tickets
     */
    public int ticketCount(){
       return ticketCount;
    }

    /**
     * Getter for the amount of cards of a player
     * @return the number of cards
     */
    public int cardCount(){
        return cardCount;
    }

    /**
     * Getter for the routes of the player
     * @return the routes of the player
     */
    public List<Route> routes(){
        return routes;
    }

    /**
     * Getter for the amount of cars of the player
     * @return the number cars
     */
    public int carCount(){
        return carCount;
    }

    /**
     * Getter for the amount of the claim point of a player
     * @return the amount of claim points
     */
    public int claimPoints(){
        return claimPoints;
    }
}
