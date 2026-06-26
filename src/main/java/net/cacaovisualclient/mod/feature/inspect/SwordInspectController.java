package net.cacaovisualclient.mod.feature.inspect;

import net.cacaovisualclient.mod.module.modules.SwordInspectModule;

import java.util.Random;

public class SwordInspectController {

    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    private final Random random = new Random();

    private long startedAtNanos = -1L;
    private int nextCycleIndex;
    private boolean queued;
    private SwordInspectStyle activeStyle = SwordInspectStyle.BUTTERFLY;

    public void request(SwordInspectModule module) {
        if (isRunning()) {
            if (module.shouldQueuePresses()) {
                queued = true;
            }
            return;
        }

        start(module);
    }

    public void tick(SwordInspectModule module) {
        if (!isRunning()) {
            return;
        }

        if (System.nanoTime() - startedAtNanos < getDurationNanos(module)) {
            return;
        }

        if (queued) {
            queued = false;
            start(module);
            return;
        }

        stop();
    }

    public void stop() {
        startedAtNanos = -1L;
        queued = false;
    }

    public float getProgress(SwordInspectModule module) {
        if (!isRunning()) {
            return -1.0F;
        }

        final float progress = (System.nanoTime() - startedAtNanos) / (float) getDurationNanos(module);
        return Math.min(progress, 1.0F);
    }

    public SwordInspectStyle getActiveStyle() {
        return activeStyle;
    }

    private boolean isRunning() {
        return startedAtNanos != -1L;
    }

    private void start(SwordInspectModule module) {
        activeStyle = chooseStyle(module);
        startedAtNanos = System.nanoTime();
    }

    private SwordInspectStyle chooseStyle(SwordInspectModule module) {
        final String mode = module.getStyleMode();

        if ("Random".equalsIgnoreCase(mode)) {
            final SwordInspectStyle[] styles = SwordInspectStyle.values();
            return styles[random.nextInt(styles.length)];
        }

        if ("Cycle".equalsIgnoreCase(mode)) {
            final SwordInspectStyle[] styles = SwordInspectStyle.values();
            final SwordInspectStyle style = styles[nextCycleIndex % styles.length];
            nextCycleIndex = (nextCycleIndex + 1) % styles.length;
            return style;
        }

        return SwordInspectStyle.fromDisplayName(mode)
                .orElse(SwordInspectStyle.BUTTERFLY);
    }

    private long getDurationNanos(SwordInspectModule module) {
        return Math.max(1L, (long) (module.getDurationSeconds() * NANOS_PER_SECOND));
    }
}
