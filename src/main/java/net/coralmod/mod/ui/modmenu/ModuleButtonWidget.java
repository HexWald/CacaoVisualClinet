package net.coralmod.mod.ui.modmenu;

import net.coralmod.mod.CoralMod;
import net.coralmod.mod.module.Module;
import net.coralmod.mod.theme.Theme;
import net.coralmod.mod.ui.Widget;
import net.coralmod.mod.utils.ColorUtils;
import net.coralmod.mod.utils.MouseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class ModuleButtonWidget extends Widget {

    private final Module module;
    private static final int BORDER_THICKNESS = 2;
    private static final int BUTTON_HEIGHT = 15;
    private static final int BUTTON_PADDING = 6;

    public ModuleButtonWidget(Module module, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.module = module;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
        super.render(guiGraphics, mouseX, mouseY, scrollOffset);

        final int renderY = y - scrollOffset;

        final Theme theme = CoralMod.getInstance().getSelectedTheme();

        final Color baseGray = ModMenuScreen.BASE_GRAY;
        int backgroundColor = baseGray.getRGB();
        int borderColor = baseGray.brighter().getRGB();

        if (module.isEnabled()) {
            final Color themeColor = ColorUtils.setAlpha(theme.getPrimaryColor(), 100);

            borderColor = theme.getPrimaryColor().getRGB();
            backgroundColor = ColorUtils.blendColors(baseGray, themeColor).getRGB();
        }

        if (hovered) {
            backgroundColor = ColorUtils.blendColors(new Color(backgroundColor, true), ModMenuScreen.HOVER_COLOR).getRGB();
            borderColor = ColorUtils.blendColors(new Color(borderColor, true), ModMenuScreen.HOVER_COLOR).getRGB();
        }

        guiGraphics.fill(x, renderY, x + width, renderY + height, borderColor);
        guiGraphics.fill(x + BORDER_THICKNESS, renderY + BORDER_THICKNESS, x + width - BORDER_THICKNESS, renderY + height - BORDER_THICKNESS, backgroundColor);

        final Font font = Minecraft.getInstance().font;

        final int toggleY = renderY + height - BUTTON_HEIGHT * 2 - 10;
        final int settingY = renderY + height - BUTTON_HEIGHT - 5;

        final boolean isHoveringToggle = MouseUtils.isMouseOver(mouseX, mouseY, x, toggleY, width, BUTTON_HEIGHT);
        final boolean isHoveringSettings = MouseUtils.isMouseOver(mouseX, mouseY, x, settingY, width, BUTTON_HEIGHT);

        final Color buttonBackgroundColor = ColorUtils.removeAlpha(baseGray).brighter().brighter();
        final Color buttonHoverColor = buttonBackgroundColor.brighter().brighter();

        final Color toggleButtonColor = isHoveringToggle ? buttonHoverColor : buttonBackgroundColor;
        final Color settingsButtonColor = isHoveringSettings ? buttonHoverColor : buttonBackgroundColor;

        guiGraphics.fillGradient(x + BUTTON_PADDING, toggleY, x + width - BUTTON_PADDING, toggleY + BUTTON_HEIGHT, toggleButtonColor.getRGB(), toggleButtonColor.darker().getRGB());
        guiGraphics.fillGradient(x + BUTTON_PADDING, settingY, x + width - BUTTON_PADDING, settingY + BUTTON_HEIGHT, settingsButtonColor.getRGB(), settingsButtonColor.darker().getRGB());

        final String toggleText = module.isEnabled() ? "Disable" : "Enable";
        final String settingsText = "Settings";

        guiGraphics.drawString(font, toggleText,
                x + width / 2 - font.width(toggleText) / 2, toggleY + 4, -1, true);

        guiGraphics.drawString(font, settingsText,
                x + width / 2 - font.width(settingsText) / 2, settingY + 4, -1, true);

        final int textX = x + width / 2 - font.width(module.getName()) / 2;
        final int textY = renderY + BORDER_THICKNESS + (toggleY - (renderY + BORDER_THICKNESS) - font.lineHeight) / 2;
        guiGraphics.drawString(font, module.getName(), textX, textY, -1, true);
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        int mouseX = (int) event.x();
        int mouseY = (int) event.y();

        int renderY = y - parent.getScrollOffset();

        int toggleY = renderY + height - BUTTON_HEIGHT * 2 - 10;
        int settingY = renderY + height - BUTTON_HEIGHT - 5;

        if (MouseUtils.isMouseOver(mouseX, mouseY, x, toggleY, width, BUTTON_HEIGHT)) {
            module.setEnabled(!module.isEnabled());
            return;
        }

        if (MouseUtils.isMouseOver(mouseX, mouseY, x, settingY, width, BUTTON_HEIGHT)) {
            parent.getParent().switchWindow(
                    new ModuleSettingsWindow(
                            parent.getParent(),
                            module,
                            "Settings",
                            parent.getX(),
                            parent.getY()
                    )
            );
        }
    }
}
