package net.cacaovisualclient.mod.ui;

import lombok.Getter;
import net.cacaovisualclient.mod.ui.modmenu.ModMenuScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Getter
public class Window {

    protected final ModMenuScreen parent;
    protected final String name;
    protected final int x;
    protected int y;

    protected int width = ModMenuScreen.MENU_WIDTH;
    protected int height = ModMenuScreen.MENU_HEIGHT - ModMenuScreen.MENU_TITLE_BAR_HEIGHT;

    protected int scrollOffset = 0;
    protected int maxScroll = 0;

    protected final List<Widget> widgets = new CopyOnWriteArrayList<>();

    public Window(ModMenuScreen parent, String name, int x, int y) {
        this.parent = parent;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public void init() {
        for (Widget widget : widgets) {
            widget.init();
            maxScroll = Math.max(
                    maxScroll,
                    (widget.y + widget.height) - (y + height)
            );
        }

        maxScroll = Math.max(0, maxScroll + 10);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        final int contentY = y + ModMenuScreen.BUTTON_TOP_MARGIN;
        final int contentHeight = height - ModMenuScreen.BUTTON_TOP_MARGIN;

        guiGraphics.enableScissor(x, contentY, x + width, y + height);
        for (Widget widget : widgets) {
            widget.render(guiGraphics, mouseX, mouseY, scrollOffset);
        }
        guiGraphics.disableScissor();

        if (maxScroll > 10) {
            final int scrollbarWidth = 2;
            final int scrollbarX = x + width - scrollbarWidth - 2;
            final int scrollbarHeight = (int) ((float) contentHeight / (contentHeight + maxScroll) * contentHeight);
            final int scrollbarY = contentY + (int) ((float) scrollOffset / maxScroll * (contentHeight - scrollbarHeight));

            guiGraphics.fill(scrollbarX, contentY, scrollbarX + scrollbarWidth, contentY + contentHeight, ModMenuScreen.BASE_GRAY.darker().getRGB());
            guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, Color.GRAY.getRGB());
        }
    }

    public void mouseClicked(MouseButtonEvent event) {
        for (Widget widget : widgets) {
            if (widget.hovered) {
                widget.mouseClicked(event);
            }
        }
    }

    public void mouseReleased(MouseButtonEvent event) {
        for (Widget widget : widgets) {
            widget.mouseReleased(event);
        }
    }

    protected void addWidget(Widget widget) {
        widgets.add(widget);
        widget.parent = this;
    }

    public void mouseScrolled(double delta) {
        scrollOffset -= (int) (delta * 10);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
    }
}
