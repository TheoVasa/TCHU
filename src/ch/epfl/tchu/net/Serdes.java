package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * This class is used to contain all different Serde used, not instantiable.
 */
public class Serdes {

    /**
     * Serde use to (de)serialize some Integer.
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(String::valueOf, Integer::valueOf);

    /**
     * Serde use to (de)serialize some String.
     */
    //pas sur de celui la.. a revoir
    public static final Serde<String> STRING_SERDE = Serde.of(
            (String str)-> Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8)),
            (String data)-> new String (Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)
    );

    /**
     * Serde use to (de)serialize some PlayerID.
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);


    /**
     * Serde use to (de)serialize some TurnKind.
     */
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serde use to (de)serialize some Card.
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * Serde use to (de)serialize some Route.
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * Serde use to (de)serialize some Ticket.
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * Serde use to (de)serialize some List of String.
     */
    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");

    /**
     * Serde use to (de)serialize some List of Card.
     */
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, ",");

    /**
     * Serde use to (de)serialize some List of Route.
     */
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");

    /**
     * Serde use to (de)serialize some SortedBag of Card.
     */
    public static final Serde<SortedBag<Card>> SORTED_BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, ",");

    /**
     * Serde use to (de)serialize some SortedBag of Ticket.
     */
    public static final Serde<SortedBag<Ticket>> SORTED_BAG_TICKETS_SERDE = Serde.bagOf(TICKET_SERDE, ",");

    /**
     * Serde use to (de)serialize some List of SortedBag of Card.
     */
    public static final Serde<List<SortedBag<Card>>> LIST_SORTED_BAG_CARD_SERDE = Serde.listOf(SORTED_BAG_CARD_SERDE, ";");

    /**
     * Serde use to (de)serialize some PublicPlayerState.
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE;

    /**
     * Serde use to (de)serialize some PlayerState.
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE;

    /**
     * Serde use to (de)serialize some PublicGameState.
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE;

    /**
     * Serde use to (de)serialize some PublicCardState.
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE;




}
