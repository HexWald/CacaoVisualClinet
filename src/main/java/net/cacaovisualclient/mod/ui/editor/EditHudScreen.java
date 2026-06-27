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
    private Integer snapGuideX;
    private Integer snapGuideY;

    protected EditHudScreen() {
        super(Component.literal("Edit Hud"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        final ModuleManager moduleManager = CacaoVisualClient.getInstance().getModuleManager();

        guiGraphics.drawString(
                font,
                "Drag HUD elements. Scroll over one to scale. Right click resets position.",
                8,
                8,
                new Color(255, 255, 255, 190).getRGB(),
                true
        );

        renderSnapGuides(guiGraphics);

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

        if (event.button() == 1) {
            final HudModule module = getModuleUnderMouse((int) event.x(), (int) event.y());
            if (module != null) {
                module.move(20, 20);
                return true;
            }
        }

        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && selectedModule != null) {
            selectedModule = null;
            snapGuideX = null;
            snapGuideY = null;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double d, double e) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && selectedModule != null) {

            int newX = (int) event.x() - offsetX;
            int newY = (int) event.y() - offsetY;

            newX = Math.max(0, Math.min(newX, width - selectedModule.getWidth()));
            newY = Math.max(0, Math.min(newY, height - selectedModule.getHeight()));

            final int snapDistance = 6;
            snapGuideX = null;
            snapGuideY = null;

            newX = snapX(newX, snapDistance);
            newY = snapY(newY, snapDistance);

            for (HudModule other : CacaoVisualClient.getInstance().getModuleManager().getHudModules()) {
                if (other == selectedModule || !other.isEnabled()) {
                    continue;
                }

                if (Math.abs(newX - (other.getX() + other.getWidth())) <= snapDistance) {
                    newX = other.getX() + other.getWidth();
                    snapGuideX = newX;
                } else if (Math.abs((newX + selectedModule.getWidth()) - other.getX()) <= snapDistance) {
                    newX = other.getX() - selectedModule.getWidth();
                    snapGuideX = other.getX();
                }

                if (Math.abs(newY - (other.getY() + other.getHeight())) <= snapDistance) {
                    newY = other.getY() + other.getHeight();
                    snapGuideY = newY;
                } else if (Math.abs((newY + selectedModule.getHeight()) - other.getY()) <= snapDistance) {
                    newY = other.getY() - selectedModule.getHeight();
                    snapGuideY = other.getY();
                }
            }

            selectedModule.move(newX, newY);
            return true;
        }
        return super.mouseDragged(event, d, e);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        final HudModule module = selectedModule != null ? selectedModule : getModuleUnderMouse((int) mouseX, (int) mouseY);
        if (module == null) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        final double value = module.getScale().getValue() + scrollY * 5.0;
        module.getScale().setValue(Math.max(50.0, Math.min(200.0, value)));
        return true;
    }

    private int snapX(int value, int snapDistance) {
        final int moduleCenter = value + selectedModule.getWidth() / 2;
        final int screenCenter = width / 2;

        if (Math.abs(value) <= snapDistance) {
            snapGuideX = 0;
            return 0;
        }

        if (Math.abs((value + selectedModule.getWidth()) - width) <= snapDistance) {
            snapGuideX = width;
            return width - selectedModule.getWidth();
        }

        if (Math.abs(moduleCenter - screenCenter) <= snapDistance) {
            snapGuideX = screenCenter;
            return screenCenter - selectedModule.getWidth() / 2;
        }

        return value;
    }

    private int snapY(int value, int snapDistance) {
        final int moduleCenter = value + selectedModule.getHeight() / 2;
        final int screenCenter = height / 2;

        if (Math.abs(value) <= snapDistance) {
            snapGuideY = 0;
            return 0;
        }

        if (Math.abs((value + selectedModule.getHeight()) - height) <= snapDistance) {
            snapGuideY = height;
            return height - selectedModule.getHeight();
        }

        if (Math.abs(moduleCenter - screenCenter) <= snapDistance) {
            snapGuideY = screenCenter;
            return screenCenter - selectedModule.getHeight() / 2;
        }

        return value;
    }

    private void renderSnapGuides(GuiGraphics guiGraphics) {
        final Color guideColor = CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor().brighter();

        if (snapGuideX != null) {
            guiGraphics.fill(snapGuideX, 0, snapGuideX + 1, height, guideColor.getRGB());
        }

        if (snapGuideY != null) {
            guiGraphics.fill(0, snapGuideY, width, snapGuideY + 1, guideColor.getRGB());
        }
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
