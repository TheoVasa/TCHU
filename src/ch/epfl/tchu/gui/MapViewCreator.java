package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.List;

public class MapViewCreator {
    private final static int RECTANGLES_WIGHT = 36;
    private final static int RECTANGLES_HEIGHT = 12;
    private final static int CIRCLES_RADIUS = 3;
    private final static int CIRCLE1_XPOS = 12;
    private final static int CIRCLE1_YPOS = 6;
    private final static int CIRCLE2_XPOS = 24;
    private final static int CIRCLE2_YPOS = 6;

    public static Pane createMapView(ObservableGameState obsGameState, ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHandler, CardChooser cardChooser){
        //the map
        Pane map = new Pane();
        map.getStylesheets().addAll("map.css", "colors.css");
        //the image of the map
        ImageView imageMap = new ImageView();
        map.getChildren().add(imageMap);
        //all routes
        for (Route r : ChMap.routes()) {
            Group route = generateRoute(r, obsGameState);

            //manage when the player click on a route.
            route.setOnMouseClicked((event)->{
                List<SortedBag<Card>> possibleClaimCards = obsGameState.possibleClaimCards(r);
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
            //Disable or not the route interactions with the user, in function of if the route is claimable or not.
            route.disableProperty().bind(claimRouteHandler.isNull().or(obsGameState.claimable(r).not()));
        }
        return map;
    }
    //generate a route node
    private static Group  generateRoute(Route r, ObservableGameState obsGameState) {
        Group route = new Group();
        route.setId(r.id());
        //route color
        String routeColor = (r.color()==null) ? "NEUTRAL" : r.color().toString();
        route.getStyleClass().addAll("route",r.level().toString(), routeColor);
        //manage route player
        obsGameState.routeProperty(r).addListener((o, oV, nV)->{
            String owner = (nV==null) ? "" : nV.toString();
            route.getStyleClass().add(owner);
        });

        for(int i=1; i<=r.length(); ++i)
            route.getChildren().add(generateCaseRoute(new StringBuilder()
                                                        .append(r.id())
                                                        .append("_")
                                                        .append(i)
                                                        .toString()));
       return route;
    }
    //generate the case of a route node
    private static Group generateCaseRoute(String id){
        Group caseOfRoute = new Group();
        caseOfRoute.setId(id);
        Rectangle rect = new Rectangle(RECTANGLES_WIGHT, RECTANGLES_HEIGHT);
        rect.getStyleClass().addAll("track", "filled");
        caseOfRoute.getChildren().add(rect);
        caseOfRoute.getChildren().add(generateCarGroup());

        return caseOfRoute;
    }
    //generate the car on the case of a route node
    private static Group generateCarGroup(){
        Group car = new Group();
        car.getStyleClass().add("car");

        Rectangle rect = new Rectangle(RECTANGLES_WIGHT, RECTANGLES_HEIGHT);
        rect.getStyleClass().add("filled");

        Circle circ1 = new Circle(CIRCLE1_XPOS, CIRCLE1_YPOS,CIRCLES_RADIUS);
        Circle circ2 = new Circle(CIRCLE2_XPOS, CIRCLE2_YPOS, CIRCLES_RADIUS);

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
