package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.theme.Theme;
import net.cacaovisualclient.mod.ui.Widget;
import net.cacaovisualclient.mod.utils.ColorUtils;
import net.cacaovisualclient.mod.utils.Notification;
import net.cacaovisualclient.mod.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class ThemeButtonWidget extends Widget {

    private final Theme theme;
    private static final int BORDER_THICKNESS = 2;

    public ThemeButtonWidget(Theme theme, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.theme = theme;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
        super.render(guiGraphics, mouseX, mouseY, scrollOffset);

        final int renderY = y - scrollOffset;

        int borderColor = theme.getPrimaryColor().getRGB();
        int backgroundColor = ColorUtils.blendColors(ModMenuScreen.BASE_GRAY, ColorUtils.modifyAlpha(theme.getPrimaryColor(), 100)).getRGB();

        if (hovered) {
            backgroundColor = ColorUtils.blendColors(new Color(backgroundColor, true), ModMenuScreen.HOVER_COLOR).getRGB();
            borderColor = ColorUtils.blendColors(new Color(borderColor, true), ModMenuScreen.HOVER_COLOR).getRGB();
        }

        guiGraphics.fill(x, renderY, x + width, renderY + height, borderColor);
        guiGraphics.fill(
                x + BORDER_THICKNESS,
                renderY + BORDER_THICKNESS,
                x + width - BORDER_THICKNESS,
                renderY + height - BORDER_THICKNESS,
                backgroundColor
        );

        RenderUtils.scaledItem(
                guiGraphics.pose(),
                guiGraphics,
                theme.getDisplayItem(),
                x + width / 2,
                renderY + height / 2,
                2
        );

        final Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(
                font,
                theme.getName(),
                x + width / 2 - font.width(theme.getName()) / 2,
                renderY + height - (BORDER_THICKNESS * 2) - 10,
                -1,
                true
        );
    }


    @Override
    public void mouseClicked(MouseButtonEvent event) {
        CacaoVisualClient.getInstance().setSelectedTheme(theme);

        Notification.sendNotification("Updated Theme", "Theme was updated to: " + theme.getName());
    }
}
