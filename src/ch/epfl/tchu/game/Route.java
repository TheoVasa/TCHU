package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;


/**
 * This class represent a route between two stations.
 * It is public, final and immutable.
 *
 * @author Théo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public final class Route {

    //First station of the route
    private final Station station1;
    //Second station of the route
    private final Station station2;
    //The id of the route
    private final String id;
    //The length of the route
    private final int length;
    //The level of the route
    private final Level level;
    //The color of the route (null if no color)
    private final Color color;


    /**
     * Enumeration for the level of the route (UNDERGROUND or OVERGROUND).
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    /**
     * Create a route.
     *
     * @param id       the id of the route
     * @param station1 the first station of the route
     * @param station2 the second station of the route
     * @param length   the length of the route
     * @param level    le level of the route (overground or underground)
     * @param color    color of the route (can be null if no color)
     * @throws NullPointerException     if <code>station1</code>, <code>station2</code>,
     *                                  <code>level</code> or <code>id</code> is null
     * @throws IllegalArgumentException if <code>station1</code> equals <code>station2</code>
     *                                  or if length is not between 1 and 6
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

        //Check NullPointerException or init vars
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.level = Objects.requireNonNull(level);
        this.id = Objects.requireNonNull(id);

        //Check Precondition on length and stations
        Preconditions.checkArgument(!(station1.equals(station2)));
        Preconditions.checkArgument(length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH);

        //Init vars length color
        this.length = length;
        this.color = color;
    }

    /**
     * Getter for the id.
     *
     * @return the id of the route (String)
     */
    public String id() {
        return id;
    }

    /**
     * Getter for the first station.
     *
     * @return the first station (Station)
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter for second station.
     *
     * @return the second station (Station)
     */
    public Station station2() {
        return station2;
    }

    /**
     * Getter for the length of the route.
     *
     * @return the length of the route (int)
     */
    public int length() {
        return length;
    }

    /**
     * Getter for the level of the route.
     *
     * @return the level of the route (Level)
     */
    public Level level() {
        return level;
    }

    /**
     * Getter for the color of the route.
     *
     * @return the color of the route (Color)
     */
    public Color color() {
        return color;
    }

    /**
     * Gives an ordered list with the two station of the route
     * (same order as given in the constructor).
     *
     * @return a list with the two station of the route (List< Station >)
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Gives the opposite station of the route.
     *
     * @param station a station of the route
     * @return the other station of the route (Station)
     * @throws IllegalArgumentException if <code>station</code> is not a station of the route
     */
    public Station stationOpposite(Station station) {
        //Check if the given station is either station1 or station2
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));
        //Return the opposite station
        return station.equals(station1) ? station2 : station1;
    }

    /**
     * Gives all the possible sets of cards a player could play to claim the route.
     *
     * @return a list of all the possible sets of cards to claim the route (List< SortedBag< Card >>)
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        //List using SortedBag.Builder to creat a list of a list of all possible ClaimCards
        List<SortedBag<Card>> allPossibleClaimCards = new ArrayList<>();
        //SortedBag.Builder singlePossibleClaimCards = new SortedBag.Builder();
        List<Card> listOfPlayableCars = (color != null) ? Collections.singletonList(Card.of(color)) : Card.CARS;

        //Generate a list of a list of all possible sets of playable cards
        if (level.equals(Level.UNDERGROUND)) {
            for (int numberOfLoco = 0; numberOfLoco <= length; ++numberOfLoco) {
                for (Card card : listOfPlayableCars) {
                    allPossibleClaimCards.add(SortedBag.of(length - numberOfLoco, card, numberOfLoco, Card.LOCOMOTIVE));

                    //get out of the loop when we have added full loco
                    if (numberOfLoco == length)
                        break;
                }
            }
        } else if (level.equals(Level.OVERGROUND)) {
            for (Card card : listOfPlayableCars) {
                allPossibleClaimCards.add(SortedBag.of(length, card));
            }
        }

        return allPossibleClaimCards;
    }


    /**
     * Use only for underground levels:
     * gives the additional cards a player has to play to claim the route
     * (depending on his previously played cards and the drawn cards of the deck).
     *
     * @param claimCards the cards the player has played
     * @param drawnCards the 3 top cards of the deck
     * @return the additional cards the player must play to claim the route (int)
     * @throws IllegalArgumentException if the level of the route is underground
     *                                  and if the size of <code>drawnCards</code> is not equal 3
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        //Check if the route is indeed an underground route
        Preconditions.checkArgument(level.equals(Level.UNDERGROUND));
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        //If the player has more or less cards in front than possible
        Preconditions.checkArgument(claimCards.size() >= Constants.MIN_ROUTE_LENGTH &&
                claimCards.size() <= Constants.MAX_ROUTE_LENGTH);

        //Count the number of the additional card
        int additionalClaimCardsCount = 0;
        for (Card d : drawnCards) {
            if (claimCards.contains(d) || d.equals(Card.LOCOMOTIVE))
                additionalClaimCardsCount++;
        }
        return additionalClaimCardsCount;
    }

    /**
     * Return the amount of point given by the route.
     *
     * @return the points given by the route (int)
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
}
