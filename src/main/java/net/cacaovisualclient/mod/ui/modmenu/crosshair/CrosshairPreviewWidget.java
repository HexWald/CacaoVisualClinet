package net.cacaovisualclient.mod.ui.modmenu.crosshair;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.modules.CustomCrosshairModule;
import net.cacaovisualclient.mod.ui.Widget;
import net.cacaovisualclient.mod.ui.modmenu.ModMenuScreen;
import net.cacaovisualclient.mod.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class CrosshairPreviewWidget extends Widget {

    private static final int PADDING = 8;
    private static final long PREVIEW_ANIMATION_NANOS = 180_000_000L;

    private final CustomCrosshairModule module;
    private long previewClickAtNanos = -1L;

    public CrosshairPreviewWidget(CustomCrosshairModule module, int x, int y, int width) {
        super(x, y, width, 72);
        this.module = module;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
        super.render(guiGraphics, mouseX, mouseY, scrollOffset);

        final int renderY = y - scrollOffset;
        final int cardX = x + PADDING;
        final int cardY = renderY;
        final int cardWidth = width - PADDING * 2;
        final int cardHeight = height;
        final Font font = Minecraft.getInstance().font;
        final Color themeColor = CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor();
        Color cardColor = new Color(18, 18, 18, 220);

        if (hovered) {
            cardColor = ColorUtils.blendColors(cardColor, ModMenuScreen.HOVER_COLOR);
        }

        guiGraphics.fillGradient(
                cardX,
                cardY,
                cardX + cardWidth,
                cardY + cardHeight,
                cardColor.getRGB(),
                cardColor.darker().getRGB()
        );
        guiGraphics.fill(cardX, cardY, cardX + cardWidth, cardY + 1, new Color(255, 255, 255, 24).getRGB());

        guiGraphics.drawString(font, "Crosshair preview", cardX + 8, cardY + 8, themeColor.brighter().getRGB(), true);

        final String hint = "Click preview to test hit animation";
        guiGraphics.drawString(
                font,
                hint,
                cardX + 8,
                cardY + cardHeight - 15,
                new Color(170, 170, 170).getRGB(),
                true
        );

        final int previewX = cardX + cardWidth - 58;
        final int previewY = cardY + cardHeight / 2;

        guiGraphics.fill(
                previewX - 34,
                previewY - 24,
                previewX + 34,
                previewY + 24,
                new Color(0, 0, 0, 120).getRGB()
        );
        guiGraphics.fill(previewX - 34, previewY, previewX + 35, previewY + 1, new Color(255, 255, 255, 18).getRGB());
        guiGraphics.fill(previewX, previewY - 24, previewX + 1, previewY + 25, new Color(255, 255, 255, 18).getRGB());

        module.renderPreview(guiGraphics, previewX, previewY, getPreviewAnimationAmount());
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        if (event.button() == 0 && hovered) {
            previewClickAtNanos = System.nanoTime();
        }
    }

    private float getPreviewAnimationAmount() {
        if (previewClickAtNanos == -1L) {
            return 0.0F;
        }

        final long age = System.nanoTime() - previewClickAtNanos;
        if (age >= PREVIEW_ANIMATION_NANOS) {
            return 0.0F;
        }

        final float progress = age / (float) PREVIEW_ANIMATION_NANOS;
        final float inverse = 1.0F - Math.max(0.0F, Math.min(progress, 1.0F));
        return inverse * inverse;
    }
}
