package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class ObservableGameState {

    //the player attached to the observable game state.
    private final PlayerId player;
    private final PlayerId otherPlayer;

    //all properties:
    //public state of the game
    private final SimpleIntegerProperty restingTicketsPercents;
    private final SimpleIntegerProperty restingCardsPercents;
    private final List<SimpleObjectProperty<Card>> faceUpCards;
    private final Map<Route, SimpleObjectProperty<PlayerId>> ownersOfEachRoutes;

    //public state of all players
    private final Map<PlayerId, SimpleIntegerProperty> numberTicketsForEachPlayer;
    private final Map<PlayerId, SimpleIntegerProperty> numberCardsForEachPlayer;
    private final Map<PlayerId, SimpleIntegerProperty> numberCarsForEachPlayer;
    private final Map<PlayerId, SimpleIntegerProperty> numberConstructsPointsForEachPlayer;

    //private state of the player
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

        numberTicketsForEachPlayer = createEnumMapWithNullIntegerProperty(PlayerId.values());
        numberCardsForEachPlayer = createEnumMapWithNullIntegerProperty(PlayerId.values());
        numberCarsForEachPlayer = createEnumMapWithNullIntegerProperty(PlayerId.values());
        numberConstructsPointsForEachPlayer = createEnumMapWithNullIntegerProperty(PlayerId.values());

        ticketsOfPlayer = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList());
        numberOfCardsForEachType = createEnumMapWithNullIntegerProperty(Card.values());
        playerHasGivenRoute = new HashMap<>();
        for(Route r : ChMap.routes()) playerHasGivenRoute.put(r, new SimpleBooleanProperty(false));
    }

    /**
     * Set all properties given a new PublicGameState and a new PlayerState
     *
     * @param gameState, the new PublicGameState
     * @param playerState, the new PlayerState
     */
    public void setState(GameState gameState, PlayerState playerState){

        List<PlayerId> allPlayers = List.of(player, otherPlayer);
        SortedBag<Card> playerCards = playerState.cards();
        SortedBag<Ticket> playerTickets = playerState.tickets();
        List<Route> playerRoutes = gameState.playerState(player).routes();
        List<Route> otherPlayerRoutes = gameState.playerState(otherPlayer).routes();

    //set all state of the player
        playerHasGivenRoute.forEach((r, p)->{
            boolean hasRoute = playerRoutes.contains(r);
            p.set(hasRoute);
        });

        ticketsOfPlayer.setAll(playerTickets.toList());
        for(Card c : Card.ALL)numberOfCardsForEachType.put(c, new SimpleIntegerProperty(playerCards.countOf(c)));

    //set the percents of the cards and tickets
        restingTicketsPercents.set(generatePercents(gameState.ticketsCount(), ChMap.tickets().size()));
        restingCardsPercents.set(generatePercents(gameState.cardState().deckSize(), gameState.cardState().totalSize()));

    //set states for all players
        allPlayers.forEach((PlayerId plr)->{
            numberTicketsForEachPlayer.get(plr).set(gameState.playerState(plr).tickets().size());
            numberCardsForEachPlayer.get(plr).set(gameState.playerState(plr).cardCount());
            numberCarsForEachPlayer.get(plr).set(gameState.playerState(plr).carCount());
            numberConstructsPointsForEachPlayer.get(plr).set(gameState.playerState(plr).claimPoints());
        });

    //set the owner of each route
        playerRoutes.forEach((Route r)->ownersOfEachRoutes.get(r).set(player));
        otherPlayerRoutes.forEach((Route r)->ownersOfEachRoutes.get(r).set(otherPlayer));

    //set the faceUpCards
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = gameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
    }

    /**
     * Used to get the resting tickets in the game in percents.
     *
     * @return the resting tickets in the game in percents (int).
     */
    public int getRestingTicketsPercents() {
        return restingTicketsPercents.get();
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
     * Used to get the resting cards in the game in percents.
     *
     * @return the resting cards in the game in percents (int).
     */
    public int getRestingCardsPercents() {
        return restingCardsPercents.get();
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
        Preconditions.checkArgument(slot< faceUpCards.size());
        return faceUpCards.get(slot);
    }

    /**
     * Used to get the face up card attached of a given property.
     *
     * @param slot of the face up card attached to the property.
     * @return the card in the property (Card).
     * @throws IllegalArgumentException if the slot isn't in face up cards
     */
    public Card getFaceUpCard (int slot){
        Preconditions.checkArgument(slot< faceUpCards.size());
        return faceUpCards.get(slot).get();
    }

    /**
     * Used to get the property attached of an owner of a given route.
     *
     * @param route we want to know the property of his owner.
     * @return the property attached to the owner (ReadOnlyObjectProperty)
     * @throws IllegalArgumentException if the route
     */
    public ReadOnlyObjectProperty<PlayerId> RouteProperty (Route route){
        Preconditions.checkArgument(ownersOfEachRoutes.containsKey(route));
        return ownersOfEachRoutes.get(route);
    }

    /**
     * Used to get the current owner of a given route attached of a given property.
     *
     * @param route we want to know the owner.
     * @return the owner of the route (PlayerId) or null if the route hasn't owner.
     */
    public PlayerId getFaceUpCards (Route route){
        Preconditions.checkArgument(ownersOfEachRoutes.containsKey(route));
        return ownersOfEachRoutes.get(route).get();
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
     * Used to get the number of tickets of a given player.
     *
     * @param plr player we want to know his number of tickets.
     * @return the number of tickets of the given player (int).
     * @throws IllegalArgumentException if the given player isn't one of the player in the game.
     */
    public int getNumberOfTicketsForGivenPlayer(PlayerId plr){
        Preconditions.checkArgument(numberTicketsForEachPlayer.containsKey(plr));
        return numberTicketsForEachPlayer.get(plr).get();
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
     * Used to get the number of cards of a given player.
     *
     * @param plr player we want to know his number of cards.
     * @return the number of cards of the given player (int).
     * @throws IllegalArgumentException if the given player isn't one of the player in the game.
     */
    public int getNumberOfCardsForGivenPlayer(PlayerId plr){
        Preconditions.checkArgument(numberCardsForEachPlayer.containsKey(plr));
        return numberCardsForEachPlayer.get(plr).get();
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
     * Used to get the number of cars of a given player.
     *
     * @param plr player we want to know his number of cars.
     * @return the number of cars of the given player (int).
     * @throws IllegalArgumentException if the given player isn't one of the player in the game.
     */
    public int getNumberOfCarsForGivenPlayer(PlayerId plr){
        Preconditions.checkArgument(numberCarsForEachPlayer.containsKey(plr));
        return numberCarsForEachPlayer.get(plr).get();
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
     * Used to get the number of construct points of a given player.
     *
     * @param plr player we want to know his number of construct points.
     * @return the number of construct points of the given player (int).
     * @throws IllegalArgumentException if the given player isn't one of the player in the game.
     */
    public int getNumberOfConstructPointsForGivenPlayer(PlayerId plr){
        Preconditions.checkArgument(numberConstructsPointsForEachPlayer.containsKey(plr));
        return numberConstructsPointsForEachPlayer.get(plr).get();
    }

    /**
     * Used to get the observable list of the tickets of the player.
     *
     * @return the observable list of the ticket (ObservableList).
     */
    public ObservableList<Ticket> TicketsOfPlayerProperty() {
        return ticketsOfPlayer;
    }

    /**
     * Used to get the given ticket from the tickets of the player
     *
     * @param slot of the ticket.
     * @return the ticket (Ticket).
     * @throws IllegalArgumentException if the slot is bigger than the size of the list.
     */
    public Ticket getTicketOfPlayer(int slot){
        Preconditions.checkArgument(ticketsOfPlayer.size()>slot);
        return ticketsOfPlayer.get(slot);

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
     * Used to get the number of cards possessed by the player for a given type.
     *
     * @param type type of cards we want to know.
     * @return the number of cards possessed by the player for the given type (int).
     * @throws IllegalArgumentException if the type isn't one of the Card in the game.
     */
    public int getNumberOfPlayerCardsForGivenType(Card type){
        Preconditions.checkArgument(numberOfCardsForEachType.containsKey(type));
        return numberOfCardsForEachType.get(type).get();
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
     * Used to get if the player has the given route.
     *
     * @param route we want to know if the player has it.
     * @return true if the player has the route (boolean).
     * @throws IllegalArgumentException if the route isn't in the map.
     */
    public boolean getPlayerHasGivenRoute(Route route){
        Preconditions.checkArgument(playerHasGivenRoute.containsKey(route));
        return playerHasGivenRoute.get(route).get();
    }

    private static List<SimpleObjectProperty<Card>> createFaceUpCards(){
        List<SimpleObjectProperty<Card>> listOfProper = new ArrayList<>();
        for(int i=0; i<Constants.FACE_UP_CARDS_COUNT; ++i) listOfProper.add(new SimpleObjectProperty<Card>(null));

        return listOfProper;
    }

    private static Map<Route, SimpleObjectProperty<PlayerId>> createAllRoutesOwners(){
        Map<Route, SimpleObjectProperty<PlayerId>> map = new HashMap<>();
        for(Route r : ChMap.routes()) map.put(r, new SimpleObjectProperty<PlayerId>(null));

        return map;
    }

    private static <E extends Enum<E>> Map<E, SimpleIntegerProperty> createEnumMapWithNullIntegerProperty(E[] tabOfEnum){
        Map<E, SimpleIntegerProperty> map = new HashMap<>();
        for(E element : tabOfEnum)map.put(element, new SimpleIntegerProperty(0));

        return map;
    }

    private int generatePercents(int number, int total){
        return (int) Math.ceil(((double) number / (double) total)*100);
    }

}
