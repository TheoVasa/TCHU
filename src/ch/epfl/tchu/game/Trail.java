package ch.epfl.tchu.game;

import java.util.*;

/**
 * This class represents a Trail (a succession of routes that are connected).
 * Only useful to find the longest trail of a player.
 * It is public, final and immutable.
 *
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class Trail {
    //The routes of the trail
    private final List<Route> routes;
    //The first station of the trail
    private final Station firstStation;
    //The last station of the trail
    private final Station lastStation;
    //The length of the trail given by the sum of the length of all the routes
    private final int length;
    //The text of the trail
    private final String text;


    //Create a Trail with all the station it goes trough.
    private Trail(List<Route> routes) {
        //Init vars
        this.routes = routes;
        firstStation = generateFirstStation();
        lastStation = generateLastStation();
        length = generateLength();
        text = generateName();
    }

    /**
     * Gives the longest possible trail.
     *
     * @param routes the list of all possible routes
     * @return the longest trail of multiple routes (Trail)
     */
    public static Trail longest(List<Route> routes) {
        //longestTrail, will be set and returned at the end
        Trail longestTrail = new Trail(new ArrayList<>());

        //Create a list of all Trails of singles roads and generate list with trails of single roads
        List<Trail> trails = new ArrayList<>();
        for (Route r : routes) {
            //Two possible directions of a trail
            trails.add(new Trail(Collections.singletonList(r)));
            trails.add(new Trail(Collections.singletonList(r)));
        }

        //Find longest trail
        while (!trails.isEmpty()) {
            //New list of trails that will extend the already existing trails
            List<Trail> extendedTrails = new ArrayList<>();

            for (Trail trail : trails) {
                //Set longest trail
                if (longestTrail.length < trail.length)
                    longestTrail = new Trail(trail.routes);

                //Find trail extensions
                for (Route route : routes) {
                    if ((route.station2().equals(trail.lastStation) || route.station1().equals(trail.lastStation)) && !trail.contains(route)) {
                        //Set the new extended roads of the trail
                        List<Route> extendedTrailRoute = new ArrayList<>(trail.routes);
                        extendedTrailRoute.add(route);
                        extendedTrails.add(new Trail(extendedTrailRoute));
                    }
                }
            }
            //Set the new extended trails
            trails.clear();
            trails.addAll(extendedTrails);
        }
        return longestTrail;
    }

    /**
     * Getter for the length of the trail.
     * The length of the trail is just the sum of the length of its routes.
     *
     * @return the length of the trail (int)
     */
    public int length() {
        return length;
    }

    /**
     * Getter for the last station of the trail.
     *
     * @return the first station of the trail (Station)
     */
    public Station station1() {
        return firstStation;
    }

    /**
     * Getter for the last station of the trail.
     *
     * @return the second station of the trail (Station)
     */
    public Station station2() {
        return lastStation;
    }

    //Check if a road is contained in the trail
    //Needed for double route type to avoid duplications, exp. : Lausanne - Fribourg.
    //If we used the methode contains(Obj) of the class Collection, then the trail could
    //take twice the same route.
    private boolean contains(Route route) {
        for (Route r : routes) {
            if (r.stations().contains(route.station1()) && r.stations().contains(route.station2()))
                return true;
        }
        return false;
    }

    //Calculate the length of the trail.
    private int generateLength() {
        int finalLength = 0;
        if (routes.size() > 0) {
            for (Route route : routes)
                finalLength += route.length();
        }
        return finalLength;
    }

    //Generate the first station of the trail
    private Station generateFirstStation(){
        Station fStation = null;
        if (routes.size() > 0){
            if (routes.size() == 1)
                fStation = routes.get(0).station1();
            else
                fStation = (routes.get(1).stations().contains(routes.get(0).station1())
                        ? routes.get(0).station2()
                        : routes.get(0).station1());
        }
        return fStation;
    }

    //Generate the last station of the trail
    private Station generateLastStation(){
        Station lStation = null;
        if (routes.size() > 0){
            if (routes.size() == 1)
                lStation = routes.get(0).station2();
            else
                lStation =  (routes.get(routes.size()-2).stations().contains(routes.get(routes.size()-1).station1()))
                            ? routes.get(routes.size()-1).station2()
                            : routes.get(routes.size()-1).station1();
        }
        return lStation;
    }

    //Generate the name of the trail (a name with all the route that the trail contains).
    private String generateName() {
        //Name list of all the station of the trail
        List<String> stationNames = (firstStation == null || lastStation == null)
                                    ? List.of()
                                    : List.of(firstStation.name(), lastStation.name());

        //Generate the name
        String name = new StringBuilder()
                .append(String.join(" - ", stationNames))
                .append(" (")
                .append(length)
                .append(")")
                .toString();

        return (stationNames.isEmpty())
                ? "Empty Trail"
                : name;
    }

    @Override
    public String toString() {
        return text;
    }
}
