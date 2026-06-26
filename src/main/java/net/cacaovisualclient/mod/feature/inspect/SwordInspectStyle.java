package net.cacaovisualclient.mod.feature.inspect;

import java.util.Arrays;
import java.util.Optional;

public enum SwordInspectStyle {

    BUTTERFLY("Butterfly"),
    AGGRESSIVE_FLIP("Aggressive flip"),
    SHOWCASE_SPIN("Showcase spin");

    private final String displayName;

    SwordInspectStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<SwordInspectStyle> fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(style -> style.displayName.equalsIgnoreCase(displayName))
                .findFirst();
    }
}
