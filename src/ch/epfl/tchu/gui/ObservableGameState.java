package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * This class contains all the observable property in a game of tchu, those property "observe" what's passing during the game and must be set each time the state of the game change.
 */
public final class ObservableGameState {
    //Constants
    private static final int TOTAL_CARDS_IN_GAME = 110;
    private static final int TOTAL_TICKETS_IN_GAME = ChMap.tickets().size();

    //the player attached to the observable game state.
    private final PlayerId player;
    //the other one
    private final PlayerId otherPlayer;
    //gameState attached to the observable
    private PublicGameState gameState;
    //playerState attached to the observable
    private PlayerState playerState;

    //all properties:
    //public states of the game
    private final SimpleIntegerProperty restingTicketsPercents;
    private final SimpleIntegerProperty restingCardsPercents;
    private final List<SimpleObjectProperty<Card>> faceUpCards;
    private final Map<Route, SimpleObjectProperty<PlayerId>> ownersOfEachRoutes;
    private final Map<Route, SimpleBooleanProperty> claimableRoutes;

    //public states of all players
    private final Map<PlayerId, SimpleIntegerProperty> numberTicketsForEachPlayer;
    private final Map<PlayerId, SimpleIntegerProperty> numberCardsForEachPlayer;
    private final Map<PlayerId, SimpleIntegerProperty> numberCarsForEachPlayer;
    private final Map<PlayerId, SimpleIntegerProperty> numberConstructsPointsForEachPlayer;

    //private states of the player
    private final ObservableList<Ticket> ticketsOfPlayer;
    private final Map<Card, SimpleIntegerProperty> numberOfCardsForEachType;
    private final Map<Route, SimpleBooleanProperty> playerHasGivenRoute;

    /**
     * Construct an ObservableGameState.
     *
     * @param player attached to the ObservableGameState.
     */
    public ObservableGameState(PlayerId player) {
        this.player = player;
        otherPlayer = player.next();

        restingCardsPercents = new SimpleIntegerProperty(0);
        restingTicketsPercents = new SimpleIntegerProperty(0);
        faceUpCards = createFaceUpCards();
        ownersOfEachRoutes= createAllRoutesOwners();
        //set that all routes aren't claimable
        claimableRoutes = new HashMap<>();
        ChMap.routes().forEach((route) -> claimableRoutes.put(route, new SimpleBooleanProperty(false)));

        numberTicketsForEachPlayer = createEnumMapWithNullIntegerProperty(PlayerId.values());
        numberCardsForEachPlayer = createEnumMapWithNullIntegerProperty(PlayerId.values());
        numberCarsForEachPlayer = createEnumMapWithNullIntegerProperty(PlayerId.values());
        numberConstructsPointsForEachPlayer = createEnumMapWithNullIntegerProperty(PlayerId.values());

        ticketsOfPlayer = FXCollections.observableArrayList();
        numberOfCardsForEachType = createEnumMapWithNullIntegerProperty(Card.values());
        playerHasGivenRoute = new HashMap<>();
        ChMap.routes().forEach((route) -> playerHasGivenRoute.put(route, new SimpleBooleanProperty(false)));
    }

