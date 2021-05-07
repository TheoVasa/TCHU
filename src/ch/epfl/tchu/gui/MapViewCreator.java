package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class MapViewCreator {
    public static Pane createMapView(ObservableGameState gameState, ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHandler, CardChooser cardChooser){
        //the map
        Pane map = new Pane();
        map.getStylesheets().addAll("map.css", "colors.css");
        //the image of the map
        ImageView imageMap = new ImageView();
        map.getChildren().add(imageMap);
        //all routes
        for (Route r : ChMap.routes()) {
            Group route = generateRoute(r, gameState);

            //manage when the player click on a route.
            route.setOnMouseClicked((event)->{
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(r);
                //if the player have only one choice to claim the route.
                if(possibleClaimCards.size()==1){
                    claimRouteHandler.get().onClaimRoute(r, possibleClaimCards.get(0));
                }else{
                    //if not
                    ActionHandler.ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteHandler.get().onClaimRoute(r, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });
            map.getChildren().add(route);
            System.out.println("Test");
            route.disableProperty().bind(claimRouteHandler.isNull().or(gameState.claimable(r).not()));

            //boolean property in function of if the player can claim route.
        }
        return map;
    }

    private static Group generateRoute(Route r, ObservableGameState observableGameState) {
        Group route = new Group();
        route.setId(r.id());
        //route color
        System.out.println("test2");
        String routeColor = (r.color()==null) ? "NEUTRAL" : r.color().toString();
        route.getStyleClass().addAll("route",r.level().toString(), routeColor);
        //manage route player
        observableGameState.RouteProperty(r).addListener((o, oV, nV)->{
            String owner = (nV==null) ? "" : nV.toString();
            route.getStyleClass().add(owner);
        });

        for(int i=1; i<=r.length(); ++i)
            route.getChildren().add(generateCaseRoute(r.id() + "_" + i));
       return route;
    }

    private static Group generateCaseRoute(String id){
        Group caseOfRoute = new Group();
        caseOfRoute.setId(id);
        Rectangle rect = new Rectangle(36, 12);
        rect.getStyleClass().addAll("track", "filled");
        caseOfRoute.getChildren().add(rect);
        caseOfRoute.getChildren().add(generateCarGroup());

        return caseOfRoute;
    }

    private static Group generateCarGroup(){
        Group car = new Group();
        car.getStyleClass().add("car");

        Rectangle rect = new Rectangle(36, 12);
        rect.getStyleClass().add("filled");

        Circle circ1 = new Circle(12, 6,3);
        Circle circ2 = new Circle(24, 6, 3);

        //draw the rectangle
        car.getChildren().add(rect);
        //draw the circles
        car.getChildren().addAll(circ1, circ2);

        return car;
    }


    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                ActionHandler.ChooseCardsHandler handler);
    }

}
