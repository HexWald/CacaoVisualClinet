package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.event.MouseButtonClickedEvent;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.module.settings.ModeSetting;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

@ModuleInfo(name = "Custom Crosshair", description = "Replaces the vanilla crosshair with cleaner styles")
public class CustomCrosshairModule extends Module {

    private static final long HIT_ANIMATION_DURATION_NANOS = 180_000_000L;

    private final ModeSetting style = new ModeSetting("Style", "Cacao", List.of("Cacao", "Cross", "Dot", "Corner", "Circle"));
    private final NumberSetting size = new NumberSetting("Size", 6.0, 2.0, 16.0, 1.0);
    private final NumberSetting gap = new NumberSetting("Gap", 3.0, 0.0, 10.0, 1.0);
    private final NumberSetting thickness = new NumberSetting("Thickness", 1.0, 1.0, 4.0, 1.0);
    private final BooleanSetting hitAnimation = new BooleanSetting("Hit Animation", true);
    private final NumberSetting hitAnimationStrength = new NumberSetting("Hit Strength", 55.0, 10.0, 90.0, 5.0);
    private final BooleanSetting dot = new BooleanSetting("Center Dot", true);
    private final BooleanSetting outline = new BooleanSetting("Outline", true);

    private long lastHitAtNanos = -1L;

    public CustomCrosshairModule() {
        addSettings(style, size, gap, thickness, hitAnimation, hitAnimationStrength, dot, outline);
        MouseButtonClickedEvent.MOUSE_BUTTON_CLICKED_EVENT.register(this::handleMouseClick);
    }

    public void render(GuiGraphics guiGraphics) {
        if (!isEnabled() || mc.options.hideGui) {
            return;
        }

        final int centerX = guiGraphics.guiWidth() / 2;
        final int centerY = guiGraphics.guiHeight() / 2;
        final float hitAmount = getHitAnimationAmount();
        final float contraction = 1.0F - hitAmount * (hitAnimationStrength.getValue().floatValue() / 100.0F);
        final int crosshairSize = Math.max(1, Math.round(size.getValue().floatValue() * (0.75F + contraction * 0.25F)));
        final int crosshairGap = Math.max(0, Math.round(gap.getValue().floatValue() * contraction));
        final int lineThickness = thickness.getValue().intValue();
        final Color themeColor = CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor().brighter();
        final Color animatedColor = blend(themeColor, Color.WHITE, hitAmount * 0.35F);
        final int color = argb(235, animatedColor);

        if (outline.getValue()) {
            renderStyle(guiGraphics, centerX, centerY, crosshairSize, crosshairGap, lineThickness + 2, argb(150, Color.BLACK));
        }

        renderStyle(guiGraphics, centerX, centerY, crosshairSize, crosshairGap, lineThickness, color);

        if (dot.getValue()) {
            drawRect(guiGraphics, centerX, centerY, lineThickness, lineThickness, color);
        }
    }

    private void handleMouseClick(MouseButtonInfo mouseButtonInfo) {
        if (!isEnabled()
                || mc.player == null
                || mc.level == null
                || mc.screen != null
                || mouseButtonInfo.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }

        lastHitAtNanos = System.nanoTime();
    }

    private float getHitAnimationAmount() {
        if (!hitAnimation.getValue() || lastHitAtNanos == -1L) {
            return 0.0F;
        }

        final long age = System.nanoTime() - lastHitAtNanos;
        if (age >= HIT_ANIMATION_DURATION_NANOS) {
            return 0.0F;
        }

        final float progress = age / (float) HIT_ANIMATION_DURATION_NANOS;
        final float inverse = 1.0F - Math.max(0.0F, Math.min(progress, 1.0F));
        return inverse * inverse;
    }

