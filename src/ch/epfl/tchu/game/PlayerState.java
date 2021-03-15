package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

public final class PlayerState extends PublicPlayerState {

    /**
     * Attributes
     */
    private SortedBag<Ticket> tickets;
    private SortedBag<Card> cards;

    /**
     * Constructor
     * @param tickets the tickets of the player
     * @param cards the remaining cards of the player (that he can play)
     * @param routes the routes of the player (that he already claimed)
     *
     */
    PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(), cards.size(),routes);
        this.cards = cards;
        this.tickets = tickets;

    }

    /**
     * Initialize the initial state of the player
     * @param initialCards the initial cards of the player
     * @return the new PlayerState withe initial cards
     */
    public PlayerState initial(SortedBag<Card> initialCards){
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
     *
     * @param newTickets
     * @return
     */
    public PlayerState withAddedTicket(SortedBag<Ticket> newTickets){
        SortedBag.Builder newTicketsBuilder = new SortedBag.Builder();
        newTicketsBuilder.add(tickets);
        newTicketsBuilder.add(newTickets);

        return new PlayerState(newTicketsBuilder.build(), cards, routes());
    }


    public PlayerState withAddedCards(SortedBag<Card> newCards){
        SortedBag.Builder newCardsBuilder = new SortedBag.Builder();
        newCardsBuilder.add(cards);
        newCardsBuilder.add(newCards);

        return new PlayerState(tickets, newCardsBuilder.build(), routes());
    }

    public boolean canClaimRoute(Route route){
        //Sufficient  cards
        for (SortedBag<Card> l: route.possibleClaimCards())
            if (!cards.contains(l))
                return false;

        //Sufficient cars
        if (route.length() > carCount())
            return false;

        return true;
    }

    public List<SortedBag<Card>> possibleClaimCard(Route route){
        //Check correctness of the argument
        Preconditions.checkArgument(route.length() <= carCount());
        return route.possibleClaimCards();
    }

    public PlayerState withClaimRoute(Route route, SortedBag<Card> claimCards){
        //Set the new cards of the player
        SortedBag<Card> newCards = cards.difference(claimCards);
        //Add the route to the list of routes
        List<Route> newRoutes = new ArrayList<>();
        newRoutes.addAll(routes());
        newRoutes.add(route);
        return new PlayerState(tickets, newCards, newRoutes);
    }

    public int ticketPoint(){
        //Get the biggest id
        int maxId = 0;
        for (Route r: routes())
            maxId = (ChMap.stations().indexOf(r) > maxId) ? ChMap.stations().indexOf(r) : maxId;

        //Create the partition of the connectivity
        StationPartition.Builder partitionBuilder = new StationPartition.Builder(maxId);
        for (Route r: routes())
            partitionBuilder.connect(r.station1(), r.station2());

        //Calculate the total points given by the ticket
        int totalTicketPoint = 0;
        for (Ticket t: tickets)
            totalTicketPoint+=t.points(partitionBuilder.build());

        return totalTicketPoint;
    }

    public int finalPoints(){
        return claimPoints() + ticketPoint();
    }

    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
        //Check correctness of the arguments
        Preconditions.checkArgument( 1 <= additionalCardsCount &&
                                    additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);



    }


}
