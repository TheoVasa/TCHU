package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class TestPlayer implements Player {
    private static final int TURN_LIMIT = 1000;

    private final Random rng;
    // Toutes les routes de la carte
    private final List<Route> allRoutes;

    private int turnCounter;
    private PlayerState ownState;
    private PublicGameState gameState;

    //current info of the player
    private String currentInfo;

    // Lorsque nextTurn retourne CLAIM_ROUTE
    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;

    public TestPlayer(long randomSeed, List<Route> allRoutes) {
        this.rng = new Random(randomSeed);
        this.allRoutes = List.copyOf(allRoutes);
        this.turnCounter = 0;
    }

    @Override
    public void initPlayers(PlayerId ownId,
            Map<PlayerId, String> playerNames) {

    }

    public String getCurrentInfo() {
        return currentInfo;
    }

    @Override
    public void receiveInfo(String info) {
        currentInfo = info;

    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.gameState = newState;
        this.ownState = ownState;
    }

    @Override public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return null;
    }

    @Override
    public TurnKind nextTurn() {
        turnCounter += 1;
        if (turnCounter > TURN_LIMIT)
            throw new Error("Trop de tours joués !");

        // Détermine les routes dont ce joueur peut s'emparer
        List<Route> claimableRoutes = new ArrayList<>();
        for(Route r : allRoutes){
            if (ownState.canClaimRoute(r)) claimableRoutes.add(r);
        }

        if (claimableRoutes.isEmpty()) {
            return TurnKind.DRAW_CARDS;
        } else {
            int routeIndex = rng.nextInt(claimableRoutes.size());
            Route route = claimableRoutes.get(routeIndex);
            List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

            routeToClaim = route;
            initialClaimCards = cards.get(0);
            return TurnKind.CLAIM_ROUTE;
        }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        int numberOfTickets = rng.nextInt(options.size());
        SortedBag.Builder<Ticket> chosenTicketsBuilder = new SortedBag.Builder<>();
        int counter =0;
        while(counter==numberOfTickets){
            Ticket chosenTicket = options.get(rng.nextInt(options.size()));
            if(chosenTicketsBuilder.)
            chosenTicketsBuilder.add(chosenTicket);
            ++counter;
        }
        return chosenTicketsBuilder.build();
    }

    @Override
    public int drawSlot() {
        return rng.nextInt(Constants.FACE_UP_CARDS_COUNT+1)-1;
    }

    @Override
    public Route claimedRoute() {
        return null;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return null;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(
            List<SortedBag<Card>> options) {
        return null;
    }
}