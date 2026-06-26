package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.feature.inspect.SwordInspectController;
import net.cacaovisualclient.mod.feature.inspect.SwordInspectStyle;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.module.settings.ModeSetting;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
        name = "Sword Inspect",
        description = "Adds CS-style first-person sword inspect animations"
)
public class SwordInspectModule extends Module {

    private final SwordInspectController controller = new SwordInspectController();

    private final ModeSetting style = new ModeSetting("Style", "Cycle", getStyleModes());
    private final NumberSetting duration = new NumberSetting("Duration", 2.6, 1.0, 4.0, 0.1);
    private final BooleanSetting queuePresses = new BooleanSetting("Queue presses", true);

    public SwordInspectModule() {
        addSettings(style, duration, queuePresses);
        setEnabled(true);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!isEnabled()) {
                controller.stop();
                consumeQueuedKeyPresses();
                return;
            }

            while (CacaoVisualClient.INSPECT_KEY_MAPPING.consumeClick()) {
                if (client.player != null && client.screen == null) {
                    controller.request(this);
                }
            }

            controller.tick(this);
        });
    }

    public SwordInspectController getController() {
        return controller;
    }

    public String getStyleMode() {
        return style.getValue();
    }

    public double getDurationSeconds() {
        return duration.getValue();
    }

    public boolean shouldQueuePresses() {
        return queuePresses.getValue();
    }

    private void consumeQueuedKeyPresses() {
        while (CacaoVisualClient.INSPECT_KEY_MAPPING.consumeClick()) {
            // Drop presses while the module is disabled.
        }
    }

    private static List<String> getStyleModes() {
        final List<String> modes = new ArrayList<>();
        modes.add("Cycle");
        modes.add("Random");

        for (SwordInspectStyle style : SwordInspectStyle.values()) {
            modes.add(style.getDisplayName());
        }

        return modes;
    }
}
