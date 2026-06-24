package net.cacaovisualclient.mod.ui.modmenu.setting;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.settings.ModeSetting;
import net.cacaovisualclient.mod.ui.Widget;
import net.cacaovisualclient.mod.ui.modmenu.ModMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.List;

public class ModeSettingWidget extends Widget {

    private final ModeSetting setting;

    public ModeSettingWidget(ModeSetting setting, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
        super.render(guiGraphics, mouseX, mouseY, scrollOffset);

        if (hovered) {
            guiGraphics.fill(x, y, x + width, y + height, ModMenuScreen.HOVER_COLOR.getRGB());
        }

        final Font font = Minecraft.getInstance().font;
        final int textY = y + (height - font.lineHeight) / 2;

        guiGraphics.drawString(font, setting.getName(), x + 5, textY, Color.WHITE.getRGB());

        final String value = setting.getValue();
        final int valueX = x + width - font.width(value) - 5;
        guiGraphics.drawString(font, value, valueX, textY, CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor().brighter().getRGB());
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        super.mouseClicked(event);

        final List<String> modes = setting.getModes();
        final int currentIndex = modes.indexOf(setting.getValue());

        if (event.button() == 0) {
            final int nextIndex = (currentIndex + 1) % modes.size();
            setting.setValue(modes.get(nextIndex));
        } else if (event.button() == 1) {
            final int prevIndex = (currentIndex - 1 + modes.size()) % modes.size();
            setting.setValue(modes.get(prevIndex));
        }
    }
}
