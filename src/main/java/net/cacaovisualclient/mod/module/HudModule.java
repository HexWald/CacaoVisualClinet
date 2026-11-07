package net.cacaovisualclient.mod.module;

import lombok.Getter;
import lombok.Setter;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.ui.editor.EditHudScreen;
import net.cacaovisualclient.mod.utils.MouseUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

import java.util.List;

@Getter
@Setter
public abstract class HudModule extends Module {

    private final int defaultX;
    private final int defaultY;
    private int x;
    private int y;
    private int width;
    private int height;

    protected BooleanSetting background = new BooleanSetting("Background", false);
    protected BooleanSetting brackets = new BooleanSetting("Text Brackets", false);
    protected BooleanSetting textShadow = new BooleanSetting("Text Shadow", true);
    protected NumberSetting scale = new NumberSetting("Scale", 100.0, 50.0, 200.0, 5.0);

    public HudModule(int x, int y) {
        this.x = x;
        this.y = y;
        defaultX = x;
        defaultY = y;
        addSettings(background, brackets, textShadow, scale);
    }

    public void render(GuiGraphics guiGraphics, Font font) {
        String text = getText();
        renderLines(guiGraphics, font, text == null || text.isEmpty() ? List.of() : List.of(text));
    }

    protected void renderLines(GuiGraphics graphics, Font font, List<String> lines) {
        if (lines.isEmpty()) {
            if (!(mc.screen instanceof EditHudScreen)) {
                width = 0;
                height = 0;
                return;
            }
            lines = List.of(getName());
        }

        if (brackets.getValue()) {
            lines = lines.stream().map(line -> "[" + line + "]").toList();
        }
        int padding = background.getValue() ? 4 : 0;
        int textWidth = lines.stream().mapToInt(font::width).max().orElse(0);
        int lineHeight = font.lineHeight + 2;
        layout(graphics, textWidth + padding * 2, lines.size() * lineHeight + padding * 2);

        Matrix3x2fStack stack = graphics.pose();
        stack.pushMatrix();
        stack.translate(x, y);
        stack.scale(scaleFactor(), scaleFactor());
        int row = padding + 1;
        for (String line : lines) {
            graphics.drawString(font, line, padding, row, -1, textShadow.getValue());
            row += lineHeight;
        }
        stack.popMatrix();
    }

    protected void layout(GuiGraphics graphics, int contentWidth, int contentHeight) {
        width = (int) Math.ceil(contentWidth * scaleFactor());
        height = (int) Math.ceil(contentHeight * scaleFactor());
        keepOnScreen(graphics.guiWidth(), graphics.guiHeight());
        if (background.getValue()) {
            graphics.fill(x, y, x + width, y + height, 0x8C000000);
        }
    }

    protected float scaleFactor() {
        return scale.getValue().floatValue() / 100.0F;
    }

    public void keepOnScreen(int screenWidth, int screenHeight) {
        move(Math.clamp(x, 0, Math.max(0, screenWidth - width)),
                Math.clamp(y, 0, Math.max(0, screenHeight - height)));
    }

    public abstract String getText();

    public boolean isHovered(int mouseX, int mouseY) {
        return width > 0 && height > 0 && MouseUtils.isMouseOver(mouseX, mouseY, x, y, width, height);
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void resetPosition() {
        move(defaultX, defaultY);
    }

    @Override
    public void reset() {
        super.reset();

        resetPosition();
    }
}
