package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StationPartitionTest {

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

    @Test
    void BuilderFailsOnNegativeCount(){
        assertThrows(IllegalArgumentException.class, () -> new StationPartition.Builder(-1));
    }

    @Test
    void BuilderWorksWithPositiveCount(){
        new StationPartition.Builder(1);
        new StationPartition.Builder(0);
    }

    @Test
    void ConnectedWorksForOutOfBoundsStations(){
        StationPartition.Builder builder = new StationPartition.Builder(8);
        builder.connect(BAD, BRU);//0 to 5
        builder.connect(BRU, COI);//5 to 6
        builder.connect(BEL, DAV);//2 to 7
        builder.connect(DAV, COI);//7 to 6
        builder.connect(BER, BAL);//3 to 1

        StationPartition partition = builder.build();

        assertTrue(partition.connected(FRI, FRI));
        assertFalse(partition.connected(BAD, FRI));
    }

    @Test
    void ConnectedWorksForInBoundsStations(){
        StationPartition.Builder builder = new StationPartition.Builder(8);
        builder.connect(BAD, BRU);//0 to 5
        builder.connect(BEL, DAV);//2 to 7

        StationPartition partition = builder.build();

        assertTrue(partition.connected(BAD, BRU));
        assertTrue(partition.connected(BEL, DAV));
        assertFalse(partition.connected(DAV, BRU));
        assertTrue(partition.connected(COI, COI));
    }

    @Test
    void BuilderWorksOnVideoExample(){
        StationPartition.Builder builder = new StationPartition.Builder(8);
        builder.connect(BAD, BRU);//0 to 5
        builder.connect(BRU, COI);//5 to 6
        builder.connect(BEL, DAV);//2 to 7
        builder.connect(DAV, COI);//7 to 6
        builder.connect(BER, BAL);//3 to 1

        StationPartition partition = builder.build();

        boolean answer = false;
        if(partition.connected(BAD, BRU) && partition.connected(BAD, COI) && partition.connected(BAD, BEL)
                && partition.connected(BAD, DAV) && partition.connected(BAL, BER) && partition.connected(BRI, BRI))
            answer = true;

        assertTrue(answer);
    }

    @Test
    void BuilderWorksOnExtendedVideoExample(){
        StationPartition.Builder builder = new StationPartition.Builder(9);
        builder.connect(BAD, BRU);//0 to 5
        builder.connect(BRU, DEL);//5 to 8
        builder.connect(DEL, COI);//8 to 6
        builder.connect(BEL, DAV);//2 to 7
        builder.connect(DAV, COI);//7 to 6
        builder.connect(BER, BAL);//3 to 1

        StationPartition partition = builder.build();

        boolean answer = false;
        if(partition.connected(BAD, BRU) && partition.connected(BAD, DEL) && partition.connected(BAD, COI) && partition.connected(BAD, BEL)
                && partition.connected(BAD, DAV) && partition.connected(BAL, BER) && partition.connected(BRI, BRI))
            answer = true;

        assertTrue(answer);
    }

    @Test
    void BuilderWorksOnNotConnectedExample(){
        StationPartition.Builder builder = new StationPartition.Builder(9);
        StationPartition partition = builder.build();

        boolean answer = false;
        if(!partition.connected(BAD, BRU) && !partition.connected(BAD, DEL) && !partition.connected(BAD, COI) && !partition.connected(BAD, BEL)
                && !partition.connected(BAD, DAV) && !partition.connected(BAL, BER))
            answer = true;

        assertTrue(answer);
    }
}
