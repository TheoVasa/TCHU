package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InfoTest {

    Info info = new Info("a");
    Route route = new Route("AT1_STG_1",AT1, STG, 4,Route.Level.UNDERGROUND, null);

    // Stations - cities
    private static final Station BAD = new Station(0, "Baden");
    private static final Station BAL = new Station(1, "Bâle");
    private static final Station BEL = new Station(2, "Bellinzone");
    private static final Station BER = new Station(3, "Berne");
    private static final Station BRI = new Station(4, "Brigue");
    private static final Station BRU = new Station(5, "Brusio");
    private static final Station COI = new Station(6, "Coire");
    private static final Station DAV = new Station(7, "Davos");
    private static final Station DEL = new Station(8, "Delémont");
    private static final Station FRI = new Station(9, "Fribourg");
    private static final Station GEN = new Station(10, "Genève");
    private static final Station INT = new Station(11, "Interlaken");
    private static final Station KRE = new Station(12, "Kreuzlingen");
    private static final Station LAU = new Station(13, "Lausanne");
    private static final Station LCF = new Station(14, "La Chaux-de-Fonds");
    private static final Station LOC = new Station(15, "Locarno");
    private static final Station LUC = new Station(16, "Lucerne");
    private static final Station LUG = new Station(17, "Lugano");
    private static final Station MAR = new Station(18, "Martigny");
    private static final Station NEU = new Station(19, "Neuchâtel");
    private static final Station OLT = new Station(20, "Olten");
    private static final Station PFA = new Station(21, "Pfäffikon");
    private static final Station SAR = new Station(22, "Sargans");
    private static final Station SCE = new Station(23, "Schaffhouse");
    private static final Station SCZ = new Station(24, "Schwyz");
    private static final Station SIO = new Station(25, "Sion");
    private static final Station SOL = new Station(26, "Soleure");
    private static final Station STG = new Station(27, "Saint-Gall");
    private static final Station VAD = new Station(28, "Vaduz");
    private static final Station WAS = new Station(29, "Wassen");
    private static final Station WIN = new Station(30, "Winterthour");
    private static final Station YVE = new Station(31, "Yverdon");
    private static final Station ZOU = new Station(32, "Zoug");
    private static final Station ZUR = new Station(33, "Zürich");

    // Stations - countries
    private static final Station DE1 = new Station(34, "Allemagne");
    private static final Station DE2 = new Station(35, "Allemagne");
    private static final Station DE3 = new Station(36, "Allemagne");
    private static final Station DE4 = new Station(37, "Allemagne");
    private static final Station DE5 = new Station(38, "Allemagne");
    private static final Station AT1 = new Station(39, "Autriche");
    private static final Station AT2 = new Station(40, "Autriche");
    private static final Station AT3 = new Station(41, "Autriche");
    private static final Station IT1 = new Station(42, "Italie");
    private static final Station IT2 = new Station(43, "Italie");
    private static final Station IT3 = new Station(44, "Italie");
    private static final Station IT4 = new Station(45, "Italie");
    private static final Station IT5 = new Station(46, "Italie");
    private static final Station FR1 = new Station(47, "France");
    private static final Station FR2 = new Station(48, "France");
    private static final Station FR3 = new Station(49, "France");
    private static final Station FR4 = new Station(50, "France");

    @Test
    void showsCardNamesCorrectly(){
        assertEquals(StringsFr.BLACK_CARD, Info.cardName(Card.BLACK, 1));
        assertEquals(StringsFr.BLACK_CARD + "s", Info.cardName(Card.BLACK, 2));
    }

    @Test
    void drawWorks(){
        List<String> names = new ArrayList<String>();
        names.add("a");
        names.add("b");
        names.add("c");

        assertEquals("\na et b et c sont ex æqo avec 5 points !\n", Info.draw(names, 5));
    }

    @Test
    void WillPlayFirstWorks(){
        assertEquals("a jouera en premier.\n\n", info.willPlayFirst());
    }

    @Test
    void keptTicketsWorks(){
        assertEquals("a a gardé 1 billet.\n", info.keptTickets(1));
        assertEquals("a a gardé 5 billets.\n", info.keptTickets(5));
    }

    @Test
    void canPlayWorks(){
        assertEquals("\nC'est à a de jouer.\n", info.canPlay());
    }

    @Test
    void drewTicketsWorks(){
        assertEquals("a a tiré 1 billet...\n", info.drewTickets(1));
        assertEquals("a a tiré 5 billets...\n", info.drewTickets(5));
    }

    @Test
    void drewBlindCardWorks(){
        assertEquals("a a tiré une carte de la pioche.\n", info.drewBlindCard());
    }

    @Test
    void drewVisibleCardWorks(){
        assertEquals("a a tiré une carte noire visible.\n", info.drewVisibleCard(Card.BLACK));
    }

    @Test
    void claimedRouteWorks(){

        SortedBag.Builder<Card> biggerBag = new SortedBag.Builder<Card>();
        biggerBag.add(1, Card.LOCOMOTIVE);
        biggerBag.add(2, Card.BLACK);
        biggerBag.add(3, Card.GREEN);

        assertEquals("a a pris possession de la route Autriche" + StringsFr.EN_DASH_SEPARATOR +
                        "Saint-Gall au moyen de 2 bleues et 1 locomotive.\n",
                info.claimedRoute(route, SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.BLUE)));

        assertEquals("a a pris possession de la route Autriche" + StringsFr.EN_DASH_SEPARATOR +
                        "Saint-Gall au moyen de 1 locomotive.\n",
                info.claimedRoute(route, SortedBag.of(1, Card.LOCOMOTIVE)));

        assertEquals("a a pris possession de la route Autriche" + StringsFr.EN_DASH_SEPARATOR +
                        "Saint-Gall au moyen de 2 noires, 3 vertes et 1 locomotive.\n",
                info.claimedRoute(route, biggerBag.build()));
    }

    @Test
    void attemptsTunnelClaimWorks(){

        SortedBag.Builder<Card> biggerBag = new SortedBag.Builder<Card>();
        biggerBag.add(1, Card.LOCOMOTIVE);
        biggerBag.add(2, Card.BLACK);
        biggerBag.add(3, Card.GREEN);

        assertEquals("a tente de s'emparer du tunnel Autriche" + StringsFr.EN_DASH_SEPARATOR +
                        "Saint-Gall au moyen de 2 bleues et 1 locomotive !\n",
                info.attemptsTunnelClaim(route, SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.BLUE)));

        assertEquals("a tente de s'emparer du tunnel Autriche" + StringsFr.EN_DASH_SEPARATOR +
                        "Saint-Gall au moyen de 1 locomotive !\n",
                info.attemptsTunnelClaim(route, SortedBag.of(1, Card.LOCOMOTIVE)));

        assertEquals("a tente de s'emparer du tunnel Autriche" + StringsFr.EN_DASH_SEPARATOR +
                        "Saint-Gall au moyen de 2 noires, 3 vertes et 1 locomotive !\n",
                info.attemptsTunnelClaim(route, biggerBag.build()));
    }

    @Test
    void drewAdditionalCardsWorks(){

        SortedBag.Builder<Card> biggerBag = new SortedBag.Builder<Card>();
        biggerBag.add(1, Card.LOCOMOTIVE);
        biggerBag.add(1, Card.BLACK);
        biggerBag.add(1, Card.GREEN);

        assertEquals("Les cartes supplémentaires sont 2 bleues et 1 locomotive. " +
                        "Elles impliquent un coût additionnel de 2 cartes.\n",
                info.drewAdditionalCards(SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.BLUE), 2));

        assertEquals("Les cartes supplémentaires sont 3 locomotives. " +
                        "Elles impliquent un coût additionnel de 1 carte.\n",
                info.drewAdditionalCards(SortedBag.of(3, Card.LOCOMOTIVE), 1));

        assertEquals("Les cartes supplémentaires sont 1 noire, 1 verte et 1 locomotive. " +
                        "Elles n'impliquent aucun coût additionnel.\n",
                info.drewAdditionalCards(biggerBag.build(), 0));
    }

    @Test
    void didNotClaimRuteWorks(){
        assertEquals("a n'a pas pu (ou voulu) s'emparer de la route Autriche" + StringsFr.EN_DASH_SEPARATOR +
                        "Saint-Gall.\n", info.didNotClaimRoute(route));
    }

    @Test
    void lastTurnBeginsWorks(){
        assertEquals("\na n'a plus que 2 wagons, le dernier tour commence !\n",
                info.lastTurnBegins(2));
        assertEquals("\na n'a plus que 1 wagon, le dernier tour commence !\n",
                info.lastTurnBegins(1));
    }

    @Test
    void getsLongestTrailBonusWorks(){

        Route route_a = new Route("NEU_YVE_1", NEU, YVE, 2, Route.Level.OVERGROUND, Color.BLACK);
        Route route_b = new Route("NEU_SOL_1", NEU, SOL, 4, Route.Level.OVERGROUND, Color.GREEN);
        Route route_c = new Route("BER_NEU_1", BER, NEU, 2, Route.Level.OVERGROUND, Color.RED);
        Route route_d = new Route("BER_SOL_1", BER, SOL, 2, Route.Level.OVERGROUND, Color.BLACK);
        Route route_e = new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null);
        Route route_f = new Route("BER_FRI_1", BER, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE);

        List <Route> roads = new ArrayList<Route>();
        roads.add(route_a);
        roads.add(route_b);
        roads.add(route_c);
        roads.add(route_d);
        roads.add(route_e);
        roads.add(route_f);

        assertEquals("\na reçoit un bonus de 10 points pour le plus long trajet (Lucerne" +
                        StringsFr.EN_DASH_SEPARATOR + "Fribourg).\n",
                info.getsLongestTrailBonus(Trail.longest(roads)));
    }

    @Test
    void wonWorks(){
        assertEquals("\na remporte la victoire avec 40 points, contre 20 points !\n",
                info.won(40, 20));

        assertEquals("\na remporte la victoire avec 1 point, contre 1 point !\n",
                info.won(1, 1));
    }
}