    /**
     * Set all properties given a new PublicGameState and a new PlayerState
     *
     * @param gameState, the new PublicGameState
     * @param playerState, the new PlayerState
     */
    public void setState(PublicGameState gameState, PlayerState playerState){
        this.gameState = gameState;
        this.playerState = playerState;
        List<PlayerId> allPlayers = List.of(player, otherPlayer);
        SortedBag<Card> playerCards = playerState.cards();
        SortedBag<Ticket> playerTickets = playerState.tickets();
        List<Route> playerRoutes = gameState.playerState(player).routes();
        List<Route> otherPlayerRoutes = gameState.playerState(otherPlayer).routes();

        //set all state of the player
        playerHasGivenRoute.forEach((r, p) -> {
            boolean hasRoute = playerRoutes.contains(r);
            p.set(hasRoute);
        });

        ticketsOfPlayer.setAll(playerTickets.toList());
        for(Card c : Card.ALL) numberOfCardsForEachType.get(c).set(playerCards.countOf(c));

        //set the percents of the cards and tickets
        restingTicketsPercents.setValue(generatePercents(gameState.ticketsCount(), TOTAL_TICKETS_IN_GAME));
        restingCardsPercents.setValue(generatePercents(gameState.cardState().deckSize(),
                TOTAL_CARDS_IN_GAME));

        //set states for all players
        allPlayers.forEach((plr)->{
            numberTicketsForEachPlayer.get(plr).set(gameState.playerState(plr).ticketCount());
            numberCardsForEachPlayer.get(plr).set(gameState.playerState(plr).cardCount());
            numberCarsForEachPlayer.get(plr).set(gameState.playerState(plr).carCount());
            numberConstructsPointsForEachPlayer.get(plr).set(gameState.playerState(plr).claimPoints());
        });

        //set the owner of each route
        playerRoutes.forEach((r) -> ownersOfEachRoutes.get(r).set(player));
        otherPlayerRoutes.forEach((r) -> ownersOfEachRoutes.get(r).set(otherPlayer));
        //set which routes are now claimable
        claimableRoutes.forEach((route, bool) -> {
            if(playerState.canClaimRoute(route) && ownersOfEachRoutes.get(route).get()==null)
                claimableRoutes.get(route).set(true);
        });

        //set the faceUpCards
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = gameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
    }

    /**
     * Used to know the property attached to the resting tickets in the game.
     *
     * @return the property attached of the resting tickets in the game (ReadOnlyIntegerProperty).
     */
    public ReadOnlyIntegerProperty restingTicketsPercentsProperty() {
        return restingTicketsPercents;
    }

    /**
     * Used to know the property attached to the resting cards in the game.
     *
     * @return the property attached of the resting cards in the game (ReadOnlyIntegerProperty).
     */
    public ReadOnlyIntegerProperty restingCardsPercentsProperty() {
        return restingCardsPercents;
    }

    /**
     * Used to get the property of a given face up card.
     *
     * @param slot of the face up card.
     * @return the property attached to the card (ReadOnlyObjectProperty)
     * @throws IllegalArgumentException if the slot isn't in face up cards
     */
    public ReadOnlyObjectProperty<Card> faceUpCardsProperty (int slot){
        Preconditions.checkArgument(slot < faceUpCards.size());
        return faceUpCards.get(slot);
    }

    /**
     * Used to get the property attached of an owner of a given route.
     *
     * @param route we want to know the property of his owner.
     * @return the property attached to the owner (ReadOnlyObjectProperty)
     * @throws IllegalArgumentException if the route
     */
    public ReadOnlyObjectProperty<PlayerId> routeProperty (Route route){
        Preconditions.checkArgument(ownersOfEachRoutes.containsKey(route));
        return ownersOfEachRoutes.get(route);
    }

    /**
     * Used to get the property attached to the number of tickets of a given player.
     *
     * @param plr player we want to know his number of tickets.
     * @return the property attached to the number of tickets of the given player (ReadOnlyIntegerProperty).
     * @throws IllegalArgumentException if the player isn't one of the player in the game.
     */
    public ReadOnlyIntegerProperty numberOfTicketsForGivenPlayerProperty(PlayerId plr){
        Preconditions.checkArgument(numberTicketsForEachPlayer.containsKey(plr));
        return numberTicketsForEachPlayer.get(plr);

    }

    /**
     * Used to get the property attached to the number of cards of a given player.
     *
     * @param plr player we want to know his number of cards.
     * @return the property attached to the number of cards of the given player (ReadOnlyIntegerProperty).
     * @throws IllegalArgumentException if the player isn't one of the player in the game.
     */
    public ReadOnlyIntegerProperty numberOfCardsForGivenPlayerProperty(PlayerId plr){
        Preconditions.checkArgument(numberCardsForEachPlayer.containsKey(plr));
        return numberCardsForEachPlayer.get(plr);

    }

    /**
     * Used to get the property attached to the number of cars of a given player.
     *
     * @param plr player we want to know his number of cars.
     * @return the property attached to the number of cars of the given player (ReadOnlyIntegerProperty).
     * @throws IllegalArgumentException if the player isn't one of the player in the game.
     */
    public ReadOnlyIntegerProperty numberOfCarsForGivenPlayerProperty(PlayerId plr){
        Preconditions.checkArgument(numberCarsForEachPlayer.containsKey(plr));
        return numberCarsForEachPlayer.get(plr);

    }

