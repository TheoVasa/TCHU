package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class represent the whole state of a player.
 * It is public, final, immutable and extends PublicPlayerState.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class PlayerState extends PublicPlayerState {
    //the tickets of the player
    private final SortedBag<Ticket> tickets;
    //the cards of the player
    private final SortedBag<Card> cards;

    /**
     * Construct a new PlayerState, with tickets, given cards and given routes.
     *
     * @param tickets the tickets of the player.
     * @param cards   the remaining cards of the player (that he can play).
     * @param routes  the routes of the player (that he already claimed).
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.cards = SortedBag.of(cards);
        this.tickets = SortedBag.of(tickets);
    }

    /**
     * Initialize the initial state of the player, indeed the given initial cards has been distributed to the player, who's hasn't any route or tickets.
     *
     * @param initialCards the initial cards of the player.
     * @return the new PlayerState with the initial cards. (PlayerState)
     * @throws IllegalArgumentException if initial cards isn't equal to Constants.INITIAL_CARDS_COUNT.
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        //Check correctness of the arguments
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * The tickets of the player,
     *
     * @return the tickets of the player. (SortedBag)
     */
    public SortedBag<Ticket> tickets() {
        return SortedBag.of(tickets);
    }

    /**
     * Generate a new playerState with the new given tickets.
     *
     * @param newTickets the ticket to add to the player state.
     * @return a new player state with the added ticket. (PlayerState)
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        SortedBag.Builder<Ticket> newTicketsBuilder = new SortedBag.Builder<>();
        newTicketsBuilder.add(tickets);
        newTicketsBuilder.add(newTickets);

        return new PlayerState(newTicketsBuilder.build(), cards, routes());
    }

    /**
     * The cards of the player.
     *
     * @return the cards of the player. (SortedBag)
     */
    public SortedBag<Card> cards() {
        return SortedBag.of(cards);
    }

    /**
     * Generate a new PlayerState with the new given cards.
     *
     * @param card the new card we want to add to the player state.
     * @return a PlayerState with the added card. (PlayerState)
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag.Builder<Card> newCardsBuilder = new SortedBag.Builder<>();
        newCardsBuilder.add(cards);
        newCardsBuilder.add(card);

        return new PlayerState(tickets, newCardsBuilder.build(), routes());
    }
    /**
     * Determine if the player can claim a route depending on his state.
     *
     * @param route the route to check if it's claimable.
     * @return true if the route is claimable. (boolean)
     */
    public boolean canClaimRoute(Route route) {
        //Sufficient cars
        if (route.length() > carCount())
            return false;

        //Sufficient  cards
        for (SortedBag<Card> l : route.possibleClaimCards())
            if (cards.contains(l))
                return true;
        return false;
    }

    /**
     * Gives a multiset of all possible combinations of cards in order to claim the route.
     *
     * @param route the route to claim.
     * @return all the possible list of cards to claim the route. (List< SortedBag< Cards >>)
     * @throws IllegalArgumentException if the player don't have sufficient cars.
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        //Check correctness of the argument
        Preconditions.checkArgument(route.length() <= carCount());
        //Check if the player has the required cards
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        for (SortedBag<Card> cardList : route.possibleClaimCards()) {
            if (cards.contains(cardList))
                possibleClaimCards.add(cardList);
        }
        return possibleClaimCards;
    }

    /**
     * Gives all the possible combinations of additional cards a player can play to claim a underground route.
     *
     * @param additionalCardsCount number of additional cards to play.
     * @param initialCards         cards the player has played to claim the route.
     * @param drawnCards           the 3 cards that are drawn from the deck.
     * @return A List of all possible SortedBag of cards that can be played to claim the underground. (List)
     * @throws IllegalArgumentException if additionalCardsCount isn't bounded by 1 and Constants.ADDITIONAL_TUNNEL_CARDS (included)
     *                                  or if the initialCards is empty or contains more than 2 different type of cards
     *                                  or if the size of drawn cards isn't Constants.ADDITIONAL_TUNNEL_CARDS.
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        //Check correctness of the arguments
        Preconditions.checkArgument(1 <= additionalCardsCount
                && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(
                !initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(
                drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        //Variables required to list the additional cards
        SortedBag.Builder<Card> playableCards = new SortedBag.Builder<>();
        SortedBag<Card> newCards = cards.difference(initialCards);
        SortedBag.Builder<Card> additionalCard = new SortedBag.Builder<>();

        //Determine which cards can be played as additional cards
        for (Card c : drawnCards) {
            for (Card c2 : initialCards.toSet()) {
                if (c.equals(c2) || c.equals(Card.LOCOMOTIVE))
                    additionalCard.add(c2);
            }
        }
        for (Card c : newCards) {
            for (Card c2 : additionalCard.build().toSet()) {
                if (c.equals(c2) || c.equals(Card.LOCOMOTIVE))
                    playableCards.add(c);
            }
        }

        //Construct a list containing all possible set of card that can be played as additional cards
        List<SortedBag<Card>> options = (additionalCardsCount <= playableCards.size())
                ? new ArrayList<>(playableCards.build().subsetsOfSize(additionalCardsCount))
                : new ArrayList<>();

        //Sort the List of possible additional cards depending on the amount of locomotives
        options.sort(Comparator.comparing(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return options;
    }

    /**
     * Gives a new PlayerState with the new claimed route.
     *
     * @param route      the newly claimed route.
     * @param claimCards the cards used to claim the route.
     * @return A new PlayerState with the new list of routes. (PlayerState)
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        //Set the new cards of the player
        SortedBag<Card> newCards = cards.difference(claimCards);
        //Add the route to the list of routes
        List<Route> newRoutes = routes();
        newRoutes.add(route);

        return new PlayerState(tickets, newCards, newRoutes);
    }

    /**
     * The amount of points of the tickets (can be negative).
     *
     * @return The amount of points of the tickets. (int)
     */
    public int ticketPoints() {
        //Get the biggest id
        int maxId = 0;
        for (Route r : routes()) {
            maxId = Math.max(r.station1().id(), maxId);
            maxId = Math.max(r.station2().id(), maxId);
        }

        //Create the partition of the connectivity
        StationPartition.Builder partitionBuilder = new StationPartition.Builder(maxId + 1);
        for (Route r : routes())
            partitionBuilder.connect(r.station1(), r.station2());

        //Calculate the total points given by the ticket
        StationPartition partition = partitionBuilder.build();
        int totalTicketsPoints = 0;
        for (Ticket t : tickets)
            totalTicketsPoints += t.points(partition);

        return totalTicketsPoints;
    }

    /**
     * The final points of the player (claimedPoints + ticketsPoints).
     *
     * @return The total points. (int)
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
