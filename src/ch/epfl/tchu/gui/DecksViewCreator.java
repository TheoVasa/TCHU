package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DecksViewCreator {

    /**
     * Create the view of the hand of a player
     * @param obsGameState the observable state of the game
     * @return the view of the hand of a player that can be displayed
     */
    public static HBox createHandView(ObservableGameState obsGameState){
        //Create th root node of he handView
        HBox handView = new HBox();
        handView.getStylesheets().addAll("decks.css", "colors.css");

        //Add the tickets and the cards to the handView
        ListView tickets = createHandTicketsLayout(obsGameState);
        HBox cards = createHandCardsLayout(obsGameState);
        cards.setId("hand-pane");
        handView.getChildren().add(tickets);
        handView.getChildren().add(cards);

        return handView;
    }

    /*
     */
    public static VBox createCardsView(ObservableGameState obsGameState){
        //Create the two buttons ()
        Button buttonTickets = createButton(obsGameState, "Tickets");
        Button buttonCards = createButton(obsGameState, "Cards");

        //Create the layout of the cards
        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("decks.css", "colors.css");
        vBox.setId("card-pane");
        vBox.getChildren().add(createFacedUpCardsLayout(obsGameState));


        return vBox;
    }

    private static Button createButton(ObservableGameState observableGameState, String name){

        return null;
    }

    //Create the view of the card of that the player
    //has in its hands and create the layout of them
    private static HBox createHandCardsLayout(ObservableGameState obsGameState){
        //Create a HBox with the correct css id
        HBox handCardsLayout = new HBox();
        handCardsLayout.setId("hand-pane");

        //Create the layout of the card of the players and put them in the handCardsLayout
        for (Card c: Card.ALL){
            ReadOnlyIntegerProperty count = obsGameState.numberOfPlayerCardsForGivenTypeProperty(c);
            StackPane singleCard = createCardWithText(obsGameState, c, count);
            singleCard.getStyleClass().addAll((c == Card.LOCOMOTIVE) ? "NEUTRAL" : c.color().name(), "card");
            singleCard.visibleProperty().bind(Bindings.greaterThan(count, 0));
            handCardsLayout.getChildren().add(singleCard);
        }
        return handCardsLayout;
    }

    //Create the view of the tickets and
    //put them in the right layout
    private static ListView<Ticket> createHandTicketsLayout(ObservableGameState observableGameState){
        ListView<Ticket> ticketsView = new ListView<>(observableGameState.ticketsOfPlayerProperty());
        ticketsView.setId("tickets");
        return ticketsView;
    }

    private static Node createCard(ObservableGameState obsGameState, Card type){
        //Create the border of the card
        Rectangle border = new Rectangle(60, 90);
        border.getStyleClass().add("outside");

        //Create the filled color of th card
        Rectangle fill = new Rectangle(40, 70);
        fill.getStyleClass().addAll( "filled", "inside");

        //Create the image of the card (loco or cars)
        Rectangle imageRectangle = new Rectangle(40, 70);
        Image image = new Image( (type == Card.LOCOMOTIVE)
                                 ? "locomotive.png"
                                 : "train-car.png");
        imageRectangle.setFill(new ImagePattern(image));
        imageRectangle.getStyleClass().add("train-image");

        //Bind them together to create a card and return it
        Group card = new Group(border, fill, imageRectangle);
        return card;
    }

    //Create a card with the text for the
    //representation of the card of the player
    private static StackPane createCardWithText(ObservableGameState obsGameState, Card type, ReadOnlyIntegerProperty count){
        //Create the text for the number of the cards
        Text cardText = new Text();
        cardText.getStyleClass().add("count");
        cardText.setText(String.valueOf(count.get()));
        cardText.textProperty().bind(Bindings.convert(count));

        //Create the StackPane that will represent a cards in the players hand (with number)
        StackPane cardsWithNumber = new StackPane(new Group(createCard(obsGameState, type), cardText));
        return cardsWithNumber;
    }

    private static Node createFacedUpCardsLayout(ObservableGameState obsGameState){
        Group facedUpCards = new Group();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i){
            Card type = obsGameState.faceUpCardsProperty(i).get();
            StackPane card = new StackPane(createCard(obsGameState, type));
            card.getStyleClass().addAll((type == Card.LOCOMOTIVE || type == null) ? "NEUTRAL" : type.color().name(), "card");
            facedUpCards.getChildren().add(card);
        }
        return facedUpCards;
    }
}
