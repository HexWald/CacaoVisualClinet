package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.module.settings.ModeSetting;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import net.cacaovisualclient.mod.module.settings.Setting;
import net.cacaovisualclient.mod.module.modules.AutoGGModule;
import net.cacaovisualclient.mod.module.modules.CustomCrosshairModule;
import net.cacaovisualclient.mod.ui.Widget;
import net.cacaovisualclient.mod.ui.Window;
import net.cacaovisualclient.mod.ui.modmenu.autogg.AutoGGPanelWidget;
import net.cacaovisualclient.mod.ui.modmenu.crosshair.CrosshairPreviewWidget;
import net.cacaovisualclient.mod.ui.modmenu.setting.BooleanSettingWidget;
import net.cacaovisualclient.mod.ui.modmenu.setting.ModeSettingWidget;
import net.cacaovisualclient.mod.ui.modmenu.setting.NumberSettingWidget;
import net.cacaovisualclient.mod.utils.ColorUtils;
import net.cacaovisualclient.mod.utils.MouseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class ModuleSettingsWindow extends Window {

    private static final int TOP_OFFSET = 14;
    private static final int TOP_BUTTON_HEIGHT = 14;
    private static final int TOP_BUTTON_WIDTH = 48;
    private static final int CONTENT_RIGHT_PADDING = 12;

    private final Module module;

    public ModuleSettingsWindow(ModMenuScreen parent, Module module, String name, int x, int y) {
        super(parent, name, x, y);
        this.module = module;
    }

    @Override
    public void init() {
        widgets.clear();

        int currentWidgetY = y + TOP_OFFSET + TOP_BUTTON_HEIGHT + 8;

        final int widgetHeight = 15;

        for (Setting<?> setting : module.getSettings()) {
            Widget settingWidget = null;

            if (setting instanceof BooleanSetting booleanSetting) {
                settingWidget = new BooleanSettingWidget(
                        booleanSetting,
                        x,
                        currentWidgetY,
                        ModMenuScreen.MENU_WIDTH - CONTENT_RIGHT_PADDING,
                        widgetHeight
                );
            } else if (setting instanceof ModeSetting modeSetting) {
                settingWidget = new ModeSettingWidget(
                        modeSetting,
                        x,
                        currentWidgetY,
                        ModMenuScreen.MENU_WIDTH - CONTENT_RIGHT_PADDING,
                        widgetHeight
                );
            } else if (setting instanceof NumberSetting numberSetting) {
                settingWidget = new NumberSettingWidget(
                        numberSetting,
                        x,
                        currentWidgetY,
                        ModMenuScreen.MENU_WIDTH - CONTENT_RIGHT_PADDING,
                        widgetHeight
                );
            }

            if (settingWidget != null) {
                addWidget(settingWidget);
                currentWidgetY += widgetHeight;
            }
        }

        if (module instanceof AutoGGModule autoGGModule) {
            currentWidgetY += 8;
            addWidget(new AutoGGPanelWidget(
                    autoGGModule,
                    x,
                    currentWidgetY,
                    ModMenuScreen.MENU_WIDTH - CONTENT_RIGHT_PADDING
            ));
        }

        if (module instanceof CustomCrosshairModule customCrosshairModule) {
            currentWidgetY += 8;
            addWidget(new CrosshairPreviewWidget(
                    customCrosshairModule,
                    x,
                    currentWidgetY,
                    ModMenuScreen.MENU_WIDTH - CONTENT_RIGHT_PADDING
            ));
        }

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);

        final Font font = Minecraft.getInstance().font;


        if (module.getSettings().isEmpty()) {
            guiGraphics.drawString(
                    font,
                    "This module has no settings",
                    x + ModMenuScreen.MENU_WIDTH / 2 - font.width("This module has no settings") / 2,
                    getParent().height / 2,
                    new Color(255, 255, 255, 100).getRGB(),
                    true
            );
        }

        final int buttonY = y + TOP_OFFSET;

        final int backX = x + 5;
        final int resetX = backX + 5 + TOP_BUTTON_WIDTH;

        final boolean hoverBack = MouseUtils.isMouseOver(
                mouseX, mouseY, backX, buttonY, TOP_BUTTON_WIDTH, TOP_BUTTON_HEIGHT
        );

        final boolean hoverReset = MouseUtils.isMouseOver(
                mouseX, mouseY, resetX, buttonY, TOP_BUTTON_WIDTH, TOP_BUTTON_HEIGHT
        );

        Color backColor = ModMenuScreen.BASE_GRAY;
        if (hoverBack) {
            backColor = ColorUtils.blendColors(new Color(backColor.getRGB(), true), ModMenuScreen.HOVER_COLOR);
        }

        Color resetColor = new Color(160, 70, 70, 200);
        if (hoverReset) {
            resetColor = ColorUtils.blendColors(new Color(resetColor.getRGB(), true), ModMenuScreen.HOVER_COLOR);
        }

        guiGraphics.fillGradient(
                backX,
                buttonY,
                backX + TOP_BUTTON_WIDTH,
                buttonY + TOP_BUTTON_HEIGHT,
                backColor.getRGB(),
                backColor.darker().getRGB()
        );

        if (!module.getSettings().isEmpty()) {
            guiGraphics.fillGradient(
                    resetX,
                    buttonY,
                    resetX + TOP_BUTTON_WIDTH,
                    buttonY + TOP_BUTTON_HEIGHT,
                    resetColor.getRGB(),
                    resetColor.darker().getRGB()
            );
        }

        final String backText = "< Back";
        guiGraphics.drawString(
                font,
                backText,
                backX + TOP_BUTTON_WIDTH / 2 - font.width(backText) / 2,
                buttonY + 3,
                -1,
                true
        );

        if (!module.getSettings().isEmpty()) {
            guiGraphics.drawString(
                    font,
                    "Reset",
                    resetX + TOP_BUTTON_WIDTH / 2 - font.width("Reset") / 2,
                    buttonY + 3,
                    -1,
                    true
            );
        }

        final int dividerY = buttonY + TOP_BUTTON_HEIGHT + 4;
        guiGraphics.fill(
                x + 6,
                dividerY,
                x + ModMenuScreen.MENU_WIDTH - 6,
                dividerY + 1,
                new Color(255, 255, 255, 20).getRGB()
        );

    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        super.mouseClicked(event);

        final int buttonY = y + TOP_OFFSET;

        final int backX = x + 5;
        final int resetX = backX + 5 + TOP_BUTTON_WIDTH;

        final boolean hoverBack = MouseUtils.isMouseOver(
                event.x(), event.y(), backX, buttonY, TOP_BUTTON_WIDTH, TOP_BUTTON_HEIGHT
        );

        final boolean hoverReset = MouseUtils.isMouseOver(
                event.x(), event.y(), resetX, buttonY, TOP_BUTTON_WIDTH, TOP_BUTTON_HEIGHT
        );

        if (hoverBack) {
            parent.switchToModulesTab();
        }

        if (hoverReset) {
            module.reset();
        }
    }

    @Override
    protected int getContentTop() {
        return y + TOP_OFFSET + TOP_BUTTON_HEIGHT + 5;
    }
}
