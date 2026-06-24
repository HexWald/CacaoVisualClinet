package net.cacaovisualclient.mod.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MouseUtils {

    public static boolean isMouseOver(double mouseX, double mouseY, float x, float y, float width, float height) {
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }
}
