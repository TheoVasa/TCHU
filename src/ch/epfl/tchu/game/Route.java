package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;


/**
 * Represent a route in the game, public, final, immutable
 *
 * @author Théo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public final class Route {

    /**
     * Attributes
     */
    private final Station station1;
    private final Station station2;
    private final String id;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Constructor
     * @param id the id of the road
     * @param station1 the first station of the road
     * @param station2 the second station of the road
     * @param length the length of the road
     * @param level Overground or underground (tunnel)
     * @param color color of the wagon needed to complete the road
     * @throws NullPointerException if one of the stations, the level or the id is null
     * @throws IllegalArgumentException if station1 equal station2 or if the length doesn't respect the standards given by ch.epfl.tchu.game.Constants
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

        //Check Precondition on length and stations
        Preconditions.checkArgument(!(station1.equals(station2)));
        Preconditions.checkArgument(length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH);

        //Check NullPointerException or init vars
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.level = Objects.requireNonNull(level);
        this.id = Objects.requireNonNull(id);

        //Init vars length color
        this.length = length;
        this.color = color;
    }

    /**
     * Getter for the id
     * @return the id of the road (String)
     */
    public String id(){
        return id;
    }

    /**
     * Getter for the first station
     * @return the first station (Station)
     */
    public Station station1(){
        return station1;
    }

    /**
     * Getter for second station
     * @return the second station (Station)
     */
    public Station station2(){
        return station2;
    }

    /**
     * Getter for the length of the road
     * @return the length of the road (int)
     */
    public int length(){
        return length;
    }

    /**
     * Getter for the level of the road
     * @return the level of the road (Level)
     */
    public Level level(){
        return level;
    }

    /**
     * Getter for the color of the road
     * @return the color of the road (Color)
     */
    public Color color(){
        return color;
    }
    /**
     * Gives the two stations of the road in the input order
     * @return a list of the two station of the road (List<Station>)
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Gives the opposite station of a road
     * @param station of the road
     * @return the other station of the road (Station)
     * @throws IllegalArgumentException if the given station don't belong to the route
     */
    public Station stationOpposite(Station station){
        //Check if the given station is either station1 or station2
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));
        //Return the opposite station
        return station.equals(station1) ? station2 : station1;
    }

    /**
     * Gives all the possible cards a player can play to claim the road
     * @return a list of all the possible card a player can play (ArrayList<SortedBad<Card>>())
     */
    public List<SortedBag<Card>> possibleClaimCards(){
        List<SortedBag<Card>> allPossibleClaimCards = new ArrayList<>();
        //check the color of the road
        List<Card> listOfPlayableCars = (color != null)
                ? Collections.singletonList(Card.of(color))
                : Card.CARS;

        //Generate a list of all possible claim cards depending the level of the road
        if (level.equals(Level.UNDERGROUND)){
            for (int numberOfLoco = 0; numberOfLoco <= length; ++numberOfLoco) {
                for (Card card : listOfPlayableCars) {
                    allPossibleClaimCards.add(SortedBag.of(length - numberOfLoco, card, numberOfLoco, Card.LOCOMOTIVE));

                    //get out of the loop when we add full loco
                    if(numberOfLoco==length)
                        break;
                }
            }
        } else if (level.equals(Level.OVERGROUND)){
            for (Card card : listOfPlayableCars){
                allPossibleClaimCards.add(SortedBag.of(length, card));
            }
        }
        return allPossibleClaimCards;
    }


    /**
     * Use only for underground levels - gives the additional cards a player has to play to claim the road
     * @param claimCards the cards the player has played
     * @param drawnCards the cards of the deck
     * @return the additional cards the player must play to claim the road (int)
     * @throws IllegalArgumentException if the Route level isn't underground
     * @throws IllegalArgumentException if the number of drawnCards doesn't respect the standards given by ch.epfl.tchu.game.Constants
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        //Check if the road is indeed an underground road
        Preconditions.checkArgument(level.equals(Level.UNDERGROUND));
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        //Count the number of the additional card
        int additionalClaimCardsCount = 0;
        for (Card d : drawnCards){
            if (claimCards.contains(d) || d.equals(Card.LOCOMOTIVE))
                additionalClaimCardsCount++;
        }
        return additionalClaimCardsCount;
    }

    /**
     * Return the amount of point given by the road
     * @return the points given by the road (int)
     */
    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }


    /**
     * Enumeration for the level of the road (UNDERGROUND or OVERGROUND)
     */
    public enum Level{
        OVERGROUND,
        UNDERGROUND
    }

}
