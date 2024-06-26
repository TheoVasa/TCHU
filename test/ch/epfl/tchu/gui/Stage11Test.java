package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.GraphicalPlayerAdapter;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Random;

public final class Stage11Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Map<PlayerId, String> names =
                Map.of(PlayerId.PLAYER_1, "Ada", PlayerId.PLAYER_2, "Charles");
        Map<PlayerId, Player> players =
                Map.of(PlayerId.PLAYER_1, new GraphicalPlayerAdapter(),
                        PlayerId.PLAYER_2, new GraphicalPlayerAdapter());
        Random rng = new Random(2001);
        new Thread(() -> Game.play(players, names, tickets, rng))
                .start();
    }
}