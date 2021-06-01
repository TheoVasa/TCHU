package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * this class generate the part of the graphical interface where all the infos of the players are write, non instantiable and private package.
 *
 * @author Théo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
class InfoViewCreator {

    //private constructor
    private InfoViewCreator(){
        //do nothing, this class is non instantiable
    }

    /**
     * This method generate a node where all the stats, states, and infos in the game are written.
     *
     * @param currentPlayer, the current player in the game.
     * @param playerNames, all the names of the player in the game.
     * @param gameState the current state of the game.
     * @param gameInfos the infos in the game.
     * @return a new Node representing al the infos in the game. (VBox)
     */
    public static VBox createInfoView(PlayerId currentPlayer, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> gameInfos, SimpleBooleanProperty isChatDisplayed) {
        //create the vbox
        VBox vbox = new VBox();
        //set the style sheets
        vbox.getStylesheets().addAll("info.css", "colors.css");

        //create the players statistics
        VBox playersStats = new VBox();
        playersStats.setId("player-stats");
        //the player attached to this interface (draw above the other player)
        playersStats.getChildren().add(generatePlayersStatistic(currentPlayer, playerNames.get(currentPlayer), gameState));
        //the other player
        playersStats.getChildren().add(generatePlayersStatistic(currentPlayer.next(), playerNames.get(currentPlayer.next()), gameState));
        vbox.getChildren().add(playersStats);

        //add the separator
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        vbox.getChildren().add(separator);

        //create the game infos
        vbox.getChildren().add(generateGameInfo(gameInfos));

        //create the show chat button
        Button showInfoButton = new Button("afficher le chat");
        showInfoButton.getStyleClass().add("button");
        showInfoButton.setOnAction((event) -> isChatDisplayed.set(!isChatDisplayed.getValue()));
        vbox.getChildren().add(showInfoButton);
        return vbox;
    }

    private static TextFlow generatePlayersStatistic(PlayerId player, String name, ObservableGameState gameState){
        //the name of the player
        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add(player.toString());

        //set the little circle
        Circle circle = new Circle(5);
        circle.getStyleClass().add("filled");
        textFlow.getChildren().add(circle);

        //generate the statistics
        Text stats = new Text();
        //bind the stats in function of the state of the player
        stats.textProperty().bind(
                Bindings.format(
                        StringsFr.PLAYER_STATS,
                        name,
                        gameState.numberOfTicketsForGivenPlayerProperty(player),
                        gameState.numberOfCardsForGivenPlayerProperty(player),
                        gameState.numberOfCarsForGivenPlayerProperty(player),
                        gameState.numberConstructsPointsForGivenPlayerProperty(player)
                ));
        textFlow.getChildren().add(stats);

        return textFlow;
    }

    private static TextFlow generateGameInfo(ObservableList<Text> gameInfos){
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");

        //set the info in function of the game, in fact the observableList of game infos
        Bindings.bindContent(textFlow.getChildren(), gameInfos);

        return textFlow;
    }

}
