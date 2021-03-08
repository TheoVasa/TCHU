package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Combine the claimed roads of a player in a (longest possible) trail
 * @author Selien Wicki (314357)
 * @author Theo Vasarino (313191)
 */
public final class Trail {

    /**
     * Attributes
     */
    private final List<Route> routes;
    private final List<Station> stations;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final String stationName;


    /**
     * Private Constructor
     */
    private Trail(List<Route> routes, List<Station> stations) {
        this.routes = routes; //#############################################
        this.stations = stations;
        this.station1 = (stations.size() > 0) ? stations.get(0) : null;
        this.station2 = (stations.size() > 0) ? stations.get(stations.size()-1) : null;
        this.length = generateLength();
        this.stationName = generateName();
    }

    /**
     * Gives the longest possible road of a trail
     * @param routes the list of all possible roads
     * @return the longest trail of multiple roads
     */
    public static Trail longest(List<Route> routes){
        //longestTrail, will be set and returned at the end
        Trail longestTrail = new Trail(new ArrayList<>(), new ArrayList<>());

        //Create a list of all Trails of singles roads and generate list with trails of single roads
        List<Trail> trails = new ArrayList<>();
        for (int i = 0; i < routes.size(); ++i){
            //Two possible directions of a trail
            trails.add(new Trail(Arrays.asList(routes.get(i)), Arrays.asList(routes.get(i).station1(), routes.get(i).station2())));
            trails.add(new Trail(Arrays.asList(routes.get(i)), Arrays.asList(routes.get(i).station2(), routes.get(i).station1())));
        }

        //Find longest trail
        while (!trails.isEmpty()){
            //New list of trails that will extend the already existing trails
            List<Trail> extendedTrails = new ArrayList<>();

            for (Trail trail: trails) {
                //Set longest trail
                if (longestTrail.length < trail.length)
                    longestTrail = new Trail(trail.routes, trail.stations);

                //Find trail extensions
                for (Route route : routes) {
                    if ((route.station2().equals(trail.station2) || route.station1().equals(trail.station2)) && !trail.contains(route)) {
                        //Set the new extended roads of the trail
                        List<Route> extendedTrailRoads = new ArrayList<>(trail.routes);
                        List<Station> extendedStations = new ArrayList<>(trail.stations);
                        extendedTrailRoads.add(route);
                        extendedStations.add((route.station2().equals(trail.station2)) ? route.station1() : route.station2());
                        extendedTrails.add(new Trail(extendedTrailRoads, extendedStations));
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
     * Getter for the length of the trail
     * @return
     */
    public int length(){
        return length;
    }

    /**
     * Getter for the first station of the trail
     * @return the first station
     */
    public Station station1(){
        return station1;
    }

    /**
     * Getter for the second station of the trail
     * @return the second station
     */
    public Station station2(){
        return station2;
    }

    /**
     * Check if a road is contained in the trail (Usefull for double road type to avoid duplications exp. : Lausanne - Fribourg)
     * @param road the road we want to check the appearance
     * @return if the road is contained in the trail
     */
    private boolean contains(Route road){
        if (routes.size() > 0){
            for (Route route: routes){
                if (route.stations().contains(road.station1()) && route.stations().contains(road.station2()))
                    return true;
            }
        }
        return false;
    }

    /**
     * Calculate the length of the trail
     * @return the length of the trail
     */
    private int generateLength(){
        int finalLength = 0;
        if (routes.size() > 0) {
            for (Route route : routes)
                finalLength += route.length();
        }
        return finalLength;
    }

    /**
     * Generate the name of the trail (a name with all the roads that the trail contains)
     * @return trail name
     */
    private String generateName() {
        //Name list of all the station of the trail
        List<String> stationNames = new ArrayList<>();
        for (Station s : stations)
            stationNames.add(s.name());

        //Set trail name
        return (stationNames.isEmpty()) ? "Empty Trail" :  String.join(" - " ,stationNames) + " (" + length + ")";
    }

    @Override
    public String toString(){
        return stationName;
    }
}
