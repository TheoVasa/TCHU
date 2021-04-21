package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
            //serialize
            (String str)-> Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8)),
            //deserialize
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
     * Serde use to (de)serialize some PublicCardState.
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(
            //serialize
            (PublicCardState cardState)->{
                String faceUpCards = LIST_CARD_SERDE.serialize(cardState.faceUpCards());
                String deckSize = INTEGER_SERDE.serialize(cardState.deckSize());
                String discardsSize = INTEGER_SERDE.serialize(cardState.discardsSize());
                List<String> listOfData = List.of(faceUpCards, deckSize, discardsSize);

                return String.join(";", listOfData);
            },
            //deserialize
            (String data)->{
                String[] tabOfData = data.split((Pattern.quote(";")), -1);
                List<Card> faceUpCards = LIST_CARD_SERDE.deserialize(tabOfData[0]);
                int deckSize = INTEGER_SERDE.deserialize(tabOfData[1]);
                int discardsSize = INTEGER_SERDE.deserialize(tabOfData[2]);

                return new PublicCardState(faceUpCards, deckSize, discardsSize);
            }
    );

    /**
     * Serde use to (de)serialize some PublicPlayerState.
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            //serialize
            (PublicPlayerState playerState)->{
                String ticketCount = INTEGER_SERDE.serialize(playerState.ticketCount());
                String cardCount = INTEGER_SERDE.serialize(playerState.cardCount());
                String routes = LIST_ROUTE_SERDE.serialize(playerState.routes());
                List<String> listOfData = List.of(ticketCount, cardCount, routes);

                return String.join(";", listOfData);
            },
            //deserialize
            (String data)->{
                List<String> listOfData = Serde.listOf(STRING_SERDE, ";").deserialize(data);
                int ticketCount = INTEGER_SERDE.deserialize(listOfData.get(0));
                int cardCount = INTEGER_SERDE.deserialize(listOfData.get(1));
                List<Route> routes = LIST_ROUTE_SERDE.deserialize(listOfData.get(2));

                return new PublicPlayerState(ticketCount, cardCount, routes);
            }
    );;

    /**
     * Serde use to (de)serialize some PlayerState.
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(
            //serialize
            (PlayerState player)->{
                String tickets = SORTED_BAG_TICKETS_SERDE.serialize(player.tickets());
                String cards = SORTED_BAG_CARD_SERDE.serialize(player.cards());
                String routes = LIST_ROUTE_SERDE.serialize(player.routes());
                List<String> listOfData = List.of(tickets, cards, routes);

                return String.join(";", listOfData);
            },
            //deserialize
            (String data)->{
                String[] tabOfData = data.split((Pattern.quote(";")), -1);
                SortedBag<Ticket> tickets = SORTED_BAG_TICKETS_SERDE.deserialize(tabOfData[0]);
                SortedBag<Card> cards = SORTED_BAG_CARD_SERDE.deserialize(tabOfData[1]);
                List<Route> routes = LIST_ROUTE_SERDE.deserialize(tabOfData[2]);

                return new PlayerState(tickets, cards, routes);
            }
    );

    /**
     * Serde use to (de)serialize some PublicGameState.
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            //serialize
            (PublicGameState game)->{
                String ticketsCount = INTEGER_SERDE.serialize(game.ticketsCount());
                String cardState = PUBLIC_CARD_STATE_SERDE.serialize(game.cardState());
                String currentPlayerId = PLAYER_ID_SERDE.serialize(game.currentPlayerId());
                String playerState1 = PUBLIC_PLAYER_STATE_SERDE.serialize(game.playerState(PlayerId.PLAYER_1));
                String playerState2 = PUBLIC_PLAYER_STATE_SERDE.serialize(game.playerState(PlayerId.PLAYER_2));
                String lastPlayer = PLAYER_ID_SERDE.serialize(game.lastPlayer());
                List<String> listOfData = List.of(ticketsCount, cardState, currentPlayerId, playerState1, playerState2, lastPlayer);

                return String.join(";", listOfData);
            },
            //deserialize
            (String data)->{
                String[] tabOfData = data.split((Pattern.quote(";")), -1);
                int ticketsCount = INTEGER_SERDE.deserialize(tabOfData[0]);
                PublicCardState cardState = PUBLIC_CARD_STATE_SERDE.deserialize(tabOfData[1]);
                PlayerId currentPlayerId = PLAYER_ID_SERDE.deserialize(tabOfData[2]);
                PublicPlayerState player1 = PUBLIC_PLAYER_STATE_SERDE.deserialize(tabOfData[3]);
                PublicPlayerState player2 = PUBLIC_PLAYER_STATE_SERDE.deserialize(tabOfData[4]);
                PlayerId lastPlayer = PLAYER_ID_SERDE.deserialize(tabOfData[5]);
                Map<PlayerId, PublicPlayerState> playerState = new EnumMap<>(PlayerId.class);
                playerState.put(PlayerId.PLAYER_1, player1);
                playerState.put(PlayerId.PLAYER_2, player2);

                return new PublicGameState(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
            }
    );

}
