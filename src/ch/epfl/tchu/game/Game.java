package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import com.sun.tools.jconsole.JConsoleContext;

import java.util.*;

public final class Game {

    /**
     * Attributes
     */
    private static Map<PlayerId, Player> players;
    private static Map<PlayerId, String> playerNames;
    private static GameState gameState;
    private static Map<PlayerId, Info> infos;
    private static boolean gameEnded;

    /**
     *
     * @param players
     * @param playerNames
     * @param tickets
     * @param rng
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        //Check correctness of the arguments
        Preconditions.checkArgument(players.size() == 2);
        Preconditions.checkArgument(playerNames.size() == 2);

        //Init vars from parameters
        Game.players = Collections.unmodifiableMap(players);
        Game.playerNames = Collections.unmodifiableMap(playerNames);
        Game.gameState = GameState.initial(tickets, rng);
        Game.gameEnded = false;

        infos = new EnumMap<>(PlayerId.class);
        infos.put(PlayerId.PLAYER_1, new Info(playerNames.get(PlayerId.PLAYER_1)));
        infos.put(PlayerId.PLAYER_2, new Info(playerNames.get(PlayerId.PLAYER_2)));

        //Init the game
        initGame();

        //Play the game
        while (!gameEnded)
            playTurn(rng);

        endGame();
    }

    /**
     * Initialize the game on the beginning
     */
    private static void initGame(){
        //Init players
        players.get(PlayerId.PLAYER_1).initPlayers(PlayerId.PLAYER_1, playerNames);
        players.get(PlayerId.PLAYER_2).initPlayers(PlayerId.PLAYER_2, playerNames);

        //Inform who will play first
        receiveInfo(infos.get(gameState.currentPlayerId()).willPlayFirst());

        //Update
        updateState();

        //Set initial
        players.get(PlayerId.PLAYER_1).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        players.get(PlayerId.PLAYER_1).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        SortedBag chosenTicketsPlayer1 = players.get(PlayerId.PLAYER_1).chooseInitialTickets();
        SortedBag chosenTicketsPlayer2 = players.get(PlayerId.PLAYER_1).chooseInitialTickets();
        gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTicketsPlayer1);
        gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_2, chosenTicketsPlayer2);

        //Give info about chosen tickets (first the info about the current player)
        receiveInfo(infos.get(PlayerId.PLAYER_1).keptTickets(chosenTicketsPlayer1.size()));
        receiveInfo(infos.get(PlayerId.PLAYER_2).keptTickets(chosenTicketsPlayer2.size()));
    }

    /**
     * Player plays the current turn of the game
     *
     * @param rng The random number used to mix the deck
     */
    private static void playTurn(Random rng){
        PlayerId id = gameState.currentPlayerId();

        //Send info that the player can play --> turn begins
        receiveInfo(infos.get(id).canPlay());

        //Update
        updateState();

        //Next turn
        switch (players.get(id).nextTurn()){
            case DRAW_TICKETS:
                //Send info that the player drew tickets
                receiveInfo(infos.get(id).drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                SortedBag drawnTickets  = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                SortedBag keptTickets = players.get(id).chooseTickets(drawnTickets);
                gameState = gameState.withoutTopTickets(Constants.IN_GAME_TICKETS_COUNT);

                //Send info that the player kept some tickets
                receiveInfo(infos.get(id).keptTickets(keptTickets.size()));
                break;
            case DRAW_CARDS:
                //Ask twice which card the current player wants
                for (int i = 0; i < 2 && gameState.canDrawCards(); ++i){
                    //Update
                    if (i == 1)
                        updateState();

                    //Recreate the deck from discard if needed
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                    //Player draw from faced up cards or deck
                    int cardSlot = players.get(id).drawSlot();
                    if (cardSlot == Constants.DECK_SLOT){
                        //Send info that the player drew from deck
                        receiveInfo(infos.get(id).drewBlindCard());
                        gameState = gameState.withBlindlyDrawnCard();
                    } else if (Constants.FACE_UP_CARD_SLOTS.contains(cardSlot)) {
                        //Send info that the player drew from faced up cards
                        receiveInfo(infos.get(id).drewVisibleCard(gameState.cardState().faceUpCard(cardSlot)));
                        gameState = gameState.withDrawnFaceUpCard(cardSlot);
                    }
                }
                break;
            case CLAIM_ROUTE:
                Route claimRoute = players.get(id).claimedRoute();
                SortedBag claimCards = players.get(id).initialClaimCards();

                if (claimRoute.level().equals(Route.Level.OVERGROUND)){
                    gameState = gameState.withClaimedRoute(claimRoute, claimCards);
                    //Send info that the player toke a route
                    receiveInfo(infos.get(id).claimedRoute(claimRoute, claimCards));
                } else {
                    //Send info that the player attempts to take an underground route
                    receiveInfo(infos.get(id).attemptsTunnelClaim(claimRoute, claimCards));

                    //Take the three first cards of the deck
                    SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder();
                    for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS && gameState.canDrawCards(); ++i) {
                        //Recreate deck from discard if needed
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        //Take top cards and add it to the drawn cards
                        gameState.topCard();
                        drawnCardsBuilder.add(gameState.topCard());
                        gameState = gameState.withoutTopCard();
                    }

                    //Handle the additional cards to play
                    SortedBag drawnCards = drawnCardsBuilder.build();
                    int additionalCardsCount = claimRoute.additionalClaimCardsCount(claimCards, drawnCards);

                    //Send message to inform which card has been drawn
                    receiveInfo(infos.get(id).drewAdditionalCards(drawnCards, additionalCardsCount));

                    //Determine if the player can/want to play the additional cards
                    List possibleAddCards = gameState.currentPlayerState().possibleAdditionalCards(additionalCardsCount, claimCards, drawnCards);
                    SortedBag additionalCardsPlayed = players.get(id).chooseAdditionalCards(possibleAddCards);

                    if (additionalCardsPlayed.size() > 0){
                        //Update all the cards he used to claim route
                        SortedBag.Builder<Card> claimCardsBuilder = new SortedBag.Builder<>();
                        claimCardsBuilder.add(claimCards);
                        claimCardsBuilder.add(additionalCardsPlayed);
                        claimCards = claimCardsBuilder.build();
                        gameState = gameState.withClaimedRoute(claimRoute, claimCards);

                        //Send info that the player toke a route
                        receiveInfo(infos.get(id).claimedRoute(claimRoute, claimCards));

                    } else {
                        //Send info that the player did not take a route
                        receiveInfo(infos.get(id).didNotClaimRoute(claimRoute));
                    }

                    //Put the drawn cards in the discard
                    gameState.withMoreDiscardedCards(drawnCards);
                }
                break;
            default:
                break; //do nothing
        }

        //If last turn begins draw message
        if (gameState.lastTurnBegins())
            receiveInfo(infos.get(id).lastTurnBegins(gameState.currentPlayerState().carCount()));

        //Game state for the next turn
        gameState = gameState.forNextTurn();
    }

    /**
     * End the game (send infos, count points...)
     */
    private static void endGame(){
        //Update
        updateState();

        int longestTrailPlayer1 = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes()).length();
        int longestTrailPlayer2 = Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes()).length();

        if (longestTrailPlayer1 > longestTrailPlayer2){


        }else if (longestTrailPlayer2 > longestTrailPlayer1){

        } else { //Both have the longest trail

        }

    }

    /**
     *
     * @param info
     */
    private static void receiveInfo(String info){
        players.get(PlayerId.PLAYER_1).receiveInfo(info);
        players.get(PlayerId.PLAYER_2).receiveInfo(info);
    }

    /**
     * Update the state of the player
     */
    private static  void updateState(){
        players.get(PlayerId.PLAYER_1).updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
        players.get(PlayerId.PLAYER_2).updateState(gameState, gameState.playerState(PlayerId.PLAYER_2));
    }
}
