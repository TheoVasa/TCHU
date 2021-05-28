package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import javafx.scene.control.SelectionMode;

import java.util.*;

/**
 * This class represent the graphical player.
 * It creates the main window and allows the player to take actions in the game.
 *
 * @author Théo Vasarino (313191)
 * @author Selien Wicki (314357)
 */
public final class GraphicalPlayer {

    //Constants
    private static final int IN_GAME_MIN_CHOOSE_CLAIM_CARD_SIZE = 1;
    //Player information
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    //Other attributes
    private final ObservableGameState obsGameState;
    private final ObservableList<Text> infoList;
    //ActionHandler
    private final ObjectProperty<ActionHandler.DrawTicketsHandler> drawTicketsHandlerProperty;
    private final ObjectProperty<ActionHandler.DrawCardHandler> drawCardHandlerProperty;
    private final ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHandlerProperty;
    //Windows
    private final Stage mainWindow;
    private Stage choiceWindow;

    /**
     * Create a graphical player for the given playerId.
     *
     * @param playerId the Id of the player
     * @param playerNames the names of the two player that are playing
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        //Init player attributes
        this.playerId = playerId;
        this.playerNames = Map.copyOf(playerNames);
        //Init observable
        this.obsGameState = new ObservableGameState(playerId);
        infoList = FXCollections.observableArrayList();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i)
            infoList.add(new Text());
        drawTicketsHandlerProperty = new SimpleObjectProperty<>();
        drawCardHandlerProperty = new SimpleObjectProperty<>();
        claimRouteHandlerProperty = new SimpleObjectProperty<>();
        //Init window
        mainWindow = createMainWindow();
        choiceWindow = new Stage();
    }

    /**
     * Set the state of the game and of the player in the ObservableGameState.
     *
     * @param gameState the game state that must be set in the ObservableGameState
     * @param playerState the player state that must be set in the ObservableGameState
     */
    public void setState(PublicGameState gameState, PlayerState playerState){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();
        obsGameState.setState(gameState, playerState);
    }

    /**
     * Receive the infos of the game and put them in the InfoView.
     *
     * @param info the info that will be displayed in the info view
     */
    public void receiveInfo(String info){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        //Put the received info to the last position of the observable list of info
        for (int i = 1; i < infoList.size(); ++i)
            infoList.get(i-1).textProperty().setValue(infoList.get(i).getText());
        infoList.get(infoList.size()-1).textProperty().setValue(info);
    }

    /**
     * Start the (next) turn, this methode allows the player to take an action.
     *
     * @param drawTicketsHandler the ActionHandler to draw a ticket
     * @param drawCardHandler the ActionHandler to draw a card
     * @param claimRouteHandler the ActionHandle to claim a route
     */
    public void startTurn(ActionHandler.DrawTicketsHandler drawTicketsHandler,
                          ActionHandler.DrawCardHandler drawCardHandler,
                          ActionHandler.ClaimRouteHandler claimRouteHandler){

        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        //Change the ticket handler
        this.drawTicketsHandlerProperty.setValue(
                (!obsGameState.canDrawTickets())
                ? null
                : () -> {
                    drawTicketsHandler.onDrawTickets();
                    setActionHandlerOnNull();
                }
        );

        //Change the card handler
        this.drawCardHandlerProperty.setValue(
                (!obsGameState.canDrawCards())
                ? null
                : (i) -> {
                    drawCardHandler.onDrawCard(i);
                    setActionHandlerOnNull();
                }
        );

        //Change the claim route handler
        this.claimRouteHandlerProperty.setValue((route, cards) -> {
            claimRouteHandler.onClaimRoute(route ,cards);
            setActionHandlerOnNull();
        });
    }

    /**
     * This methode will create a choice window to allow the player to choose the tickets he wants to keep.
     *
     * @param options the tickets he could choose
     * @param chooseTicketsHandler handle the action of the player
     */
    public void chooseTickets(SortedBag<Ticket> options,
                              ActionHandler.ChooseTicketsHandler chooseTicketsHandler){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();
        //Check correctness of arguments
        Preconditions.checkArgument(options.size() == 5 || options.size() == 3);

        //Create ListView
        int minChoiceSize = (obsGameState.numberOfTicketsForGivenPlayerProperty(playerId).get() == 0)
                            ? Constants.INITIAL_TICKETS_COUNT - Constants.DISCARDABLE_TICKETS_COUNT
                            : Constants.IN_GAME_TICKETS_COUNT - Constants.DISCARDABLE_TICKETS_COUNT;
        ListView<Ticket> optionsView = new ListView<>(FXCollections.observableArrayList(options.toList()));
        optionsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //Create button
        Button button = new Button("Choisir");
        button.disableProperty().bind(Bindings
                .size(optionsView.getSelectionModel().getSelectedItems())
                .lessThan(minChoiceSize)
        );
        button.setOnAction(
                (event) -> {
                    SortedBag.Builder<Ticket> selection = new SortedBag.Builder<>();
                    optionsView.getSelectionModel().getSelectedItems().forEach(selection::add);
                    chooseTicketsHandler.onChooseTickets(selection.build());
                    choiceWindow.hide();
                }
        );

        //Chose the tickets
        choiceWindow = createChoiceWindow(  StringsFr.TICKETS_CHOICE,
                                            String.format(StringsFr.CHOOSE_TICKETS, minChoiceSize, StringsFr.plural(minChoiceSize)),
                                            optionsView,
                                            button);
        choiceWindow.show();
    }