    /**
     * Used to get the property attached to the number of construct points of a given player.
     *
     * @param plr player we want to know his number of construct points.
     * @return the property attached to the number of construct points of the given player (ReadOnlyIntegerProperty).
     * @throws IllegalArgumentException if the player isn't one of the player in the game.
     */
    public ReadOnlyIntegerProperty numberConstructsPointsForGivenPlayerProperty(PlayerId plr){
        Preconditions.checkArgument(numberConstructsPointsForEachPlayer.containsKey(plr));
        return numberConstructsPointsForEachPlayer.get(plr);

    }

    /**
     * Used to get the observable list of the tickets of the player.
     *
     * @return the observable list of the ticket (ObservableList).
     */
    public ObservableList<Ticket> ticketsOfPlayerProperty() {
        return ticketsOfPlayer;
    }

    /**
     * Used to get the property attached to the number of cards possessed by the player for a given type.
     *
     * @param type type of cards we want to know.
     * @return the property attached to the number of cards possessed by the player for the given type. (ReadOnlyIntegerProperty).
     * @throws IllegalArgumentException if the type isn't one of the Card in the game.
     */
    public ReadOnlyIntegerProperty numberOfPlayerCardsForGivenTypeProperty(Card type){
        Preconditions.checkArgument(numberOfCardsForEachType.containsKey(type));
        return numberOfCardsForEachType.get(type);
    }

    /**
     * Used to get the boolean property attached to the given route.
     *
     * @param route we want to know the property attached to it.
     * @return the property attached to the route (ReadOnlyBooleanProperty).
     * @throws IllegalArgumentException if the route isn't in the map.
     */
    public ReadOnlyBooleanProperty playerHasGivenRouteProperty(Route route){
        Preconditions.checkArgument(playerHasGivenRoute.containsKey(route));
        return playerHasGivenRoute.get(route);
    }

    /**
     *
     * @return canDrawTickets from the current gameState(boolean).
     */
    public boolean canDrawTickets(){
        return gameState.canDrawTickets();
    }

    /**
     *
     * @return canDrawCards from the current gameState (boolean).
     */
    public boolean canDrawCards(){
        return gameState.canDrawCards();
    }

    /**
     *
     * @param route the player want to claim.
     * @return possible ClaimCards of the current PlayerState (List).
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return playerState.possibleClaimCards(route);
    }

    /**
     * This method is used to know if a route if claimable by the player attached to the current ObservableGameState.
     *
     * @param route we want to know if claimable.
     * @return a property containing if the route is claimable. (ReadOnlyBooleanProperty)
     */
    public ReadOnlyBooleanProperty claimable (Route route){
        return claimableRoutes.get(route);
    }

    //initialize the face uo cards with a "null" card for each slot.
    private static List<SimpleObjectProperty<Card>> createFaceUpCards(){
        List<SimpleObjectProperty<Card>> listOfProper = new ArrayList<>();
        for(int i=0; i<Constants.FACE_UP_CARDS_COUNT; ++i)
            listOfProper.add(new SimpleObjectProperty<Card>(null));
        return listOfProper;
    }

    //create all route owner trough the chmap routes and set a null owner for all of them.
    private static Map<Route, SimpleObjectProperty<PlayerId>> createAllRoutesOwners(){
        Map<Route, SimpleObjectProperty<PlayerId>> map = new HashMap<>();
        for(Route r : ChMap.routes()) map.put(r, new SimpleObjectProperty<>(null));
        return map;
    }

    //create a map from elements of an enum with for all elements, an integer property to 0.
    private static <E extends Enum<E>> Map<E, SimpleIntegerProperty> createEnumMapWithNullIntegerProperty(E[] tabOfEnum){
        Map<E, SimpleIntegerProperty> map = new HashMap<>();
        for(E element : tabOfEnum)map.put(element, new SimpleIntegerProperty(0));
        return map;
    }

    //generate percents given two numbers, in int (always between 0 and 100).
    private int generatePercents(int number, int total){
        int percent = (int) Math.ceil(((double) number / (double) total)*100);
        if(percent > 100) percent = 100;
        else if(percent < 0) percent = 0;
        return percent;
    }
}
