package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.ui.Position;

import java.util.function.BiConsumer;

public class GridUtil {

    public static <T> void layoutGrid(
            Iterable<T> elements,
            int x,
            int y,
            BiConsumer<T, Position> consumer
    ) {
        final int totalSpacing = (ModMenuScreen.BUTTONS_PER_ROW - 1) * ModMenuScreen.BUTTON_SPACING;
        final int buttonWidth = (ModMenuScreen.MENU_WIDTH - 2 * ModMenuScreen.BUTTON_SPACING - totalSpacing) / ModMenuScreen.BUTTONS_PER_ROW;
        final int totalButtonsWidth = ModMenuScreen.BUTTONS_PER_ROW * buttonWidth + totalSpacing;
        final int startX = x + (ModMenuScreen.MENU_WIDTH - totalButtonsWidth) / 2;
        int buttonX = startX;
        int buttonY = y + ModMenuScreen.BUTTON_TOP_MARGIN;

        for (T element : elements) {
            consumer.accept(element, new Position(buttonX, buttonY, buttonWidth, ModMenuScreen.BUTTON_HEIGHT));

            buttonX += buttonWidth + ModMenuScreen.BUTTON_SPACING;

            if (buttonX + buttonWidth > startX + totalButtonsWidth) {
                buttonX = startX;
                buttonY += ModMenuScreen.BUTTON_HEIGHT + ModMenuScreen.BUTTON_SPACING;
            }
        }
    }

}