    /**
     * This methode is used to change the ActionHandler that handle the drawing of a card.
     * This method is essential for the choice of the second card (block all all actions other than drawing a card).
     *
     * @param drawCardHandler handle the action of the player
     */
    public void drawCard(ActionHandler.DrawCardHandler drawCardHandler){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();
        //Change the card handler
        this.drawCardHandlerProperty.setValue(i -> {
                    drawCardHandler.onDrawCard(i);
                    setActionHandlerOnNull();
                }
        );
    }

    /**
     * Create a window that allows the player to chose which cards he wants to player to claim a route.
     *
     * @param options all the possible set of cards that he could play to claim the route.
     * @param chooseCardHandler the ActionHandler to choose a set of cards.
     */
    public void chooseClaimCards(List<SortedBag<Card>> options,
                                 ActionHandler.ChooseCardsHandler chooseCardHandler){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        //Create the ListView
        ListView<SortedBag<Card>> optionsView = new ListView<>(FXCollections.observableArrayList(options));
        optionsView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        //Create the button
        Button button = new Button("Choisir");
        button.setOnAction(
                (event) -> {
                    SortedBag.Builder<Card> selection = new SortedBag.Builder<>();
                    optionsView.getSelectionModel().getSelectedItems().forEach(selection::add);
                    chooseCardHandler.onChooseCards(selection.build());
                    choiceWindow.hide();
                }
        );
        button.disableProperty().bind(Bindings
                .size(optionsView.getSelectionModel().getSelectedItems())
                .lessThan(IN_GAME_MIN_CHOOSE_CLAIM_CARD_SIZE));

        //Chose the claim cards
        choiceWindow = createChoiceWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, optionsView, button);
        choiceWindow.show();
    }

    /**
     * Create a window that allows the player to chose which cards he wants to play as additional cards.
     *
     * @param options all the possible set of cards that he could play as additional card
     * @param chooseCardHandler the ActionHandler to choose a set of cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options,
                                      ActionHandler.ChooseCardsHandler chooseCardHandler){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        //Create the ListView
        ListView<SortedBag<Card>> optionsView = new ListView<>(FXCollections.observableArrayList(options));
        optionsView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        //Create the button
        Button button = new Button("Choisir");
        button.setOnAction(
                (event) -> {
                    SortedBag.Builder<Card> selection = new SortedBag.Builder<>();
                    optionsView.getSelectionModel().getSelectedItems().forEach(selection::add);
                    chooseCardHandler.onChooseCards(selection.build());
                    choiceWindow.hide();
                }
        );
        //Chose the additional cards
        choiceWindow = createChoiceWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS ,optionsView, button);
        choiceWindow.show();
    }

    //Create the main window
    private Stage createMainWindow(){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        //Create the Scene containing the BorderPane with all the 4 different view
        Pane mapView = MapViewCreator.createMapView(obsGameState, claimRouteHandlerProperty, this::chooseClaimCards);
        VBox cardView = DecksViewCreator.createCardsView(obsGameState, drawTicketsHandlerProperty, drawCardHandlerProperty);
        HBox handView = DecksViewCreator.createHandView(obsGameState);
        VBox infoView = InfoViewCreator.createInfoView(playerId, playerNames, obsGameState, infoList);
        Scene scene = new Scene(new BorderPane(mapView, null, cardView, handView, infoView));

        //Create stage of the main window
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(new StringBuilder()
                           .append("tCHu - ")
                           .append(playerNames.get(playerId))
                           .toString());
        stage.show();
        return stage;
    }

    //Create the choice window
    private <T> Stage createChoiceWindow(String title, String action, ListView<T> listView, Button button){
        //Create VBox
        VBox vBox = new VBox();
        //Create the text view
        Text text = new Text(action);
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(text);
        //Add the different element to the choice window (text, options, button)
        vBox.getChildren().add(textFlow);
        vBox.getChildren().add(listView);
        vBox.getChildren().add(button);
        //Create scene
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        //Create the choice window
        Stage choiceWindow = new Stage(StageStyle.UTILITY);
        choiceWindow.setTitle(title);
        choiceWindow.setScene(scene);
        choiceWindow.setOnCloseRequest(Event::consume);
        choiceWindow.initOwner(mainWindow);
        choiceWindow.initModality(Modality.WINDOW_MODAL);

        return choiceWindow;
    }

    //Set the all the action handle to null
    private void setActionHandlerOnNull(){
        drawTicketsHandlerProperty.setValue(null);
        drawCardHandlerProperty.setValue(null);
        claimRouteHandlerProperty.setValue(null);
    }

    //This class is used to translate the english text of a SortedBag in french
    private static final class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
        @Override
        public String toString(SortedBag<Card> object) {
            //Convert english text to french text
            List<String> toString = new ArrayList<>();
            Card.ALL.forEach(c -> {
                if (object.contains(c))
                    toString.add(new StringBuilder()
                                .append(object.countOf(c))
                                .append(" ")
                                .append(Info.cardName(c, object.countOf(c)))
                                .toString());
            });
            return String.join(" et ", toString);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
