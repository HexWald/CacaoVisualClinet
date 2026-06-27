package net.cacaovisualclient.mod.feature.autogg;

import java.util.Locale;

public enum AutoGGMatchMode {

    CONTAINS,
    REGEX;

    public static AutoGGMatchMode fromString(String value) {
        if (value == null || value.isBlank()) {
            return CONTAINS;
        }

        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return CONTAINS;
        }
    }
}
