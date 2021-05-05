package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;

import java.util.List;

public class MapViewCreator {

    private final ObservableGameState observableGame;
    private final ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHandlerProperty;

    @FunctionalInterface
    interface CardChooser{
        void chooseCards(List<SortedBag<Card>> options, ActionHandler.ChooseCardsHandler handler);
    }
}
