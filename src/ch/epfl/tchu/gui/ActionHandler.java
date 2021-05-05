package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * This interface has for sole purpose to contains all functional interfaces, who's managing the action of the player.
 */
public interface ActionHandler {

    @FunctionalInterface
    interface DrawTicketsHandler{
        /**
         * Manage when the player choose to draw tickets.
         */
        void onDrawTickets();

    }

    @FunctionalInterface
    interface DrawCardHandler{
        /**
         * Manage when want to draw a card.
         *
         * @param slot of the card he choose.
         */
        void onDrawCard(int slot);

    }

    @FunctionalInterface
    interface ClaimRouteHandler{
        /**
         * Manage when the player choose to claim a route.
         *
         * @param route he wants to claim.
         * @param claimCards with which he claims the route.
         */
        void onClaimRoute(Route route, SortedBag<Card> claimCards);
    }

    @FunctionalInterface
    interface ChooseTicketsHandler{
        /**
         * Manage when the player choose some tickets after he chose to draw some.
         *
         * @param chooseTickets he choose to keep.
         */
        void onChooseTickets(SortedBag<Ticket> chooseTickets);

    }

    @FunctionalInterface
    interface ChooseCardsHandler{
        /**
         * Manage when the player choose some additional cards after he try to claim a tunnel.
         *
         * @param chooseCards he choose to claim the tunnel.
         */
        void onChooseCards(SortedBag<Card> chooseCards);

    }

}
