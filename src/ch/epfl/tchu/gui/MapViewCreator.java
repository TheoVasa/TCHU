package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class MapViewCreator {
    public static Pane createMapView(ObservableGameState gameState, ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHandlerProperty, CardChooser cardChooser){
        //the map
        Pane map = new Pane();
        map.getStylesheets().addAll("map.css", "colors.css");
        //the image of the map
        ImageView imageMap = new ImageView();
        //all routes
        List<Group> routesGroups = new ArrayList<>();
        ChMap.routes().forEach((Route r)->
            routesGroups.add(generateRoute(r))
        );
        map.getChildren().add(imageMap);
        map.getChildren().addAll(routesGroups);

        return map;
    }
    private static Group generateCarGroup(){
        Group car = new Group();
        car.getStyleClass().add("car");

        Rectangle rect = new Rectangle(36, 12);
        rect.getStyleClass().add("filled");
        car.getChildren().add(rect);

        Circle circ1 = new Circle(6);
        circ1.setCenterX(12);
        circ1.setCenterY(6);
        Circle circ2 = new Circle(6);
        circ2.setCenterX(24);
        circ2.setCenterY(6);

        car.getChildren().setAll(circ1, circ2);

        return car;
    }

    private static Group generateRoute(Route r) {
        Group route = new Group();
        route.setId(r.id());

        if(r.color()==null) route.getStyleClass().addAll(r.level().name(), "NEUTRAL");
        else route.getStyleClass().addAll(r.level().name(), r.color().name());



        route.getStyleClass().addAll(r.level().name(), r.color().name());

        for(int i=0; i<r.length(); ++i)
            route.getChildren().add(generateCaseRoute(r.id() + "_" + i));

       return route;
    }

    private static Group generateCaseRoute(String id){
        Group caseOfRoute = new Group();
        caseOfRoute.setId(id);
        Rectangle rect = new Rectangle();
        rect.getStyleClass().addAll("track", "filled");
        caseOfRoute.getChildren().addAll(rect, generateCarGroup());

        return caseOfRoute;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                ActionHandler.ChooseCardsHandler handler);
    }

}
