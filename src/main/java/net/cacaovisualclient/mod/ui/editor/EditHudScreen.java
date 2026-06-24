package net.cacaovisualclient.mod.ui.editor;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleManager;
import net.cacaovisualclient.mod.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class EditHudScreen extends Screen {

    public static EditHudScreen INSTANCE = new EditHudScreen();

    private final Minecraft mc = Minecraft.getInstance();

    private HudModule selectedModule;
    private int offsetX;
    private int offsetY;

    protected EditHudScreen() {
        super(Component.literal("Edit Hud"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        final ModuleManager moduleManager = CacaoVisualClient.getInstance().getModuleManager();

        for (HudModule hudModule : moduleManager.getHudModules()) {
            if (hudModule.isEnabled()) {
                hudModule.render(guiGraphics, font);

                final Color outlineColor = hudModule == selectedModule ? CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor().brighter() : Color.WHITE;
                RenderUtils.outline(guiGraphics,
                        hudModule.getX(),
                        hudModule.getY(),
                        hudModule.getX() + hudModule.getWidth(),
                        hudModule.getY() + hudModule.getHeight(),
                        1,
                        outlineColor);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (event.button() == 0) {
            final HudModule module = getModuleUnderMouse((int) event.x(), (int) event.y());

            if (module != null) {
                selectedModule = module;
                offsetX = (int) event.x() - selectedModule.getX();
                offsetY = (int) event.y() - selectedModule.getY();
                return true;
            }
        }

        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && selectedModule != null) {
            selectedModule = null;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double d, double e) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && selectedModule != null) {

            int newX = (int) event.x() - offsetX;
            int newY = (int) event.y() - offsetY;

            newX = Math.max(0, Math.min(newX, mc.getWindow().getScreenWidth() - selectedModule.getWidth()));
            newY = Math.max(0, Math.min(newY, mc.getWindow().getScreenHeight() - selectedModule.getHeight()));

            final int snapDistance = 5;

            for (HudModule other : CacaoVisualClient.getInstance().getModuleManager().getHudModules()) {
                if (other == selectedModule || !other.isEnabled()) {
                    continue;
                }

                if (Math.abs(newX - (other.getX() + other.getWidth())) <= snapDistance) {
                    newX = other.getX() + other.getWidth();
                } else if (Math.abs((newX + selectedModule.getWidth()) - other.getX()) <= snapDistance) {
                    newX = other.getX() - selectedModule.getWidth();
                }

                if (Math.abs(newY - (other.getY() + other.getHeight())) <= snapDistance) {
                    newY = other.getY() + other.getHeight();
                } else if (Math.abs((newY + selectedModule.getHeight()) - other.getY()) <= snapDistance) {
                    newY = other.getY() - selectedModule.getHeight();
                }
            }

            selectedModule.move(newX, newY);
            return true;
        }
        return super.mouseDragged(event, d, e);
    }

    private HudModule getModuleUnderMouse(int mouseX, int mouseY) {
        return CacaoVisualClient.getInstance().getModuleManager().getHudModules().stream()
                .filter(Module::isEnabled)
                .filter(module -> module.isHovered(mouseX, mouseY))
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void renderBlurredBackground(GuiGraphics guiGraphics) {}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
