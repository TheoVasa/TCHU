package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * Represent the game of tChu, final, immutable
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class Game {
    //The two players of the game
    private static Map<PlayerId, Player> players;
    //The name of the players
    private static Map<PlayerId, String> playerNames;
    //The state of the game
    private static GameState gameState;
    //The information that will be sent to the players during the game
    private static Map<PlayerId, Info> infos;

    private Game() {
        //this class is non instanciable
    }

    /**
     * Loop of the game that will make the game run.
     *
     * @param players     the two players of the game
     * @param playerNames the names of the two players
     * @param tickets     the initial tickets in the games
     * @param rng         the random number to generate a shuffles deck
     * @throws IllegalArgumentException if the <code>players</code> and <code>playerNames</code>
     *                                  does not contain exactly 2 players
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        //Check correctness of the arguments
        Preconditions.checkArgument(players.size() == PlayerId.COUNT);
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);

        //Init vars from parameters
        Game.players = players;
        Game.playerNames = Map.copyOf(playerNames);
        Game.gameState = GameState.initial(tickets, rng);

        infos = new EnumMap<>(PlayerId.class);
        infos.put(PlayerId.PLAYER_1, new Info(playerNames.get(PlayerId.PLAYER_1)));
        infos.put(PlayerId.PLAYER_2, new Info(playerNames.get(PlayerId.PLAYER_2)));

        //Init the game
        initGame();

        //Play the game
        boolean endGame = false;
        while (!endGame) {
            if (gameState.lastPlayer() == gameState.currentPlayerId())
                endGame = true;
            playTurn(rng);
        }

        endGame();
    }

    //Initialize the game
    private static void initGame() {
        //Init the players
        for (PlayerId id : PlayerId.ALL)
            players.get(id).initPlayers(id, playerNames);

        //Inform who will play first
        receiveInfo(infos.get(gameState.currentPlayerId()).willPlayFirst());

        //Give initial tickets choice to the players
        updateState();
        for (PlayerId id : PlayerId.ALL)
            players.get(id).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));

        //handle the tickets choice of the player (put them in their hand)
        updateState();
        EnumMap<PlayerId, SortedBag<Ticket>> chosenTicketsPlayer = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL){
            chosenTicketsPlayer.put(id ,players.get(id).chooseInitialTickets());
            gameState = gameState.withInitiallyChosenTickets(id, chosenTicketsPlayer.get(id));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        //Give info about chosen tickets (first the info about the current player)
        for (PlayerId id: PlayerId.ALL)
            receiveInfo(infos.get(PlayerId.PLAYER_1).keptTickets(chosenTicketsPlayer.get(id).size()));
    }

    //Make the current player play a turn
    private static void playTurn(Random rng) {
        PlayerId id = gameState.currentPlayerId();

        //Send info that the player can play --> turn begins
        receiveInfo(infos.get(id).canPlay());

        //Update
        updateState();

        //Next turn
        switch (players.get(id).nextTurn()) {
            case DRAW_TICKETS:
                //Send info that the player drew tickets
                receiveInfo(infos.get(id).drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                //chose tickets
                SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                SortedBag<Ticket> keptTickets = players.get(id).chooseTickets(drawnTickets);
                gameState = gameState.withChosenAdditionalTickets(drawnTickets, keptTickets);

                //Send info that the player kept some tickets
                receiveInfo(infos.get(id).keptTickets(keptTickets.size()));
                break;
            case DRAW_CARDS:
                //Ask twice which card the current player wants
                for (int i = 0; i < 2 && gameState.canDrawCards(); ++i) {
                    //Update
                    if (i == 1)
                        updateState();

                    //Recreate the deck from discard if needed
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                    //Player draw from faced up cards or deck
                    int cardSlot = players.get(id).drawSlot();
                    if (cardSlot == Constants.DECK_SLOT) {
                        //Send info that the player drew from deck
                        receiveInfo(infos.get(id).drewBlindCard());
                        gameState = gameState.withBlindlyDrawnCard();
                    } else {
                        //Send info that the player drew from faced up cards
                        receiveInfo(infos.get(id).drewVisibleCard(gameState.cardState().faceUpCard(cardSlot)));
                        gameState = gameState.withDrawnFaceUpCard(cardSlot);
                    }
                }
                break;
            case CLAIM_ROUTE:
                Route claimRoute = players.get(id).claimedRoute();
                SortedBag<Card> claimCards = players.get(id).initialClaimCards();

                if (claimCards.size() > 0) {
                    if (claimRoute.level().equals(Route.Level.OVERGROUND)) {
                        gameState = gameState.withClaimedRoute(claimRoute, claimCards);
                        //Send info that the player toke a route
                        receiveInfo(infos.get(id).claimedRoute(claimRoute, claimCards));
                    } else {
                        //Send info that the player attempts to take an underground route
                        receiveInfo(infos.get(id).attemptsTunnelClaim(claimRoute, claimCards));

                        //Take the three first cards of the deck
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS && gameState.canDrawCards(); ++i) {
                            //Recreate deck from discard if needed
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            //Take top cards and add it to the drawn cards
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }

                        //Handle the additional cards to play
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                        int additionalCardsCount = (drawnCards.size() > 0)
                                ? claimRoute.additionalClaimCardsCount(claimCards, drawnCards)
                                : 0;

                        //Send message to inform which card has been drawn
                        receiveInfo(infos.get(id).drewAdditionalCards(drawnCards, additionalCardsCount));

                        //Determine all possibilities to play additional cards (if needed)
                        List<SortedBag<Card>> possibleAddCards = (additionalCardsCount > 0)
                                ? gameState.currentPlayerState().possibleAdditionalCards(additionalCardsCount, claimCards, drawnCards)
                                : List.of();

                        //Chose cards to play if he can
                        SortedBag<Card> additionalCardsPlayed = (possibleAddCards.size() > 0)
                                ? players.get(id).chooseAdditionalCards(possibleAddCards)
                                : SortedBag.of();

                        //If player decides to play, else send a message that he does not want to play
                        if (additionalCardsPlayed.size() > 0 || additionalCardsCount == 0) {
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
                        gameState = gameState.withMoreDiscardedCards(drawnCards);
                    }
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

    //End the game (send infos, count points...)
    private static void endGame() {
        //Update
        updateState();

        //Get the final points of the players
        int finalPointsCountPlayer1 = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
        int finalPointsCountPlayer2 = gameState.playerState(PlayerId.PLAYER_2).finalPoints();

        //Generate bonus for longest trail
        Trail longestTrailPlayer1 = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes());
        Trail longestTrailPlayer2 = Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes());

        //Send info for the bonus of the longest trail
        if (longestTrailPlayer1.length() > longestTrailPlayer2.length() ||
                longestTrailPlayer1.length() == longestTrailPlayer2.length()) {

            receiveInfo(infos.get(PlayerId.PLAYER_1).getsLongestTrailBonus(longestTrailPlayer1));
            finalPointsCountPlayer1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        }
        if (longestTrailPlayer2.length() > longestTrailPlayer1.length() ||
                longestTrailPlayer1.length() == longestTrailPlayer2.length()) {

            receiveInfo(infos.get(PlayerId.PLAYER_2).getsLongestTrailBonus(longestTrailPlayer2));
            finalPointsCountPlayer2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        }

        //Send info for the winner
        if (finalPointsCountPlayer1 > finalPointsCountPlayer2)
            receiveInfo(infos.get(PlayerId.PLAYER_1).won(finalPointsCountPlayer1, finalPointsCountPlayer2));
        else if (finalPointsCountPlayer2 > finalPointsCountPlayer1)
            receiveInfo(infos.get(PlayerId.PLAYER_2).won(finalPointsCountPlayer2, finalPointsCountPlayer1));
        else receiveInfo(Info.draw(new ArrayList<>(playerNames.values()), finalPointsCountPlayer1));

    }

    //make the two player of the game receive an info
    private static void receiveInfo(String info) {
        players.get(PlayerId.PLAYER_1).receiveInfo(info);
        players.get(PlayerId.PLAYER_2).receiveInfo(info);
    }

    //Update the state of the game
    private static void updateState() {
        players.get(PlayerId.PLAYER_1).updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
        players.get(PlayerId.PLAYER_2).updateState(gameState, gameState.playerState(PlayerId.PLAYER_2));
    }
}
