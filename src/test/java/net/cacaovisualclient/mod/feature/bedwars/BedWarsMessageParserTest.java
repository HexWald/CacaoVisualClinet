package net.cacaovisualclient.mod.feature.bedwars;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BedWarsMessageParserTest {
    private final BedWarsMessageParser parser = new BedWarsMessageParser();

    @Test
    void readsMatchResults() {
        assertEquals("Victory", parser.parse("VICTORY!", "Player").orElseThrow().title());
        assertEquals("Victory", parser.parse("You won the game!", "Player").orElseThrow().title());
        assertEquals("Defeat", parser.parse("GAME OVER!", "Player").orElseThrow().title());
        assertEquals("Defeat", parser.parse("You lost the round.", "Player").orElseThrow().title());
    }

    @Test
    void ignoresUnrelatedServerMessages() {
        assertTrue(parser.parse("You won't receive chat messages here.", "Player").isEmpty());
        assertTrue(parser.parse("You lost connection to the lobby.", "Player").isEmpty());
        assertTrue(parser.parse("The game over screen has been updated.", "Player").isEmpty());
    }
}
