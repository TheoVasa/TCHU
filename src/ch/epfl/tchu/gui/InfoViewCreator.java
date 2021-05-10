package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

public class InfoViewCreator {
    public static Node createInfoView(PlayerId currentPlayer, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> gameInfos) {
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

        return vbox;
    }

    private static TextFlow generatePlayersStatistic(PlayerId player, String name, ObservableGameState gameState){
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
                Bindings.format(StringsFr.PLAYER_STATS, name, gameState.numberOfTicketsForGivenPlayerProperty(player), gameState.numberOfCardsForGivenPlayerProperty(player),
                        gameState.numberOfCarsForGivenPlayerProperty(player), gameState.numberConstructsPointsForGivenPlayerProperty(player))
        );
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
