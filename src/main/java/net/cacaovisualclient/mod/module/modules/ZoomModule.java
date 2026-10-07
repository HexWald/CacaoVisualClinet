package net.cacaovisualclient.mod.module.modules;

import lombok.Getter;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Options;

@ModuleInfo(name = "Zoom", description = "Zooms in the game")
public class ZoomModule extends Module {

    @Getter
    private boolean zooming = false;

    private int oldFov;
    private double oldSensitivity;
    private boolean oldSmoothCamera;

    public ZoomModule() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            final boolean shouldZoom = isEnabled()
                    && client.player != null
                    && client.screen == null
                    && client.isWindowActive()
                    && CacaoVisualClient.ZOOM_KEY_MAPPING.isDown();

            if (shouldZoom && !zooming) {
                startZooming();
            } else if (!shouldZoom && zooming) {
                stopZooming();
            }
        });
    }

    @Override
    public void onDisable() {
        if (zooming) {
            stopZooming();
        }
    }

    private void startZooming() {
        if (mc == null) {
            return;
        }

        if (mc.screen != null) {
            return;
        }

        final Options options = mc.options;

        oldFov = options.fov().get();
        oldSensitivity = options.sensitivity().get();
        oldSmoothCamera = options.smoothCamera;

        options.fov().set(30);
        options.smoothCamera = true;
        options.sensitivity().set(0.2);

        zooming = true;
    }

    private void stopZooming() {
        if (mc == null) {
            return;
        }

        final Options options = mc.options;

        options.fov().set(oldFov);
        options.smoothCamera = oldSmoothCamera;
        options.sensitivity().set(oldSensitivity);

        zooming = false;
    }
}
