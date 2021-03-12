package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import javax.print.DocFlavor;
import java.util.List;

/**
 * this class is use to generate all type of text during the game, final, immutable
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class Info {

    /**
     * attributs
     */
    private final String playerName;

    /**
     * constructor
     * @param playerName of the player associate to the info we want generate
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * generate the name of the given card
     * @param card we want print
     * @param count 0 if plural, 1 if not
     * @return the name of the card
     */
    public static String cardName(Card card, int count){
        String name = "";

        switch(card) {

        case BLACK:
            name = StringsFr.BLACK_CARD;
            break;

        case VIOLET:
            name = StringsFr.VIOLET_CARD;
            break;

        case BLUE:
            name = StringsFr.BLUE_CARD;
            break;

        case GREEN:
            name = StringsFr.GREEN_CARD;
            break;

        case YELLOW:
            name = StringsFr.YELLOW_CARD;
            break;

        case ORANGE:
            name = StringsFr.ORANGE_CARD;
            break;

        case RED:
            name = StringsFr.RED_CARD;
            break;

        case WHITE:
            name = StringsFr.WHITE_CARD;
            break;

        case LOCOMOTIVE:
            name = StringsFr.LOCOMOTIVE_CARD;
            break;

        default:
            throw new IllegalArgumentException();
        }

        return name + generatePlural(count);
    }

    /**
     * generate a text when the two players finish the game with a draw
     * @param playerNames of the two players
     * @param points of the players
     * @return a draw text
     */
    public static String draw(List<String> playerNames, int points){
        String players = generatePlayersNames(playerNames);
        return String.format(StringsFr.DRAW, players, points);
    }

    /**
     * generate a text saying that the player will play first
     * @return the text
     */
    public String willPlayFirst(){
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * generate a text saying how many tickets the player decide to keep
     * @param count the number of tickets the player keep
     * @return the text
     */
    public String keptTickets(int count){
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, generatePlural(count));
    }

    /**
     * generate a text saying that the player can play
     * @return the text
     */
    public String canPlay(){
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * generate a text saying how much tickets taked by the player
     * @param count the number of tickets the player draw
     * @return the text
     */
    public  String drewTickets(int count){
        return String.format(StringsFr.DREW_TICKETS, playerName, count, generatePlural(count));
    }

    /**
     * generate a text saying that the player drew a blind card
     * @return the text
     */
    public String drewBlindCard(){
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * generate a text saying that the player decided to drew a given visible card
     * @param card the player decided the drew
     * @return the text
     */
    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * generate a text saying that the player just claimed a new route
     * @param route the player just get
     * @param cards the player taked the route with
     * @return the text
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, generateRouteName(route), generateListOfCard(cards));
    }

    /**
     * generate a text saying that the player just claimed a new tunnel
     * @param route the player just get
     * @param cards the player taked the tunnel with
     * @return the text
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> cards){
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, generateRouteName(route), generateListOfCard(cards));
    }

    /**
     * Generate a text saying that the player draw cards and saying the additional cost it implies
     * @param drawnCards the cards drawn from the deck by the player
     * @param additionalCost made by the drawnCards
     * @return the text
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String text = String.format(StringsFr.ADDITIONAL_CARDS_ARE, generateListOfCard(drawnCards));
        return (additionalCost==0) ? text + StringsFr.NO_ADDITIONAL_COST : text + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, generatePlural(additionalCost));
    }

    /**
     * Generate a text saying that the player dont want to claim the given route
     * @param route the player don't want to claim
     * @return the text
     */
    public String didNotClaimRoute (Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, generateRouteName(route));
    }

    /**
     * Generate a text saying that it left carCount cars for the player and that the last turn begin
     * @param carCount number of cars left for the player
     * @return the text
     */
    public String lastTurnBegins (int carCount){
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount,generatePlural(carCount));
    }

    /**
     * Generate a text saying that the player win the bonus with the given longestTrail
     * @param longestTrail of the game
     * @return the text
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        String trailName = longestTrail.station1().name() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2().name();
        return String.format(StringsFr.GETS_BONUS, playerName, trailName);
    }

    /**
     * generate the ending text announcing the winner, the loser and the points
     * @param points of the winner
     * @param loserPoints of the loser
     * @return the text
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS, playerName, points, generatePlural(points), loserPoints, generatePlural(loserPoints));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * private methods
     */
    /**
     * method generating the names of the players with "et" between
     * @param playerNames we want to generate
     * @return the text
     */
    private static String generatePlayersNames(List<String> playerNames){
        String names = "";
        int counter =0;

        for (String player : playerNames){

            names = names + player;
            counter++;
            if(counter== playerNames.size())
                break;
            else names = names + StringsFr.AND_SEPARATOR;
        }
        return names;
    }

    /**
     * method generating the end of words depending if there is plural or not
     * @param i number of objects
     * @return "s" or not at the end of words
     */
    private static String generatePlural(int i){
        if (i==1 || i==-1){
            return StringsFr.plural(1);
        } else return StringsFr.plural(0);
    }

    /**
     * method generating the name of the cards in the right order given by Class.Card, with "," between each names and "et" for the last one
     * @param cards we want to generate
     * @return the text
     */
    private static String generateListOfCard(SortedBag<Card> cards){
        String listOfCard = "";
        int counter=0;

        for(Card c : Card.ALL){
            if(cards.contains(c)){
                int numberOfThisCard = cards.countOf(c);
                listOfCard = listOfCard + numberOfThisCard + " " + cardName(c, numberOfThisCard);
                counter++;

                if(counter==cards.toSet().size()-1) listOfCard = listOfCard + StringsFr.AND_SEPARATOR;
                else if(counter==cards.toSet().size()) break;
                else  listOfCard = listOfCard + ", ";
            }
        }
        return listOfCard;
    }

    /**
     * generate the name of the with the form "station1 + - + station2"
     * @param route we want to generate
     * @return the text
     */
    private static String generateRouteName(Route route){
        return route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2();
    }
}