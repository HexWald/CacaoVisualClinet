package net.coralmod.mod.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.coralmod.mod.module.settings.BooleanSetting;
import net.coralmod.mod.utils.MouseUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
public abstract class HudModule extends Module {

    private int x;
    private int y;
    private int width;
    private int height;

    protected BooleanSetting background = new BooleanSetting("Background", false);
    protected BooleanSetting brackets = new BooleanSetting("Text Brackets", true);
    protected BooleanSetting textShadow = new BooleanSetting("Text Shadow", true);

    public HudModule(int x, int y) {
        this.x = x;
        this.y = y;
        addSettings(background, brackets, textShadow);
    }

    public void render(GuiGraphics guiGraphics, Font font) {
        final String text = brackets.getValue() ? "[" + getText() + "]" : getText();

        final int textWidth = font.width(text);
        final int textHeight = font.lineHeight;

        final int padding = background.getValue() ? 4 : 0;

        width = textWidth + padding * 2;
        height = textHeight + padding * 2;

        if (background.getValue()) {
            guiGraphics.fill(
                    x,
                    y,
                    x + width,
                    y + height,
                    new Color(0, 0, 0, 140).getRGB()
            );
        }

        final int textX = x + (width - textWidth) / 2;
        final int textY = y + (height - textHeight) / 2 + 1;

        guiGraphics.drawString(
                font,
                text,
                textX,
                textY,
                -1,
                textShadow.getValue()
        );
    }

    public abstract String getText();

    public boolean isHovered(int mouseX, int mouseY) {
        return MouseUtils.isMouseOver(mouseX, mouseY, x, y, width, height);
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void reset() {
        super.reset();

        move(20, 20);
    }
}
