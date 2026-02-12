package net.coralmod.mod.ui.modmenu.setting;

import net.coralmod.mod.CoralMod;
import net.coralmod.mod.module.settings.NumberSetting;
import net.coralmod.mod.theme.Theme;
import net.coralmod.mod.ui.Widget;
import net.coralmod.mod.ui.modmenu.ModMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

import java.awt.*;

public class NumberSettingWidget extends Widget {

    private final NumberSetting setting;
    private boolean sliding;

    public NumberSettingWidget(NumberSetting setting, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void init() {
        sliding = false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
        super.render(guiGraphics, mouseX, mouseY, scrollOffset);
        updateValue(mouseX);

        final Color baseGray = ModMenuScreen.BASE_GRAY;
        final Theme theme = CoralMod.getInstance().getSelectedTheme();

        final double renderWidth = (double) (width) * (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());

        guiGraphics.fillGradient(x, y, x + width, y + height, baseGray.getRGB(), baseGray.darker().getRGB());
        guiGraphics.fillGradient(x,
                y,
                (int) (x + renderWidth),
                y + height,
                theme.getPrimaryColor().getRGB(),
                theme.getSecondaryColor().getRGB()
        );

        if (hovered) {
            guiGraphics.fill(x, y, x + width, y + height, ModMenuScreen.HOVER_COLOR.getRGB());
        }

        final Font font = Minecraft.getInstance().font;
        final int textY = y + (height - font.lineHeight) / 2;

        guiGraphics.drawString(font, setting.getName(), x + 5, textY, Color.WHITE.getRGB());

        final String valueText = formatValue(setting.getValue()) + "/" + setting.getMax();
        guiGraphics.drawString(font, valueText, x + width - 5 - font.width(valueText), textY, Color.WHITE.getRGB());
    }

    private String formatValue(double value) {
        if (setting.getIncrement() < 1) {
            return String.format("%.2f", value);
        }
        return String.valueOf((int) value);
    }

    private void updateValue(int mouseX) {
        if (sliding) {
            final double diff = Math.min(width, Math.max(0, mouseX - x));
            final double range = setting.getMax() - setting.getMin();
            final double newValue = setting.getMin() + (diff / width) * range;
            setting.setValue(roundToIncrement(Mth.clamp(newValue, setting.getMin(), setting.getMax())));
        }
    }

    private double roundToIncrement(double value) {
        double increment = setting.getIncrement();
        return Math.round(value / increment) * increment;
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        super.mouseClicked(event);
        if (hovered && event.button() == 0) {
            sliding = true;
        }
    }

    @Override
    public void mouseReleased(MouseButtonEvent event) {
        sliding = false;
        super.mouseReleased(event);
    }
}
