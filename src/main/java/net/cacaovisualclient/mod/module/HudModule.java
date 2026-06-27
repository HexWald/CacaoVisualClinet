package net.cacaovisualclient.mod.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.utils.MouseUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

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
    protected NumberSetting scale = new NumberSetting("Scale", 100.0, 50.0, 200.0, 5.0);

    public HudModule(int x, int y) {
        this.x = x;
        this.y = y;
        addSettings(background, brackets, textShadow, scale);
    }

    public void render(GuiGraphics guiGraphics, Font font) {
        final String text = brackets.getValue() ? "[" + getText() + "]" : getText();
        final float scaleValue = scale.getValue().floatValue() / 100.0F;

        final int textWidth = font.width(text);
        final int textHeight = font.lineHeight;

        final int padding = background.getValue() ? 4 : 0;
        final int unscaledWidth = textWidth + padding * 2;
        final int unscaledHeight = textHeight + padding * 2;

        width = Math.max(1, Math.round(unscaledWidth * scaleValue));
        height = Math.max(1, Math.round(unscaledHeight * scaleValue));

        if (background.getValue()) {
            guiGraphics.fill(
                    x,
                    y,
                    x + width,
                    y + height,
                    new Color(0, 0, 0, 140).getRGB()
            );
        }

        final int textX = padding + (unscaledWidth - padding * 2 - textWidth) / 2;
        final int textY = padding + (unscaledHeight - padding * 2 - textHeight) / 2 + 1;

        final Matrix3x2fStack stack = guiGraphics.pose();
        stack.pushMatrix();
        stack.translate(x, y);
        stack.scale(scaleValue, scaleValue);
        guiGraphics.drawString(
                font,
                text,
                textX,
                textY,
                -1,
                textShadow.getValue()
        );
        stack.popMatrix();
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
