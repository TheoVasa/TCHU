package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.*;


class PlayerIdTest {

    @Test
    void nextIsWorking(){
        assertEquals(PLAYER_2, PLAYER_1.next());
        assertEquals(PLAYER_1, PLAYER_2.next());
    }
}
