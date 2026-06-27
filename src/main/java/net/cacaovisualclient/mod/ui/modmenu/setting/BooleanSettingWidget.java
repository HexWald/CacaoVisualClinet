package net.cacaovisualclient.mod.ui.modmenu.setting;

import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.ui.Widget;
import net.cacaovisualclient.mod.ui.modmenu.ModMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class BooleanSettingWidget extends Widget {

    private final BooleanSetting setting;

    public BooleanSettingWidget(BooleanSetting setting, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
        super.render(guiGraphics, mouseX, mouseY, scrollOffset);

        final int renderY = y - scrollOffset;

        if (hovered) {
            guiGraphics.fill(x, renderY, x + width, renderY + height, ModMenuScreen.HOVER_COLOR.getRGB());
        }

        final Font font = Minecraft.getInstance().font;
        final int textY = renderY + (height - font.lineHeight) / 2;

        guiGraphics.drawString(font, setting.getName(), x + 5, textY, Color.WHITE.getRGB());

        final String value = setting.getValue() ? "On" : "Off";
        final int valueColor = setting.getValue()
                ? new Color(80, 255, 80).getRGB()
                : new Color(255, 80, 80).getRGB();

        final int valueX = x + width - font.width(value) - 5;
        guiGraphics.drawString(font, value, valueX, textY, valueColor);
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        super.mouseClicked(event);
        if (hovered && event.button() == 0) {
            setting.setValue(!setting.getValue());
        }
    }
}
