package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrailTest {

    Route[] routes = {
            ChMap.routes().get(66),
            ChMap.routes().get(65),
            ChMap.routes().get(19),
            ChMap.routes().get(16),
            ChMap.routes().get(18),
            ChMap.routes().get(14)
    };

    @Test
    void lengthIsCorrectOnEmptyTrail() {
        Trail trail = Trail.longest(List.of());
        int expectedLength = 0;
        assertEquals(trail.length(), expectedLength);
    }

    @Test
    void lengthIsCorrectOnNonEmptyTrail(){
        Trail trail = Trail.longest(Arrays.asList(routes));
        int expectedLength = 13;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void station1IsCorrectOnEmptyTrail() {
        Trail trail = Trail.longest(List.of());
        assertNull(trail.station1());
    }

    @Test
    void station1IsCorrectOnNonEmptyTrail() {
        Trail trail = Trail.longest(Arrays.asList(routes));
        Station expectedStation = ChMap.stations().get(16);
        assertEquals(expectedStation, trail.station1());
    }

    @Test
    void station2IsCorrectOnEmptyTrail() {
        Trail trail = Trail.longest(List.of());
        assertNull(trail.station2());
    }

    @Test
    void station2IsCorrectOnNonEmptyTrail(){
        Trail trail = Trail.longest(Arrays.asList(routes));
        Station expectedStation = ChMap.stations().get(9);
        assertEquals(expectedStation, trail.station2());
    }

    @Test
    void testToStringIsCorrectOnEmptyTrail() {
        assertEquals("Empty Trail", Trail.longest(List.of()).toString());
    }
}