package net.cacaovisualclient.mod.ui.toast;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.Color;

public class CacaoToast {

    private static final long ENTER_NANOS = 240_000_000L;
    private static final long EXIT_NANOS = 300_000_000L;
    private static final long DEFAULT_VISIBLE_NANOS = 3_400_000_000L;

    private static final int WIDTH = 190;
    private static final int HEIGHT = 42;

    private final String title;
    private final String message;
    private final CacaoToastType type;
    private final long createdAtNanos;
    private final long visibleNanos;

    public CacaoToast(String title, String message, CacaoToastType type) {
        this(title, message, type, DEFAULT_VISIBLE_NANOS);
    }

    public CacaoToast(String title, String message, CacaoToastType type, long visibleNanos) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.visibleNanos = visibleNanos;
        this.createdAtNanos = System.nanoTime();
    }

    public boolean isExpired(long nowNanos) {
        return nowNanos - createdAtNanos > visibleNanos + EXIT_NANOS;
    }

    public void render(GuiGraphics guiGraphics, Font font, int screenWidth, int index, long nowNanos) {
        final long age = nowNanos - createdAtNanos;
        final float opacity = getOpacity(age);
        final float slide = easeOutCubic(clamp01(Math.min(age, ENTER_NANOS) / (float) ENTER_NANOS));

        final int x = screenWidth - 10 - WIDTH + Math.round((1.0F - slide) * (WIDTH + 16));
        final int y = 24 + index * (HEIGHT + 8);

        final Color accent = type.getColor();
        final int backgroundStart = argb((int) (178 * opacity), 20, 20, 24);
        final int backgroundEnd = argb((int) (164 * opacity), 10, 10, 14);

        guiGraphics.fillGradient(x, y, x + WIDTH, y + HEIGHT, backgroundStart, backgroundEnd);
        guiGraphics.fill(x, y, x + 3, y + HEIGHT, argb((int) (235 * opacity), accent));

        final int titleColor = argb((int) (255 * opacity), 255, 255, 255);
        final int messageColor = argb((int) (205 * opacity), 216, 218, 225);

        guiGraphics.drawString(font, trimToWidth(font, title, WIDTH - 18), x + 10, y + 7, titleColor, false);
        guiGraphics.drawString(font, trimToWidth(font, message, WIDTH - 18), x + 10, y + 22, messageColor, false);

        final float lifetime = clamp01(age / (float) visibleNanos);
        final int progressWidth = Math.round((WIDTH - 3) * (1.0F - lifetime));
        guiGraphics.fill(
                x + 3,
                y + HEIGHT - 2,
                x + 3 + progressWidth,
                y + HEIGHT,
                argb((int) (190 * opacity), accent)
        );
    }

    private float getOpacity(long ageNanos) {
        if (ageNanos <= ENTER_NANOS) {
            return easeOutCubic(ageNanos / (float) ENTER_NANOS);
        }

        if (ageNanos <= visibleNanos) {
            return 1.0F;
        }

        return 1.0F - easeOutCubic(clamp01((ageNanos - visibleNanos) / (float) EXIT_NANOS));
    }

    private static String trimToWidth(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return text;
        }

        String trimmed = text;
        while (trimmed.length() > 3 && font.width(trimmed + "...") > maxWidth) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }

        return trimmed + "...";
    }

    private static int argb(int alpha, Color color) {
        return argb(alpha, color.getRed(), color.getGreen(), color.getBlue());
    }

    private static int argb(int alpha, int red, int green, int blue) {
        final int safeAlpha = Math.max(0, Math.min(alpha, 255));
        return safeAlpha << 24 | red << 16 | green << 8 | blue;
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(value, 1.0F));
    }

    private static float easeOutCubic(float value) {
        final float inverted = 1.0F - value;
        return 1.0F - inverted * inverted * inverted;
    }
}
