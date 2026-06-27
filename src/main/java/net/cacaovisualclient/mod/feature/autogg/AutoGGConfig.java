package net.cacaovisualclient.mod.feature.autogg;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AutoGGConfig {

    public static final int VERSION = 1;

    private int version = VERSION;
    private String message = "GG";
    private long delayMs = 1000L;
    private List<AutoGGPattern> patterns = new ArrayList<>();

    public void normalize() {
        version = VERSION;

        if (message == null || message.isBlank()) {
            message = "GG";
        }

        delayMs = Math.max(0L, delayMs);

        if (patterns == null) {
            patterns = new ArrayList<>();
        }

        patterns.removeIf(pattern -> pattern == null || !pattern.isValid());
    }
}
