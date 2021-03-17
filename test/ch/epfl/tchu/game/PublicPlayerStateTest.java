package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class PublicPlayerStateTest {

    //attributs for testing
    private Route r1 = ChMap.routes().get(0);
    private Route r2 = ChMap.routes().get(10);
    private Route r3 = ChMap.routes().get(20);
    private List<Route> listOfRoute = new ArrayList<>();
    {
        listOfRoute.add(r1);
        listOfRoute.add(r2);
        listOfRoute.add(r3);
    }
    private int ticketCountTest = 15;
    private int cardCountTest = 10;



    @Test
    void constructorOfCopyIsWorking(){
        PublicPlayerState playerTest = new PublicPlayerState(ticketCountTest, cardCountTest, listOfRoute);
        List<Route> copyOfRoute = new ArrayList<>(listOfRoute);
        listOfRoute.clear();
         assertEquals(copyOfRoute, playerTest.routes());
    }

    @Test
    void constructorThrowsIllegalArgumentException(){

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(-1, cardCountTest, listOfRoute);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(ticketCountTest, -1, listOfRoute);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(-1, -1, listOfRoute);
        });
    }

    @Test
    void ticketCountIsWorking(){
        PublicPlayerState playerTest1 = new PublicPlayerState(ticketCountTest, cardCountTest, listOfRoute);
        PublicPlayerState playerTest2 = new PublicPlayerState(0, cardCountTest, listOfRoute);

        assertEquals(ticketCountTest, playerTest1.ticketCount());
        assertEquals(0, playerTest2.ticketCount());
    }

    @Test
    void cardCountIsWorking(){
        PublicPlayerState playerTest1 = new PublicPlayerState(ticketCountTest, cardCountTest, listOfRoute);
        PublicPlayerState playerTest2 = new PublicPlayerState(ticketCountTest, 0, listOfRoute);

        assertEquals(cardCountTest, playerTest1.cardCount());
        assertEquals(0, playerTest2.cardCount());
    }

    @Test
    void routeIsWorking(){
        PublicPlayerState playerTest = new PublicPlayerState(ticketCountTest, cardCountTest, listOfRoute);
        assertEquals(listOfRoute, playerTest.routes());
    }

    @Test
    void carCountIsWorking(){
        PublicPlayerState playerTest = new PublicPlayerState(ticketCountTest, cardCountTest, listOfRoute);
        int totalRouteLength = 0;
        for(Route r : listOfRoute) totalRouteLength += r.length();
        int carCountTest = Constants.INITIAL_CAR_COUNT-totalRouteLength;
        assertEquals(carCountTest, playerTest.carCount());
    }

    @Test
    void claimPointsIsWorking(){
        PublicPlayerState playerTest = new PublicPlayerState(ticketCountTest, cardCountTest, listOfRoute);
        int constructionPoints = r1.claimPoints() + r2.claimPoints() + r3
                .claimPoints();
        assertEquals(constructionPoints, playerTest.claimPoints());
    }
}
