package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.ui.Window;
import net.cacaovisualclient.mod.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ModMenuScreen extends Screen {

    private final Minecraft mc = Minecraft.getInstance();

    public static final int MENU_WIDTH = 400;
    public static int MENU_HEIGHT = 200;
    public static final int MENU_TITLE_BAR_HEIGHT = 20;

    public static final int BUTTONS_PER_ROW = 4;
    public static final int BUTTON_SPACING = 10;
    public static final int BUTTON_HEIGHT = 75;
    public static final int BUTTON_TOP_MARGIN = 15;

    public static final Color HOVER_COLOR = new Color(255, 255, 255, 30);
    public static final Color BASE_GRAY = new Color(20, 20, 20, 200);

    private int startX;
    private int startY;

    private Window currentWindow;

    public ModMenuScreen() {
        super(Component.literal("Mod Menu"));
    }

    @Override
    protected void init() {
        startX = (mc.screen.width - MENU_WIDTH) / 2;
        startY = (mc.screen.height - MENU_HEIGHT) / 2;

        switchToModulesTab();

        final int textSpacing = 10;
        final String modsText = "Mods";
        final String themesText = "Themes";
        final String profilesText = "Profiles";

        final int totalWidth = font.width(modsText) + font.width(themesText) + font.width(profilesText) + 2 * textSpacing;
        final int startButtonX = startX + (MENU_WIDTH - totalWidth) / 2;
        final int buttonY = startY + 15;

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
                startButtonX + font.width(modsText) + textSpacing,
                buttonY,
                () -> switchWindow(new ThemesTabWindow(this, "Themes", startX, startY + MENU_TITLE_BAR_HEIGHT)))
        );

        addRenderableWidget(RenderUtils.pressableText(
                font,
                Component.literal(profilesText),
                startButtonX + font.width(modsText) + textSpacing + font.width(themesText) + textSpacing,
                buttonY,
                () -> System.out.println("Profiles clicked"))
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float f) {
        guiGraphics.fill(startX, startY, startX + MENU_WIDTH, startY + MENU_HEIGHT, new Color(0, 0, 0, 160).getRGB());

        guiGraphics.renderItem(new ItemStack(CacaoVisualClient.getInstance().getSelectedTheme().getDisplayItem()), startX + 10, startY + 10);

        RenderUtils.scaledText(
                guiGraphics.pose(),
                guiGraphics,
                "CacaoVisualClient",
                startX + mc.font.width("CacaoVisualClient") - 10,
                startY + 15,
                1.35f,
                -1,
                true
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
        currentWindow.mouseScrolled(scrollY);
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    public void switchToModulesTab() {
        switchWindow(new ModulesTabWindow(this, "Modules", startX, startY + MENU_TITLE_BAR_HEIGHT));
    }

    public void switchWindow(Window window) {
        currentWindow = window;
        window.init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
