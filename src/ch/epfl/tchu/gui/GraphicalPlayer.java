package ch.epfl.tchu.gui;


import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
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

import javax.swing.*;
import java.util.*;

public final class GraphicalPlayer {
    //Constants
    private final int IN_GAME_DISCARABLE_CARD_SIZE = 1;
    private final int INITIAL_DISCARDABLE_TICKETS = 0;

    //Player informations
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    //Other attributes
    private final ObservableGameState obsGameState;
    private final ObservableList<Text> infoList;
    private final ObjectProperty<Boolean> activateSelectionButton;
    //ActionHandler
    private final ObjectProperty<ActionHandler.DrawTicketsHandler> drawTicketsHandlerProperty;
    private final ObjectProperty<ActionHandler.DrawCardHandler> drawCardHandlerProperty;
    private final ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHandlerProperty;
    //Windows
    private final Stage mainWindow;
    private Stage choiceWindow;



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
        activateSelectionButton = new SimpleObjectProperty<>();
        drawTicketsHandlerProperty = new SimpleObjectProperty<>();
        drawCardHandlerProperty = new SimpleObjectProperty<>();
        claimRouteHandlerProperty = new SimpleObjectProperty<>();
        //Init window
        mainWindow = createMainWindow();
        choiceWindow = new Stage();
    }

    public void setState(PublicGameState gameState, PlayerState playerState){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();
        obsGameState.setState(gameState, playerState);
    }

    public void receiveInfo(String info){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        //Put the received info to the last position of the observable list of info
        for (int i = 1; i < infoList.size(); ++i)
            infoList.get(i-1).textProperty().setValue(infoList.get(i).getText());
        infoList.get(infoList.size()-1).textProperty().setValue(info);
    }

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
    private void setActionHandlerOnNull(){
        drawTicketsHandlerProperty.setValue(null);
        drawCardHandlerProperty.setValue(null);
        claimRouteHandlerProperty.setValue(null);
    }

    public void chooseTickets(SortedBag<Ticket> options,
                              ActionHandler.ChooseTicketsHandler chooseTicketsHandler){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        //Check correctness of arguments
        Preconditions.checkArgument(options.size() == 5 || options.size() == 3);

        //Chose the tickets
        choiceWindow = createTicketsChoiceWindow(options, chooseTicketsHandler);
        choiceWindow.show();
    }

    public void drawCard(ActionHandler.DrawCardHandler drawCardHandler){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();
        //Change the card handler
        this.drawCardHandlerProperty.setValue(i -> {
                    setActionHandlerOnNull();
                    drawCardHandler.onDrawCard(i);
                }
        );
    }


    public void chooseClaimCards(List<SortedBag<Card>> options,
                                 ActionHandler.ChooseCardsHandler chooseCardHandler){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        choiceWindow = createCardChoiceWindow(options, chooseCardHandler);
        choiceWindow.show();
    }


    public void chooseAdditionalCards(List<SortedBag<Card>> options,
                                      ActionHandler.ChooseCardsHandler chooseCardHandler){
        //Check if on thread of javaFX
        assert Platform.isFxApplicationThread();

        choiceWindow = createAdditionalCardChoiceWindow(options, chooseCardHandler);
        choiceWindow.show();
    }

    private Stage createMainWindow(){
        //Create the Scene containing the BorderPane with all the 4 different view
        Pane mapView = MapViewCreator.createMapView(obsGameState, claimRouteHandlerProperty, (o, h) -> {
            if (o.size() > 0)
                h.onChooseCards(o.get(0));
        });
        VBox cardView = DecksViewCreator.createCardsView(obsGameState, drawTicketsHandlerProperty, drawCardHandlerProperty);
        HBox handView = DecksViewCreator.createHandView(obsGameState);
        VBox infoView = InfoViewCreator.createInfoView(playerId, playerNames, obsGameState, infoList);
        Scene scene = new Scene(new BorderPane(mapView, null, cardView, handView, infoView));

        //Create stage of the main window
        Stage mainWindow = new Stage();
        mainWindow.setScene(scene);
        mainWindow.setTitle(new StringBuilder()
                            .append("tCHu - ")
                            .append(playerNames.get(playerId))
                            .toString());
        mainWindow.show();
        return mainWindow;
    }

    private Stage createAdditionalCardChoiceWindow(List<SortedBag<Card>> options,
                                                   ActionHandler.ChooseCardsHandler chooseCardsHandler){
        return createChoiceWindow(  StringsFr.CHOOSE_ADDITIONAL_CARDS,
                                    options,
                                    SortedBag.of(),
                                    chooseCardsHandler,
                                    null);
    }

    private Stage createCardChoiceWindow(List<SortedBag<Card>> options,
                                         ActionHandler.ChooseCardsHandler chooseCardsHandler){
        return createChoiceWindow(  StringsFr.CHOOSE_CARDS,
                options,
                SortedBag.of(),
                chooseCardsHandler,
                null);
    }

    private Stage createTicketsChoiceWindow(SortedBag<Ticket> options,
                                            ActionHandler.ChooseTicketsHandler chooseTicketsHandler){
        return createChoiceWindow(  StringsFr.CHOOSE_CARDS,
                List.of(),
                options,
                null,
                chooseTicketsHandler);
    }

    private Stage createChoiceWindow(String choiceType,
                                     List<SortedBag<Card>> cards,
                                     SortedBag<Ticket> tickets,
                                     ActionHandler.ChooseCardsHandler chooseCardsHandler,
                                     ActionHandler.ChooseTicketsHandler chooseTicketsHandler){
        //Both list can't be either both empty or both non empty !
        Preconditions.checkArgument((chooseCardsHandler != null && chooseTicketsHandler == null && !cards.isEmpty()) ||
                                    (chooseCardsHandler == null && chooseTicketsHandler != null && !tickets.isEmpty()));

        //Set title of the choice window
        String title = new StringBuilder()
                .append((chooseCardsHandler != null)
                        ? StringsFr.CARDS_CHOICE
                        : StringsFr.TICKETS_CHOICE)
                .toString();

        //Create VBox
        VBox vBox = new VBox();
        Button button = new Button();

        //Create the text view
        Text text = new Text(choiceType);
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(text);
        vBox.getChildren().add(textFlow);

        //Create the options view and activation condition for button
        if (chooseCardsHandler != null){
            ListView<SortedBag<Card>> optionsView = new ListView<>(FXCollections.observableArrayList(cards));
            optionsView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
            vBox.getChildren().add(optionsView);
            if (choiceType == StringsFr.CHOOSE_CARDS)
                button.disableProperty().bind(
                        Bindings.size(optionsView.getSelectionModel().getSelectedItems()).isEqualTo(IN_GAME_DISCARABLE_CARD_SIZE)
                );
            button.setOnAction(
                    (event) -> {
                        SortedBag.Builder<Card> selection = new SortedBag.Builder<>();
                        optionsView.getSelectionModel().getSelectedItems().forEach( i -> selection.add(i));
                        chooseCardsHandler.onChooseCards(selection.build());
                        choiceWindow.hide();
                    }
            );
        }else{
            ListView<Ticket> optionsView = new ListView<>(FXCollections.observableArrayList(tickets.toList()));
            vBox.getChildren().add(optionsView);
            button.disableProperty().bind(
                    Bindings.size(optionsView.getSelectionModel().getSelectedItems()).isEqualTo(INITIAL_DISCARDABLE_TICKETS)
            );
            button.setOnAction(
                    (event) -> {
                        SortedBag.Builder<Ticket> selection = new SortedBag.Builder<>();
                        optionsView.getSelectionModel().getSelectedItems().forEach(i -> selection.add(i));
                        chooseTicketsHandler.onChooseTickets(selection.build());
                        choiceWindow.hide();
                    }
            );
        }
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
/*
    private Stage createCardChoiceWindow(String title, String textString, List<SortedBag<Card>> options,
                                          ObjectProperty<ActionHandler.ChooseCardsHandler> handler, boolean isAdditionalCards){
        //Create the text view
        Text text = new Text(textString);
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(text);
        //Create the options view
        ListView<SortedBag<Card>> optionsView =new ListView<>(FXCollections.observableArrayList(options));
        optionsView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        optionsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //Create the button
        Button button = new Button();
        button.disableProperty().bind(Bindings.size(optionsView.getSelectionModel().getSelectedItems()).greaterThanOrEqualTo(
                (isAdditionalCards)
                ? MIN_ADDITIONAL_CARDS_SIZE
                : MIN_CLAIM_CARD_SIZE
        ));
        button.setOnAction(
            (event) -> {
                choiceWindow.hide();

                List<Integer> selection = optionsView.getSelectionModel().getSelectedIndices().sorted();
                SortedBag.Builder<Card> selectionBag = new SortedBag.Builder<>();
                for (Integer i : selection)
                    selectionBag.add(options.get(i));
                handler.get().onChooseCards(selectionBag.build());
            }
        );
        //Attach them to each other
        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlow, optionsView, button);

        return createChoiceWindow(vBox, title);
    }

    private Stage createTicketChoiceWindow(String title, SortedBag<Ticket> options,
                                           ActionHandler.ChooseTicketsHandler handler){
        //Check correctness of the arguments
        Preconditions.checkArgument(title.equals(StringsFr.TICKETS_CHOICE));

        //Create the text view
        String textString = String.format(StringsFr.CHOOSE_TICKETS, "%s", options.size() - 2);
        Text text = new Text(textString);
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(text);
        //Create the options view
        ListView<Ticket> optionsView = new ListView<>(FXCollections.observableArrayList(options.toList()));
        //Create the button
        Button button = new Button();
        button.setOnAction( (!activateSelectionButton.get())
                ? Event::consume
                : (event) -> {
            choiceWindow.hide();
            List<Integer> selection = optionsView.getSelectionModel().getSelectedIndices().sorted();
            SortedBag.Builder<Ticket> selectionBag = new SortedBag.Builder<>();
            for (Integer i : selection)
                selectionBag.add(options.get(i));
            handler.get().onChooseTickets(selectionBag.build());
        });
        //Attach them to each other
        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlow, optionsView, button);

        return createChoiceWindow(vBox, title);
    }

    private Stage createChoiceWindow(VBox vBox, String title){
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
*/
    private final class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
        @Override
        public String toString(SortedBag<Card> object) {
            List<String> toString = new ArrayList<>();
            object.forEach(c -> toString.add(Info.cardName(c, object.countOf(c))));
            return String.join(" et ", toString);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
