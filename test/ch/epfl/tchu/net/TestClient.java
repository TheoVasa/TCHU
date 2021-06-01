package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.test.TestRandomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.Player.TurnKind.CLAIM_ROUTE;

public class TestClient {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5107, 5108, "name");
        playerClient.runGame();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements Player {
        private final List<Ticket> alltickets = ChMap.tickets();
        private final List<Route> allRoutes = ChMap.routes();
        private final List<Card> allCards = Card.ALL;
        private final Random rng = TestRandomizer.newRandom();

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> names) {
            System.out.println("Message received!");
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
        }

        /**
         * get the lastChat that the player send.
         *
         * @return the chat. (String)
         */
        @Override public String lastChat() {
            return null;
        }

        /**
         * used to inform the player he receive a new chat.
         *
         * @param chat the player need the receive.
         */
        @Override public void receiveChat(String chat) {

        }

        /**
         * Communicate information during the game.
         *
         * @param info the information we want to communicate
         */
        @Override public void receiveInfo(String info) {
            System.out.println("Message received!");
            System.out.printf("Received info : %s\n", info);

        }

        /**
         * Used to change the state and communicate the player the new game state and his own state.
         *
         * @param newState the new state of the game
         * @param ownState the state of the player
         */
        @Override public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.println("Message received!");
            System.out.printf("new game state : %s\n", Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState));
            System.out.printf("player state : %s\n", Serdes.PLAYER_STATE_SERDE.serialize(ownState));

        }

        /**
         * Used to communicate to the player the tickets he choose to begin the game
         *
         * @param tickets the tickets the player choose
         */
        @Override public void setInitialTicketChoice(
                SortedBag<Ticket> tickets) {
            System.out.println("Message received!");
            System.out.printf("initial ticket : %s\n", tickets);

        }

        /**
         * Ask the player to choose the initial tickets.
         *
         * @return a SortedBag of tickets (SortedBag< Tickets >>)
         */
        @Override public SortedBag<Ticket> chooseInitialTickets() {
            System.out.println("Message received!");
            SortedBag<Ticket> chosenTickets = SortedBag.of(alltickets.subList(3, 6));
            System.out.printf("Chosen tickets : %s\n", chosenTickets);

            return chosenTickets;
        }

        /**
         * Used to know the kind of action the player want to do during his turn.
         *
         * @return a type of action the player wants to do for the turn (TurnKind)
         */
        @Override public TurnKind nextTurn() {
            System.out.println("Message received!");
            System.out.println("player want to claim a route");
            return CLAIM_ROUTE;
        }

        /**
         * Used when the player decide to choose additional tickets during the game,
         * communicate the drawn ticket and the chosen ones.
         *
         * @param options the additional tickets he can choose
         * @return the tickets the player chose (SortedBag< Tickets >>)
         */
        @Override public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            System.out.println("Message received!");
            System.out.printf("Options tickets : %s\n", options);
            List<Ticket> chosenTickets = options.toList();
            chosenTickets.remove(0);
            System.out.printf("Chosen Tickets : %s\n", chosenTickets);
            return SortedBag.of(chosenTickets);
        }

        /**
         * Used to know where the player want to draw a new Card, meaning the face up cards or the top of the deck.
         *
         * @return the slot of the draw, meaning 0 to 4 in the case the player draw a face up cards,
         * or Constants.DECK_SLOT if he wants to pick up the top deck card
         */
        @Override public int drawSlot() {
            var slot = Constants.DECK_SLOT;
            System.out.println("Message received!");
            System.out.printf("Chosen slot %s\n", slot);
            return slot;
        }

        /**
         * When a player decide to try to claim a route, used to know which route it is.
         *
         * @return the route the player tries to claim (Route)
         */
        @Override public Route claimedRoute() {
            Route claimedRoute = allRoutes.get(rng.nextInt(allRoutes.size()));
            System.out.println("Message received!");
            System.out.printf("claimed route : %s\n", claimedRoute.id());
            return claimedRoute;
        }

        /**
         * When a player decide to try to claim a route, used to know which initial he use to do it.
         *
         * @return the initial cards he used to try to claim an route (SortedBag< Card >>)
         */
        @Override public SortedBag<Card> initialClaimCards() {
            SortedBag<Card> claimCards = SortedBag.of(4, allCards.get(rng.nextInt(allCards.size())));
            System.out.println("Message received!");
            System.out.printf("Claim cards : %s\n", claimCards);
            return claimCards;
        }

        /**
         * When a player decide to try to claim a route,
         * used to ask the player which additional cards he choose (if there is some).
         *
         * @param options the possibility of additional cards
         * @return the cards the player want play additional,
         * return a void SortedBag if he decide to not claim the route (SortedBag< Tickets >>)
         */
        @Override public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            SortedBag<Card> addCards = SortedBag.of(4, allCards.get(rng.nextInt(allCards.size())));
            System.out.println("Message received!");
            System.out.printf("Additional cards : %s\n", addCards);
            return addCards;
        }

        @Override
        public String receivePlayerName() {
            return null;
        }
    }
}
