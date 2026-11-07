package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.ui.Window;
import net.cacaovisualclient.mod.utils.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ModMenuScreen extends Screen {

    private static final int MAX_WIDTH = 440;
    private static final int MAX_HEIGHT = 260;
    public static final int MENU_TITLE_BAR_HEIGHT = 30;

    public static final int BUTTONS_PER_ROW = 4;
    public static final int BUTTON_SPACING = 10;
    public static final int BUTTON_HEIGHT = 75;
    public static final int BUTTON_TOP_MARGIN = 15;

    public static final Color HOVER_COLOR = new Color(255, 255, 255, 30);
    public static final Color BASE_GRAY = new Color(20, 20, 20, 200);

    private int startX;
    private int startY;
    private int menuWidth;
    private int menuHeight;

    private Window currentWindow;

    public ModMenuScreen() {
        super(Component.literal("Mod Menu"));
    }

    @Override
    protected void init() {
        clearWidgets();

        menuWidth = Math.min(MAX_WIDTH, width - 16);
        menuHeight = Math.min(MAX_HEIGHT, height - 16);
        startX = (width - menuWidth) / 2;
        startY = (height - menuHeight) / 2;

        switchToModulesTab();

        final int textSpacing = 10;
        final String modsText = "Mods";
        final String themesText = "Themes";
        final String profilesText = "Profiles";
        final String aboutText = "About";

        final int totalWidth = font.width(modsText)
                + font.width(themesText)
                + font.width(profilesText)
                + font.width(aboutText)
                + 3 * textSpacing;
        final int startButtonX = startX + menuWidth - totalWidth - 14;
        final int buttonY = startY + 12;
        final int themesX = startButtonX + font.width(modsText) + textSpacing;
        final int profilesX = themesX + font.width(themesText) + textSpacing;
        final int aboutX = profilesX + font.width(profilesText) + textSpacing;

        addRenderableWidget(RenderUtils.pressableText(
                font,
                Component.literal(modsText),
                startButtonX,
                buttonY,
                this::switchToModulesTab
        ));

        addRenderableWidget(RenderUtils.pressableText(
                font,
                Component.literal(themesText),
                themesX,
                buttonY,
                () -> switchWindow(new ThemesTabWindow(this, "Themes", startX, startY + MENU_TITLE_BAR_HEIGHT)))
        );

        addRenderableWidget(RenderUtils.pressableText(
                font,
                Component.literal(profilesText),
                profilesX,
                buttonY,
                () -> switchWindow(new ProfilesTabWindow(this, "Profiles", startX, startY + MENU_TITLE_BAR_HEIGHT)))
        );

        addRenderableWidget(RenderUtils.pressableText(
                font,
                Component.literal(aboutText),
                aboutX,
                buttonY,
                () -> switchWindow(new AboutTabWindow(this, "About", startX, startY + MENU_TITLE_BAR_HEIGHT)))
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float f) {
        guiGraphics.fill(startX, startY, startX + menuWidth, startY + menuHeight, 0xE0151515);

        guiGraphics.renderItem(new ItemStack(CacaoVisualClient.getInstance().getSelectedTheme().getDisplayItem()), startX + 10, startY + 8);

        guiGraphics.drawString(
                font,
                "Cacao",
                startX + 34,
                startY + 13,
                -1,
                true
        );

        guiGraphics.fill(
                startX + 6,
                startY + MENU_TITLE_BAR_HEIGHT - 1,
                startX + menuWidth - 6,
                startY + MENU_TITLE_BAR_HEIGHT,
                new Color(255, 255, 255, 20).getRGB()
        );

        currentWindow.render(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, f);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean b) {
        currentWindow.mouseClicked(event);
        return super.mouseClicked(event, b);
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        currentWindow.mouseReleased(event);
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (x >= currentWindow.getX() && x < currentWindow.getX() + currentWindow.getWidth()
                && y >= currentWindow.getY() && y < currentWindow.getY() + currentWindow.getHeight()) {
            currentWindow.mouseScrolled(scrollY);
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (currentWindow != null && currentWindow.keyPressed(event)) {
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (currentWindow != null && currentWindow.charTyped(event)) {
            return true;
        }

        return super.charTyped(event);
    }

    public void switchToModulesTab() {
        switchWindow(new ModulesTabWindow(this, "Modules", startX, startY + MENU_TITLE_BAR_HEIGHT));
    }

    public void switchWindow(Window window) {
        currentWindow = window;
        window.init();
    }

    public int getMenuWidth() {
        return menuWidth;
    }

    public int getMenuHeight() {
        return menuHeight;
    }

    @Override
    public void removed() {
        CacaoVisualClient.getInstance().save();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
