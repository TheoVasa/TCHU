package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * This class creates the view f the deck (draw tickets button, faced up cards, draw cards button)
 * and of the hand of the player (his cards and his tickets).
 * It is package private, immutable and non instantiable.
 *
 * @author Théo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
class DecksViewCreator {

    //Dimension of the cards and button
    private static final int CARD_BORDER_HEIGHT = 90;
    private static final int CARD_BORDER_WIDTH  = 60;
    private static final int CARD_FILL_HEIGHT   = 70;
    private static final int CARD_FILL_WIDTH    = 40;
    private static final int BUTTON_GAUGE_HEIGHT = 5;
    private static final int BUTTON_GAUGE_WIDTH = 50;

    //Non instantiable
    private DecksViewCreator(){}

    /**
     * Create the view of the hand of a player,
     * this view draw the tickets and the cards that a player possesses.
     *
     * @param obsGameState the observable state of the game
     * @return the view of the hand of a player that can be displayed
     */
    public static HBox createHandView(ObservableGameState obsGameState){
        //Create th root node of he handView
        HBox handView = new HBox();
        handView.getStylesheets().addAll("decks.css", "colors.css");

        //Add the tickets and the cards to the handView
        ListView<Ticket> tickets = createHandTicketsLayout(obsGameState);
        HBox cards = createHandCardsLayout(obsGameState);
        handView.getChildren().add(tickets);
        handView.getChildren().add(cards);

        return handView;
    }

    /**
     * Creates the view of the cards,
     * this view contains two button (draw tickets and draw cards) and
     * the five faced up cards that can also be drawn.
     *
     * @param obsGameState the observable game state
     * @param drawTicketsHandlerProperty the handler property for the tickets button
     * @param drawCardHandlerProperty the handler property for the cards button
     * @return the view of the cards and tickets that can be drawn
     */
    public static VBox createCardsView(ObservableGameState obsGameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> drawTicketsHandlerProperty,
                                       ObjectProperty<ActionHandler.DrawCardHandler> drawCardHandlerProperty){
        //Create the top node of the cardsView
        VBox cardViewLayout = new VBox();
        cardViewLayout.getStylesheets().addAll("decks.css", "colors.css");
        cardViewLayout.setId("card-pane");

        //Create the button for the cards
        Button buttonTickets = createButton("Billets", obsGameState.restingTicketsPercentsProperty());
        buttonTickets.disableProperty().bind(drawTicketsHandlerProperty.isNull());
        buttonTickets.setOnAction(e -> drawTicketsHandlerProperty.get().onDrawTickets());
        cardViewLayout.getChildren().add(buttonTickets);

        //Create the layout for the faceUpCards
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i){
            //Add child(facedUpCard) to VBox
            Card type = obsGameState.faceUpCardsProperty(i).get();
            StackPane card = createCard(type);
            card.disableProperty().bind(drawCardHandlerProperty.isNull());
            int finalI = i;
            card.setOnMouseClicked((e) -> drawCardHandlerProperty.get().onDrawCard(finalI));
            cardViewLayout.getChildren().add(card);

            //Add a listener to the card
            obsGameState.faceUpCardsProperty(i).addListener((p, o, n) ->
                    card.getStyleClass().setAll((n.equals(Card.LOCOMOTIVE)) ? "NEUTRAL" : p.getValue().color().name(), "card")
            );
        }

        //Create the button for the cards
        Button buttonDeck = createButton("Cartes", obsGameState.restingCardsPercentsProperty());
        buttonDeck.disableProperty().bind(drawCardHandlerProperty.isNull());
        buttonDeck.setOnAction(e -> drawCardHandlerProperty.get().onDrawCard(Constants.DECK_SLOT));
        cardViewLayout.getChildren().add(buttonDeck);

        return cardViewLayout;
    }

    //Create the view of the card of that the player
    //has in its hands and create the layout of them.
    private static HBox createHandCardsLayout(ObservableGameState obsGameState){
        //Create a HBox with the correct css id
        HBox handCardsLayout = new HBox();
        handCardsLayout.setId("hand-pane");

        //Create the layout of the card of the players and put them in the handCardsLayout
        for (Card c: Card.ALL){
            ReadOnlyIntegerProperty count = obsGameState.numberOfPlayerCardsForGivenTypeProperty(c);
            StackPane singleCard = createCardWithText(c, count);
            singleCard.visibleProperty().bind(Bindings.greaterThan(count, 0));
            handCardsLayout.getChildren().add(singleCard);
        }
        return handCardsLayout;
    }

    //Create the view of the tickets and
    //put them in the right layout.
    private static ListView<Ticket> createHandTicketsLayout(ObservableGameState observableGameState){
        ListView<Ticket> ticketsView = new ListView<>(observableGameState.ticketsOfPlayerProperty());
        ticketsView.setId("tickets");
        return ticketsView;
    }

    //This method creates a card.
    private static StackPane createCard(Card type){
        //Create the border of the card
        Rectangle border = new Rectangle(CARD_BORDER_WIDTH, CARD_BORDER_HEIGHT);
        border.getStyleClass().add("outside");

        //Create the filled color of th card
        Rectangle filled = new Rectangle(CARD_FILL_WIDTH, CARD_FILL_HEIGHT);
        filled.getStyleClass().addAll( "filled", "inside");

        //Create the image of the card (loco or cars)
        Rectangle imageRectangle = new Rectangle(CARD_FILL_WIDTH, CARD_FILL_HEIGHT);
        Image image = new Image( (type == Card.LOCOMOTIVE)
                                 ? "locomotive.png"
                                 : "train-car.png");
        imageRectangle.setFill(new ImagePattern(image));
        imageRectangle.getStyleClass().add("train-image");

        //Bind them together to create a card and return it
        return new StackPane(border, filled, imageRectangle);
    }

    //Create a card with the text for the
    //representation of the card of the player.
    private static StackPane createCardWithText(Card type, ReadOnlyIntegerProperty count){
        //Create the text for the number of the cards
        Text cardText = new Text();
        cardText.getStyleClass().add("count");
        cardText.textProperty().bind(Bindings.convert(count));
        cardText.visibleProperty().bind(Bindings.greaterThan(count, 1));

        //Create the StackPane that will represent a cards in the players hand (with number)
        StackPane cardsWithNumber = createCard(type);
        cardsWithNumber.getStyleClass().addAll((type == Card.LOCOMOTIVE) ? "NEUTRAL": type.color().name(), "card");
        cardsWithNumber.getChildren().add(cardText);
        return cardsWithNumber;
    }

    //This method creates a button.
    private static Button createButton(String name, ReadOnlyIntegerProperty percentageProperty){
        //Create the gauge
        Rectangle backgroundGauge = new Rectangle(BUTTON_GAUGE_WIDTH, BUTTON_GAUGE_HEIGHT);
        backgroundGauge.getStyleClass().add("background");
        Rectangle foregroundGauge = new Rectangle(BUTTON_GAUGE_WIDTH, BUTTON_GAUGE_HEIGHT);
        foregroundGauge.getStyleClass().add("foreground");
        foregroundGauge.widthProperty().bind(percentageProperty.multiply(50).divide(100));
        Group group = new Group(backgroundGauge, foregroundGauge);

        //Create button
        Button button = new Button();
        button.getStyleClass().add("gauged");
        button.setGraphic(group);
        button.setText(name);

        return button;
    }
}
