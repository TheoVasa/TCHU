package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Game {

    /**
     * Attributes
     */
    private static Map<PlayerId, Player> players;
    private static Map<PlayerId, String> playerNames;
    private static SortedBag<Ticket> tickets;
    private static GameState gameState;
    private static Map<PlayerId, Info> infos;
    private static boolean gameEnded = false;

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
        Game.tickets = tickets;
        Game.gameState = GameState.initial(tickets, rng);

        //Init the game
        initGame();

        //Play the game --> use while ?
        while (!gameEnded)
            playTurn();

        endGame();
    }

    /**
     * Initialize the game on the beginning
     */
    private static void initGame(){
        //Init infos and players
        infos.put(PlayerId.PLAYER_1, new Info(playerNames.get(PlayerId.PLAYER_1)));
        infos.put(PlayerId.PLAYER_2, new Info(playerNames.get(PlayerId.PLAYER_2)));
        players.get(PlayerId.PLAYER_1).initPlayers(PlayerId.PLAYER_1, playerNames);
        players.get(PlayerId.PLAYER_2).initPlayers(PlayerId.PLAYER_2, playerNames);

        //Inform who will play first
        receiveInfo(infos.get(gameState.currentPlayerId()).willPlayFirst());

        //Set initial
        players.get(PlayerId.PLAYER_1).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        players.get(PlayerId.PLAYER_1).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        SortedBag chosenTicketsPlayer1 = players.get(PlayerId.PLAYER_1).chooseInitialTickets();
        SortedBag chosenTicketsPlayer2 = players.get(PlayerId.PLAYER_1).chooseInitialTickets();
        gameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTicketsPlayer1);
        gameState.withInitiallyChosenTickets(PlayerId.PLAYER_2, chosenTicketsPlayer2);

        //Give info about chosen tickets
        receiveInfo(infos.get(PlayerId.PLAYER_2).keptTickets(chosenTicketsPlayer1.size()));
        receiveInfo(infos.get(PlayerId.PLAYER_1).keptTickets(chosenTicketsPlayer2.size()));
    }

    /**
     * Player plays the current turn of the game
     */
    private static void playTurn(){
        PlayerId id = gameState.currentPlayerId();

        //Send info that the player can play --> turn begins
        receiveInfo(infos.get(id).canPlay());

        switch (players.get(id).nextTurn()){
            case DRAW_TICKETS:
                //Send info that the player drew tickets
                receiveInfo(infos.get(id).drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                SortedBag drawnTickets  = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                SortedBag keptTickets = players.get(id).chooseTickets(drawnTickets);

                //Send info that the player kept some tickets
                receiveInfo(infos.get(id).keptTickets(keptTickets.size()));
                break;
            case DRAW_CARDS:
                //Ask twice which card the current player wants
                for (int i = 0; i < 2; ++i){
                    int cardSlot = players.get(id).drawSlot();
                    if (cardSlot == Constants.DECK_SLOT){
                        //Send info that the player drew from deck
                        receiveInfo(infos.get(id).drewBlindCard());
                        gameState.withBlindlyDrawnCard();
                    } else if (Constants.FACE_UP_CARD_SLOTS.contains(cardSlot)){
                        //Send info that the player drew from faced up cards
                        receiveInfo(infos.get(id).drewVisibleCard(gameState.cardState().faceUpCard(cardSlot)));
                        gameState.withDrawnFaceUpCard(cardSlot);
                    }
                }
                break;
            case CLAIM_ROUTE:
                Route claimRoute = players.get(id).claimedRoute();
                SortedBag claimCards = players.get(id).initialClaimCards();

                if (claimRoute.level().equals(Route.Level.OVERGROUND)){
                    gameState.withClaimedRoute(claimRoute, claimCards);
                } else {
                    //Send info that the player attempts to take an underground route
                    receiveInfo(infos.get(id).attemptsTunnelClaim(claimRoute, claimCards));

                    //Take the tree first cards of the deck
                    SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder();
                    for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS && gameState.canDrawCards(); ++i) {
                        gameState.topCard();
                        drawnCardsBuilder.add(gameState.topCard());
                        gameState = gameState.withoutTopCard();
                    }

                    SortedBag drawnCards = drawnCardsBuilder.build();
                    int additionalCardsCount = claimRoute.additionalClaimCardsCount(claimCards, drawnCards);
                    List possibleAddCards = gameState.currentPlayerState().possibleAdditionalCards(additionalCardsCount, claimCards, drawnCards);
                    SortedBag additionalCardsPlayed = players.get(id).chooseAdditionalCards(possibleAddCards);

                    //Send message to inform which card has been drawn
                    receiveInfo(infos.get(id).drewAdditionalCards(drawnCards, additionalCardsCount));



                    //Update all the cards he used to claim route
                    SortedBag.Builder<Card> claimCardsBuilder = new SortedBag.Builder<>();
                    claimCardsBuilder.add(claimCards);
                    claimCardsBuilder.add(additionalCardsPlayed);
                    claimCards = claimCardsBuilder.build();
                }

                //Send info that the player toke a route
                receiveInfo(infos.get(id).claimedRoute(claimRoute, claimCards));
                break;
            default:
                break; //do nothing
        }
    }

    /**
     * End the game (send infos, count points...)
     */
    private static void endGame(){

    }

    /**
     *
     * @param info
     */
    private static void receiveInfo(String info){
        players.get(PlayerId.PLAYER_1).receiveInfo(info);
        players.get(PlayerId.PLAYER_2).receiveInfo(info);
    }
}
