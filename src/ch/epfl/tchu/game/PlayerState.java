package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

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

    public PlayerState initial(SortedBag<Card> initialCards){

    }


}
