package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.event.MouseButtonClickedEvent;
import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "CPS", description = "Shows your CPS")
public class CPSModule extends HudModule {

    private final BooleanSetting showCpsText = new BooleanSetting("Show CPS Text", true);
    private final BooleanSetting showLeftButton = new BooleanSetting("Show Left CPS", true);
    private final BooleanSetting showRightButton = new BooleanSetting("Show Right CPS", true);

    private final List<Long> leftPresses = new ArrayList<>();
    private final List<Long> rightPresses = new ArrayList<>();

    public CPSModule() {
        super(20, 20);
        addSettings(showCpsText, showLeftButton, showRightButton);

        MouseButtonClickedEvent.MOUSE_BUTTON_CLICKED_EVENT.register(event -> {
            final long currentTime = System.currentTimeMillis();

            if (event.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
                leftPresses.add(currentTime);
            } else if (event.button() == GLFW.GLFW_MOUSE_BUTTON_2) {
                rightPresses.add(currentTime);
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            leftPresses.removeIf(lastPress -> System.currentTimeMillis() - lastPress > 1000);
            rightPresses.removeIf(lastPress -> System.currentTimeMillis() - lastPress > 1000);
        });
    }

    @Override
    public String getText() {
        final StringBuilder builder = new StringBuilder();

        if (showCpsText.getValue()) {
            builder.append("CPS: ");
        }

        if (showLeftButton.getValue()) {
            builder.append(leftPresses.size());
        }

        if (showLeftButton.getValue() && showRightButton.getValue()) {
            builder.append(" | ");
        }

        if (showRightButton.getValue()) {
            builder.append(rightPresses.size());
        }
        return builder.toString();
    }
}
