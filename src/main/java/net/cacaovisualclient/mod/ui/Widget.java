package net.cacaovisualclient.mod.ui;

import lombok.RequiredArgsConstructor;
import net.cacaovisualclient.mod.utils.MouseUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

@RequiredArgsConstructor
public class Widget {

    protected final int x;
    protected final int y;
    protected int width;
    protected int height;
    protected boolean hovered;
    protected Window parent;

    public Widget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void init() {
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
        final int renderY = y - scrollOffset;

        hovered = MouseUtils.isMouseOver(
                mouseX,
                mouseY,
                x,
                renderY,
                width,
                height
        );
    }

    public void mouseClicked(MouseButtonEvent event) {
    }

    public void mouseReleased(MouseButtonEvent event) {
    }

}
