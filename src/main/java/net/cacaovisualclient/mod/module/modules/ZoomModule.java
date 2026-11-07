package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.module.settings.NumberSetting;

@ModuleInfo(name = "Zoom", description = "Hold to zoom and scroll to adjust the view")
public class ZoomModule extends Module {

    private final NumberSetting magnification = new NumberSetting("Magnification", 3.0, 1.5, 8.0, 0.5);
    private final BooleanSetting hideHand = new BooleanSetting("Hide hand", true);
    private double factor = 1.0;
    private double target = 1.0;
    private long lastFrame;
    private boolean held;

    public ZoomModule() {
        addSettings(magnification, hideHand);
    }

    @Override
    public void onDisable() {
        resetZoom();
    }

    public float applyFov(float fov) {
        if (!canZoom()) {
            resetZoom();
            return fov;
        }

        boolean down = CacaoVisualClient.ZOOM_KEY_MAPPING.isDown();
        if (down && !held) {
            target = magnification.getValue();
        }
        held = down;

        long now = System.nanoTime();
        double elapsed = lastFrame == 0L ? 0.016 : Math.min(0.1, (now - lastFrame) / 1_000_000_000.0);
        lastFrame = now;
        double next = held ? target : 1.0;
        factor += (next - factor) * (1.0 - Math.exp(-elapsed * 18.0));
        if (Math.abs(next - factor) < 0.001) {
            factor = next;
        }
        if (factor == 1.0) {
            return fov;
        }
        double halfAngle = Math.tan(Math.toRadians(fov * 0.5));
        return (float) Math.toDegrees(2.0 * Math.atan(halfAngle / factor));
    }

    public boolean scroll(double amount) {
        if (!canZoom() || !held || !CacaoVisualClient.ZOOM_KEY_MAPPING.isDown() || amount == 0.0) {
            return false;
        }
        target = Math.clamp(target * Math.pow(1.25, amount), 1.5, 16.0);
        return true;
    }

    public double mouseScale() {
        return canZoom() ? 1.0 / factor : 1.0;
    }

    public boolean hidesHand() {
        return canZoom() && hideHand.getValue() && factor > 1.01;
    }

    private boolean canZoom() {
        return isEnabled() && mc.player != null && mc.screen == null && mc.isWindowActive();
    }

    private void resetZoom() {
        factor = 1.0;
        held = false;
        lastFrame = 0L;
    }
}
