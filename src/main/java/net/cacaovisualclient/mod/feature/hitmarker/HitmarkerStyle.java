package net.cacaovisualclient.mod.feature.hitmarker;

import java.util.Arrays;
import java.util.Optional;

public enum HitmarkerStyle {

    CLASSIC("Classic"),
    EXPAND("Expand"),
    DIAMOND("Diamond"),
    PLUS("Plus"),
    BURST("Burst");

    private final String displayName;

    HitmarkerStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<HitmarkerStyle> fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(style -> style.displayName.equalsIgnoreCase(displayName))
                .findFirst();
    }
}
