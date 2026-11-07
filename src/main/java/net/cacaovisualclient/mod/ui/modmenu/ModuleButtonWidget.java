package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.modules.AutoGGModule;
import net.cacaovisualclient.mod.ui.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ModuleButtonWidget extends Widget {
    private final Module module;

    public ModuleButtonWidget(Module module, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.module = module;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, int scrollOffset) {
        super.render(graphics, mouseX, mouseY, scrollOffset);
        int rowY = y - scrollOffset;
        int color = CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor().getRGB();
        graphics.fill(x, rowY, x + width, rowY + height, hovered ? 0xFF303030 : 0xFF232323);
        if (module.isEnabled()) {
            graphics.fill(x, rowY, x + 2, rowY + height, color);
        }

        Font font = Minecraft.getInstance().font;
        int textY = rowY + (height - font.lineHeight) / 2;
        String name = font.plainSubstrByWidth(module.getName(), width - 110);
        graphics.drawString(font, name, x + 8, textY, -1, false);
        graphics.drawString(font, module.isEnabled() ? "On" : "Off", x + width - 92, textY,
                module.isEnabled() ? color : 0xFF999999, false);
        if (hasSettings()) {
            graphics.drawString(font, "Settings", x + width - 56, textY, 0xFFBBBBBB, false);
        }
        if (hovered && !module.getDescription().isBlank()) {
            graphics.setTooltipForNextFrame(font, Component.literal(module.getDescription()), mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        if (hasSettings() && (event.button() == 1 || event.button() == 0 && event.x() >= x + width - 64)) {
            parent.getParent().switchWindow(
                    new ModuleSettingsWindow(parent.getParent(), module, "Settings", parent.getX(), parent.getY()));
        } else if (event.button() == 0) {
            module.setEnabled(!module.isEnabled());
        }
    }

    private boolean hasSettings() {
        return !module.getSettings().isEmpty() || module instanceof AutoGGModule;
    }
}
