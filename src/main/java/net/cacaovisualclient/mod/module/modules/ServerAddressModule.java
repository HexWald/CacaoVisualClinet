package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import net.cacaovisualclient.mod.utils.RenderUtils;
import net.cacaovisualclient.mod.utils.ServerUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

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
        final float factor = scaleFactor();
        final int padding = background.getValue() ? 2 : 0;
        final int iconSize = showServerIcon.getValue() && currentServerIcon != null
                ? serverIconSize.getValue().intValue() : 0;
        final int textX = padding + (iconSize > 0 ? iconSize + 4 : 0);
        final int rowHeight = Math.max(iconSize, font.lineHeight);
        final int maxTextWidth = Math.max(0, (int) (guiGraphics.guiWidth() / (double) factor) - textX - padding);
        final boolean wrap = brackets.getValue();
        final String address = getText();
        String text = wrap ? "[" + address + "]" : address;
        if (font.width(text) > maxTextWidth) {
            final String end = wrap ? "...]" : "...";
            final int remaining = maxTextWidth - font.width(end);
            text = remaining > 0 ? font.plainSubstrByWidth(text, remaining) + end
                    : font.plainSubstrByWidth(text, maxTextWidth);
        }
        layout(guiGraphics, textX + font.width(text) + padding, rowHeight + padding * 2);

        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(getX(), getY());
        pose.scale(factor, factor);
        if (iconSize > 0) {
            RenderUtils.drawTexture(guiGraphics, currentServerIcon, padding, padding + (rowHeight - iconSize) / 2, iconSize);
        }
        final int textY = padding + (rowHeight - font.lineHeight) / 2;
        guiGraphics.drawString(font, text, textX, textY + 1, -1, textShadow.getValue());
        pose.popMatrix();
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
