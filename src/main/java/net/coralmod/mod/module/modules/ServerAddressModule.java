package net.coralmod.mod.module.modules;

import net.coralmod.mod.module.HudModule;
import net.coralmod.mod.module.ModuleInfo;
import net.coralmod.mod.module.settings.BooleanSetting;
import net.coralmod.mod.module.settings.NumberSetting;
import net.coralmod.mod.utils.RenderUtils;
import net.coralmod.mod.utils.ServerUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.awt.*;

@ModuleInfo(name = "Server Address", description = "Displays the server address")
public class ServerAddressModule extends HudModule {

    private Identifier currentServerIcon;

    private final BooleanSetting showServerIcon = new BooleanSetting("Show Server Icon", true);
    private final NumberSetting serverIconSize = new NumberSetting("Server Icon Size", 12, 5, 32, 1);

    public ServerAddressModule() {
        super(20, 20);
        addSettings(showServerIcon, serverIconSize);

        brackets.setValue(false);

        ClientPlayConnectionEvents.JOIN.register((clientPacketListener, packetSender, minecraft) -> {
            // Update the server icon after joining a new server
            currentServerIcon = ServerUtils.getServerIcon();
        });
    }

    @Override
    public void onEnable() {
        currentServerIcon = ServerUtils.getServerIcon();
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font) {
        final String text = brackets.getValue() ? "[" + getText() + "]" : getText();

        final int textWidth = font.width(text);
        final int textHeight = font.lineHeight;

        final int padding = background.getValue() ? 2 : 0;
        final int iconSize = showServerIcon.getValue() && currentServerIcon != null ? this.serverIconSize.getValue().intValue() : 0;

        setWidth(iconSize + (showServerIcon.getValue() ? 4 : 0) + textWidth + padding * 2);
        setHeight(Math.max(iconSize, textHeight) + padding * 2);

        if (background.getValue()) {
            guiGraphics.fill(
                    getX(),
                    getY(),
                    getX() + getWidth(),
                    getY() + getHeight(),
                    new Color(0, 0, 0, 140).getRGB()
            );
        }

        if (showServerIcon.getValue() && currentServerIcon != null) {
            final int iconX = getX() + padding;
            final int iconY = getY() + padding + (getHeight() - padding * 2 - iconSize) / 2;

            RenderUtils.drawTexture(guiGraphics, currentServerIcon, iconX, iconY, iconSize);
        }

        final int textX = getX() + padding + iconSize + (showServerIcon.getValue() ? 4 : 0);
        final int textY = getY() + padding + (getHeight() - padding * 2 - textHeight) / 2;

        guiGraphics.drawString(font, text, textX, textY + 1, -1, textShadow.getValue());
    }


    @Override
    public String getText() {
        if (mc.isSingleplayer()) {
            return "Singleplayer";
        }

        if (mc.getCurrentServer() == null) {
            return "Unknown";
        }

        return mc.getCurrentServer().ip;
    }
}
