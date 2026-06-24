package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.theme.Theme;
import net.cacaovisualclient.mod.ui.Window;

import java.util.Arrays;

public class ThemesTabWindow extends Window {

    public ThemesTabWindow(ModMenuScreen parent, String name, int x, int y) {
        super(parent, name, x, y);
    }

    @Override
    public void init() {
        super.init();

        GridUtil.layoutGrid(
                Arrays.stream(Theme.values()).toList(),
                x,
                y,
                (theme, pos) -> {
                    addWidget(new ThemeButtonWidget(
                            theme,
                            pos.getX(), pos.getY(),
                            pos.getWidth(), pos.getHeight()
                    ));
                }
        );
    }
}
