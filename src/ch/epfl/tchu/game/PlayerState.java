package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PlayerState extends PublicPlayerState {

    /**
     * Attributes
     */
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;


    /**
     * Constructor
     * @param tickets the tickets of the player
     * @param cards the remaining cards of the player (that he can play)
     * @param routes the routes of the player (that he already claimed)
     * @throws
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(), cards.size(),routes);
        this.cards = SortedBag.of(cards);
        this.tickets = SortedBag.of(tickets);
    }

    /**
     * Initialize the initial state of the player
     * @param initialCards the initial cards of the player
     * @return the new PlayerState withe initial cards
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        //Check correctness of the arguments
        Preconditions.checkArgument(initialCards.size() == 4);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Getter for the tickets of the player
     * @return the tickets of the player
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    /**
     * Getter for the cards of the player
     * @return the cards of the player
     */
    public SortedBag<Card> cards(){
        return cards;
    }

    /**
     * Initiale a new PlayerState with the new given card
     * @param card the new card we want to add to the player state
     * @return a PlayerState with the added card
     */
    public PlayerState withAddedCard(Card card){
        return withAddedCards(SortedBag.of(card));
    }

    /**
     * Gives a player state with an added ticket
     * @param newTickets the ticket to add to the player state
     * @return a player state with the added ticket
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        SortedBag.Builder<Ticket> newTicketsBuilder = new SortedBag.Builder<>();
        newTicketsBuilder.add(tickets);
        newTicketsBuilder.add(newTickets);

        return new PlayerState(newTicketsBuilder.build(), cards, routes());
    }


    /**
     * Gives a new player state with multiple added cards
     * @param newCards the SortedBag of cards we want to add
     * @return a new player state with the added cards
     */
    public PlayerState withAddedCards(SortedBag<Card> newCards){
        SortedBag.Builder<Card> newCardsBuilder = new SortedBag.Builder<>();
        newCardsBuilder.add(cards);
        newCardsBuilder.add(newCards);

        return new PlayerState(tickets, newCardsBuilder.build(), routes());
    }

    /**
     * Determine if the player can claim a route depending on its state
     * @param route the route to check if it's claimable
     * @return true if the route is claimable
     */
    public boolean canClaimRoute(Route route){
        //Sufficient cars
        if (route.length() > carCount())
            return false;

        //Sufficient  cards
        for (SortedBag<Card> l: route.possibleClaimCards())
            if (cards.contains(l))
                return true;
        return false;
    }

    /**
     * Gives a multiset of all the possible combination of cards needed to claim the route
     * @param route the route to claim
     * @return all the possible list of cards to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        //Check correctness of the argument
        Preconditions.checkArgument(route.length() <= carCount());
        //Check if the player has the required cards
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        for (SortedBag<Card> cardList : route.possibleClaimCards()){
            if (cards.contains(cardList))
                possibleClaimCards.add(cardList);
        }
        return possibleClaimCards;
    }

    /**
     * Gives a new PlayerState with a new claimed route added to the route list
     * @param route the newly claimed route
     * @param claimCards the cards used to claim the rout
     * @return A new PlayerState with the new list of routes
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        //Set the new cards of the player
        SortedBag<Card> newCards = cards.difference(claimCards);
        //Add the route to the list of routes
        List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);

        return new PlayerState(tickets, newCards, newRoutes);
    }

    /**
     * Gives the amount of points of the tickets (can be negative)
     * @return The amount of points of the tickets
     */
    public int ticketPoints(){
        //Get the biggest id
        int maxId = 0;
        for (Route r: routes()) {
            maxId = Math.max(ChMap.stations().indexOf(r.station1()), maxId);
            maxId = Math.max(ChMap.stations().indexOf(r.station2()), maxId);
        }

        //Create the partition of the connectivity
        StationPartition.Builder partitionBuilder = new StationPartition.Builder(maxId+1);
        for (Route r: routes())
            partitionBuilder.connect(r.station1(), r.station2());

        //Calculate the total points given by the ticket
        int totalTicketPoint = 0;
        for (Ticket t: tickets)
            totalTicketPoint += t.points(partitionBuilder.build());

        return totalTicketPoint;
    }

    /**
     * Getter for the total points of the player
     * @return The total points
     */
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }

    /**
     * Gives all the possible list of additional cards a player can play to claim a underground
     * @param additionalCardsCount number of additional cards to play
     * @param initialCards cards the player has already played
     * @param drawnCards the 3 cards that are drawn
     * @return A List of all possible SortedBag of cards that can be played to claim the underground
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
        //Check correctness of the arguments
        Preconditions.checkArgument( 1 <= additionalCardsCount &&
                                    additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        //Determine which cards can be played as additional cards
        //cards.difference(initialCards) gives a new List of the cards that can be played, without the initial cards
        SortedBag.Builder<Card> playableCards = new SortedBag.Builder<>();
        for (Card card: cards.difference(initialCards)){
            if (drawnCards.contains(card) || card.equals(Card.LOCOMOTIVE))
                playableCards.add(card);
        }

        //Construct a list containing all possible set of card that can be played as additional cards
        List<SortedBag<Card>> options = new ArrayList<>(playableCards.build().subsetsOfSize(additionalCardsCount));

        //Sort the List of possible additional cards depending on the amount of locomotives
        options.sort(Comparator.comparing(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return options;
    }
}
