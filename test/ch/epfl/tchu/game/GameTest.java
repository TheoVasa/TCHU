package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;

public class GameTest {

    @Test
    void playWorks(){

        TestPlayer player1 = new TestPlayer(5, ChMap.routes());
        TestPlayer player2 = new TestPlayer(7, ChMap.routes());
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playersNames = new EnumMap<>(PlayerId.class);
        List<Ticket> tickets = ChMap.tickets();

        players.put(PlayerId.PLAYER_1, player1);
        players.put(PlayerId.PLAYER_2, player2);

        playersNames.put(PlayerId.PLAYER_1, "Théo");
        playersNames.put(PlayerId.PLAYER_2, "Sélien");


        Game.play(players, playersNames, SortedBag.of(tickets), TestRandomizer.newRandom());
    }

    private final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;

        // Toutes les routes de la carte
        private final List<Route> allRoutes;
        private String ownName;
        private Map<PlayerId, String> playerNames;
        private PlayerId ownId;

        //initialisation of tickets
        private SortedBag<Ticket> initialTicketToChoose;
        private SortedBag<Ticket> initialChoosenTickets;
        private boolean hasChooseTickets = false;

        //turn gestion
        private TurnKind currentTurn;
        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;
        private boolean isOnTurn = false;

        //infos of the player
        private Info playerInfo;

        //number of call of receiveInfos
        int receiveInfosCall = 0;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;
        private boolean currentClaimRouteIsOverground;
        private SortedBag<Card> drawnCardsForUndergroundRoute;

        //lorsque nextTurn retourne DRAW_TICKETS
        private SortedBag<Ticket> lastChosenTickets;

        //lorsque nextTurn retourne DRAW_CARDS
        private boolean chooseTopDeckCard;
        private Card lastDrewFaceUpCard;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
        }

        @Override public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            ownName = playerNames.get(ownId);
            this.ownId = ownId;
            this.playerNames = Map.copyOf(playerNames);
            playerInfo = new Info(ownName);
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

        @Override public void receiveInfo(String info) {
            ++receiveInfosCall;

            if (receiveInfosCall < turnCounter)
                throw new Error("Pas assez d'infos envoyees compare au nombre de tours");

            int carsCountPl1 = 0;
            int carsCountPl2 = 0;
            if (isOnTurn) {
                for (Route r : gameState.playerState(PlayerId.PLAYER_1).routes())
                    carsCountPl1 += r.length();
                for (Route r : gameState.playerState(PlayerId.PLAYER_2).routes())
                    carsCountPl2 += r.length();
            }

            System.out.println("Reçu par " + ownName + ": " + info);
            if (isOnTurn &&
                !(info.equals(new Info(playerNames.get((PlayerId.PLAYER_1))).canPlay()) ||
                info.equals(new Info(playerNames.get(PlayerId.PLAYER_2)).canPlay())) &&
                (info.equals(new Info(playerNames.get(PlayerId.PLAYER_1)).lastTurnBegins(carsCountPl1)) ||
                info.equals(new Info(playerNames.get(PlayerId.PLAYER_2)).lastTurnBegins(carsCountPl2))) &&
                currentClaimRouteIsOverground &&
                ownId == gameState.currentPlayerId())

                receiveInfoIsGood(info, expectedInfo(currentTurn, ownState, gameState));

        }

        @Override public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            initialTicketToChoose = SortedBag.of(tickets);
        }

        @Override public SortedBag<Ticket> chooseInitialTickets() {
            int numberOfInitialTicketsChoice = rng.nextInt(3) + 3;
            List<Ticket> chosenTickets = new ArrayList<>();
            while (!(chosenTickets.size() == numberOfInitialTicketsChoice)) {
                Ticket chosenTicket = initialTicketToChoose
                        .get(rng.nextInt(initialTicketToChoose.size()));
                if (!chosenTickets.contains(chosenTicket))
                    chosenTickets.add(chosenTicket);
            }
            initialChoosenTickets = SortedBag.of(chosenTickets);
            return initialChoosenTickets;
        }

        @Override public TurnKind nextTurn() {
            isOnTurn = true;
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");
            if (receiveInfosCall < turnCounter - 1)
                throw new Error(
                        "Problème avec le nombre d'appels à receiveInfo!");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = new ArrayList<>();
            for (Route r : allRoutes) {
                if (ownState.canClaimRoute(r))
                    claimableRoutes.add(r);
            }

            if (claimableRoutes.isEmpty()) {
                currentTurn = TurnKind.DRAW_CARDS;
                System.out.println();
                if (gameState.lastPlayer() != null)
                    isOnTurn = false;
                return TurnKind.DRAW_CARDS;

            } else if (claimableRoutes.size() > rng.nextInt(allRoutes.size())) {
                currentTurn = TurnKind.DRAW_TICKETS;
                System.out.println();
                if (gameState.lastPlayer() != null)
                    isOnTurn = false;
                return TurnKind.DRAW_TICKETS;

            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                currentClaimRouteIsOverground = route.level() == Route.Level.OVERGROUND;
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                currentTurn = TurnKind.CLAIM_ROUTE;
                System.out.println();
                if (gameState.lastPlayer() != null)
                    isOnTurn = false;
                return TurnKind.CLAIM_ROUTE;
            }
        }

        @Override public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            int numberOfTickets = rng.nextInt(options.size());
            List<Ticket> chosenTickets = new ArrayList<>();
            while (!(chosenTickets.size() == numberOfTickets)) {
                Ticket chosenTicket = options.get(rng.nextInt(options.size()));
                if (!chosenTickets.contains(chosenTicket)) {
                    chosenTickets.add(chosenTicket);
                }
            }
            lastChosenTickets = SortedBag.of(chosenTickets);
            hasChooseTickets = true;
            return lastChosenTickets;
        }

        @Override public int drawSlot() {
            int slot = rng.nextInt(Constants.FACE_UP_CARDS_COUNT + 1) - 1;
            chooseTopDeckCard = (slot == -1);
            if (slot >= 0)
                lastDrewFaceUpCard = gameState.cardState().faceUpCard(slot);
            return slot;
        }

        @Override public Route claimedRoute() {
            return routeToClaim;
        }

        @Override public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return (options.size()>0) ? options.get(rng.nextInt(options.size())) : SortedBag.of();
        }

        private void receiveInfoIsGood(String info, String expectedInfo) {
            if (!info.equals(expectedInfo)) {
                System.out.println("##########################################################");
                System.out.println("expected info:" + expectedInfo);
                System.out.println("actual info:" + info);
                throw new Error("Mauvaise info!");
            }
        }

        @Override
        public String receivePlayerName() {
            return null;
        }

        private String expectedInfo(TurnKind turnKind, PlayerState playerState,
                                    PublicGameState gameState) {

            switch (turnKind) {
                case DRAW_TICKETS:
                    if (hasChooseTickets) {
                        hasChooseTickets = false;
                        return playerInfo.keptTickets(lastChosenTickets.toSet().size());
                    } else {
                        return playerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT);
                    }

                case DRAW_CARDS:
                    if (chooseTopDeckCard) {
                        return playerInfo.drewBlindCard();
                    } else {
                        return playerInfo.drewVisibleCard(lastDrewFaceUpCard);
                    }

                case CLAIM_ROUTE:
                    return playerInfo.claimedRoute(routeToClaim, initialClaimCards);

                default:
                    return "";
                }
        }
    }
}
