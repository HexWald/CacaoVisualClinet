package net.cacaovisualclient.mod.utils;

import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class ColorUtils {

    public Color blendColors(Color base, Color overlay) {
        final float alpha = overlay.getAlpha() / 255f;
        final int r = (int) ((overlay.getRed() * alpha) + base.getRed() * (1 - alpha));
        final int g = (int) ((overlay.getGreen() * alpha) + base.getGreen() * (1 - alpha));
        final int b = (int) ((overlay.getBlue() * alpha) + base.getBlue() * (1 - alpha));
        return new Color(r, g, b, base.getAlpha());
    }

    public Color modifyAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public Color removeAlpha(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue());
    }
}
