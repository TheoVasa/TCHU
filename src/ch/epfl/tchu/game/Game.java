package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.Collections;
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

        //Play the game
        while(true){
            playTurn();
            break;
        }

        //End game
        endGame();
    }

    private static void initGame(){
        //Init infos and players
        infos.put(PlayerId.PLAYER_1, new Info(playerNames.get(PlayerId.PLAYER_1)));
        infos.put(PlayerId.PLAYER_2, new Info(playerNames.get(PlayerId.PLAYER_2)));
        players.get(PlayerId.PLAYER_1).initPlayers(PlayerId.PLAYER_1, playerNames);
        players.get(PlayerId.PLAYER_2).initPlayers(PlayerId.PLAYER_2, playerNames);

        //Inform who will play first
        players.get(PlayerId.PLAYER_1).receiveInfo(infos.get(gameState.currentPlayerId()).willPlayFirst());
        players.get(PlayerId.PLAYER_2).receiveInfo(infos.get(gameState.currentPlayerId()).willPlayFirst());

        //Set initial
        players.get(PlayerId.PLAYER_1).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        players.get(PlayerId.PLAYER_1).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        SortedBag chosenTicketsPlayer1 = players.get(PlayerId.PLAYER_1).chooseInitialTickets();
        SortedBag chosenTicketsPlayer2 = players.get(PlayerId.PLAYER_1).chooseInitialTickets();
        gameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTicketsPlayer1);
        gameState.withInitiallyChosenTickets(PlayerId.PLAYER_2, chosenTicketsPlayer2);

        //Give info about chosen tickets
        players.get(PlayerId.PLAYER_1).receiveInfo(infos.get(PlayerId.PLAYER_2).keptTickets(chosenTicketsPlayer1.size()));
        players.get(PlayerId.PLAYER_2).receiveInfo(infos.get(PlayerId.PLAYER_1).keptTickets(chosenTicketsPlayer2.size()));
    }

    private static void playTurn(){

        PlayerId id = gameState.currentPlayerId();

        switch (players.get(id).nextTurn()){
            case DRAW_TICKETS:

                break;
            case DRAW_CARDS:
                //Ask twice which card the current player wants
                for (int i = 0; i < 2; ++i){
                    int cardSlot = players.get(id).drawSlot();
                    if (cardSlot == Constants.DECK_SLOT){
                        gameState.withBlindlyDrawnCard();
                    } else if (Constants.FACE_UP_CARD_SLOTS.contains(cardSlot)){
                        gameState.withDrawnFaceUpCard(cardSlot);
                    }
                }
                break;
            case CLAIM_ROUTE:
                Route claimRoute = players.get(id).claimedRoute();
                SortedBag claimCards = players.get(id).initialClaimCards();

                if (claimRoute.level().equals(Route.Level.OVERGROUND)){

                } else {

                }


                break;
            default:
                break; //do nothing
        }
    }

    private static void endGame(){

    }

}