    private void renderStyle(GuiGraphics guiGraphics, int centerX, int centerY, int size, int gap, int thickness, int color) {
        switch (style.getValue()) {
            case "Dot" -> drawRect(guiGraphics, centerX, centerY, thickness + 1, thickness + 1, color);
            case "Corner" -> renderCorner(guiGraphics, centerX, centerY, size, gap, thickness, color);
            case "Circle" -> renderCircle(guiGraphics, centerX, centerY, Math.max(3, size), color);
            case "Cross" -> renderCross(guiGraphics, centerX, centerY, size, gap, thickness, color);
            default -> renderCacao(guiGraphics, centerX, centerY, size, gap, thickness, color);
        }
    }

    private static void renderCacao(GuiGraphics guiGraphics, int centerX, int centerY, int size, int gap, int thickness, int color) {
        renderCross(guiGraphics, centerX, centerY, size, gap, thickness, color);

        drawRect(guiGraphics, centerX - gap - size, centerY - gap - size, thickness, thickness, color);
        drawRect(guiGraphics, centerX + gap + size, centerY - gap - size, thickness, thickness, color);
        drawRect(guiGraphics, centerX - gap - size, centerY + gap + size, thickness, thickness, color);
        drawRect(guiGraphics, centerX + gap + size, centerY + gap + size, thickness, thickness, color);
    }

    private static void renderCross(GuiGraphics guiGraphics, int centerX, int centerY, int size, int gap, int thickness, int color) {
        drawRect(guiGraphics, centerX, centerY - gap - size / 2, thickness, size, color);
        drawRect(guiGraphics, centerX, centerY + gap + size / 2, thickness, size, color);
        drawRect(guiGraphics, centerX - gap - size / 2, centerY, size, thickness, color);
        drawRect(guiGraphics, centerX + gap + size / 2, centerY, size, thickness, color);
    }

    private static void renderCorner(GuiGraphics guiGraphics, int centerX, int centerY, int size, int gap, int thickness, int color) {
        final int corner = Math.max(3, size / 2);
        final int left = centerX - gap - size;
        final int right = centerX + gap + size;
        final int top = centerY - gap - size;
        final int bottom = centerY + gap + size;

        drawRect(guiGraphics, left + corner / 2, top, corner, thickness, color);
        drawRect(guiGraphics, left, top + corner / 2, thickness, corner, color);
        drawRect(guiGraphics, right - corner / 2, top, corner, thickness, color);
        drawRect(guiGraphics, right, top + corner / 2, thickness, corner, color);
        drawRect(guiGraphics, left + corner / 2, bottom, corner, thickness, color);
        drawRect(guiGraphics, left, bottom - corner / 2, thickness, corner, color);
        drawRect(guiGraphics, right - corner / 2, bottom, corner, thickness, color);
        drawRect(guiGraphics, right, bottom - corner / 2, thickness, corner, color);
    }

    private static void renderCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int color) {
        for (int i = 0; i < 360; i += 18) {
            final double radians = Math.toRadians(i);
            final int x = centerX + (int) Math.round(Math.cos(radians) * radius);
            final int y = centerY + (int) Math.round(Math.sin(radians) * radius);
            guiGraphics.fill(x, y, x + 1, y + 1, color);
        }
    }

    private static void drawRect(GuiGraphics guiGraphics, int centerX, int centerY, int width, int height, int color) {
        final int left = centerX - width / 2;
        final int top = centerY - height / 2;
        guiGraphics.fill(left, top, left + width, top + height, color);
    }

    private static int argb(int alpha, Color color) {
        final int safeAlpha = Math.max(0, Math.min(alpha, 255));
        return safeAlpha << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }

    private static Color blend(Color from, Color to, float progress) {
        final float safeProgress = Math.max(0.0F, Math.min(progress, 1.0F));
        final int red = Math.round(from.getRed() + (to.getRed() - from.getRed()) * safeProgress);
        final int green = Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * safeProgress);
        final int blue = Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * safeProgress);
        return new Color(red, green, blue);
    }
}
