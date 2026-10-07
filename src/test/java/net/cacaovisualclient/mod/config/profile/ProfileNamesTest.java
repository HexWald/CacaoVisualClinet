package net.cacaovisualclient.mod.config.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileNamesTest {

    @Test
    void acceptsNormalProfileNames() {
        for (String name : new String[]{"Default", "BedWars 2", "Мой профиль", "PvP-1.2", "CONquest"}) {
            assertTrue(ProfileNames.isValid(name), name);
        }
    }

    @Test
    void rejectsReservedWindowsNames() {
        for (String name : new String[]{"CON", "nul", "Aux", "PRN.backup", "COM1", "LPT9", "COM¹", "CONOUT$"}) {
            assertFalse(ProfileNames.isValid(name), name);
        }
    }

    @Test
    void rejectsPathsAndNamesThatWindowsChanges() {
        for (String name : new String[]{"", " ", ".", "..", "../config", "..\\config", "C:config", "PvP.", "PvP ", " PvP", "PvP\n"}) {
            assertFalse(ProfileNames.isValid(name), name);
        }
        assertFalse(ProfileNames.isValid(null));
    }

    @Test
    void rejectsEachInvalidFileCharacter() {
        for (char character : "\\/:*?\"<>|".toCharArray()) {
            assertFalse(ProfileNames.isValid("PvP" + character + "test"));
        }
    }
}
