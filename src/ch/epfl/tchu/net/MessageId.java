package ch.epfl.tchu.net;

/**
 * This enumeration contains all different messages that the server can send to the clients, public.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */

public enum MessageId {

    //The elements of the enumeration
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS
}
