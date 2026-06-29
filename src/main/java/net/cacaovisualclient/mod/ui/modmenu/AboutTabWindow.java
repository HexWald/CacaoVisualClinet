package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.config.profile.Profile;
import net.cacaovisualclient.mod.ui.Window;
import net.cacaovisualclient.mod.utils.ColorUtils;
import net.cacaovisualclient.mod.utils.MouseUtils;
import net.cacaovisualclient.mod.utils.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class AboutTabWindow extends Window {

    private static final String GITHUB_URL = "https://github.com/HexWald/CacaoVisualClinet";
    private static final int CARD_X_PADDING = 12;
    private static final int CARD_Y = 18;
    private static final int CARD_HEIGHT = 126;
    private static final int ROW_GAP = 14;

    public AboutTabWindow(ModMenuScreen parent, String name, int x, int y) {
        super(parent, name, x, y);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);

        final Minecraft mc = Minecraft.getInstance();
        final Font font = mc.font;
        final CacaoVisualClient client = CacaoVisualClient.getInstance();
        final Color themeColor = client.getSelectedTheme().getPrimaryColor();

        final int cardX = x + CARD_X_PADDING;
        final int cardY = y + CARD_Y;
        final int cardWidth = width - CARD_X_PADDING * 2;
        final int cardBottom = cardY + CARD_HEIGHT;

        guiGraphics.fillGradient(
                cardX,
                cardY,
                cardX + cardWidth,
                cardBottom,
                new Color(18, 18, 18, 225).getRGB(),
                new Color(10, 10, 10, 225).getRGB()
        );
        guiGraphics.fill(cardX, cardY, cardX + cardWidth, cardY + 1, ColorUtils.modifyAlpha(themeColor.brighter(), 120).getRGB());

        guiGraphics.drawString(font, "CacaoVisualClient", cardX + 12, cardY + 10, themeColor.brighter().getRGB(), true);
        guiGraphics.drawString(font, "Small PvP client polish, warm cacao vibes.", cardX + 12, cardY + 24, new Color(190, 190, 190).getRGB(), true);

        int rowY = cardY + 48;
        drawInfoRow(guiGraphics, font, "Version", CacaoVisualClient.MOD_VERSION, cardX + 12, rowY);
        rowY += ROW_GAP;
        drawInfoRow(guiGraphics, font, "Theme", client.getSelectedTheme().getName(), cardX + 12, rowY);
        rowY += ROW_GAP;
        drawInfoRow(guiGraphics, font, "Profile", getProfileName(client), cardX + 12, rowY);
        rowY += ROW_GAP;
        drawInfoRow(guiGraphics, font, "Modules", getModuleSummary(client), cardX + 12, rowY);

        final int buttonX = cardX + cardWidth - 126;
        final int buttonY = cardBottom - 28;
        final int buttonWidth = 108;
        final int buttonHeight = 16;
        final boolean hoverGithub = MouseUtils.isMouseOver(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);
        Color buttonColor = ColorUtils.modifyAlpha(themeColor, 205);

        if (hoverGithub) {
            buttonColor = ColorUtils.blendColors(buttonColor, ModMenuScreen.HOVER_COLOR);
        }

        guiGraphics.fillGradient(
                buttonX,
                buttonY,
                buttonX + buttonWidth,
                buttonY + buttonHeight,
                buttonColor.getRGB(),
                buttonColor.darker().getRGB()
        );

        final String buttonText = "Copy GitHub";
        guiGraphics.drawString(
                font,
                buttonText,
                buttonX + buttonWidth / 2 - font.width(buttonText) / 2,
                buttonY + 4,
                -1,
                true
        );

        guiGraphics.drawString(
                font,
                GITHUB_URL,
                cardX + 12,
                cardBottom - 24,
                new Color(160, 160, 160).getRGB(),
                true
        );
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        final int cardX = x + CARD_X_PADDING;
        final int cardWidth = width - CARD_X_PADDING * 2;
        final int buttonX = cardX + cardWidth - 126;
        final int buttonY = y + CARD_Y + CARD_HEIGHT - 28;

        if (event.button() == 0 && MouseUtils.isMouseOver(event.x(), event.y(), buttonX, buttonY, 108, 16)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(GITHUB_URL);
            Notification.sendNotification("About", "GitHub link copied");
            return;
        }

        super.mouseClicked(event);
    }

    private void drawInfoRow(GuiGraphics guiGraphics, Font font, String label, String value, int x, int y) {
        guiGraphics.drawString(font, label + ":", x, y, new Color(150, 150, 150).getRGB(), true);
        guiGraphics.drawString(font, value, x + 64, y, Color.WHITE.getRGB(), true);
    }

    private String getProfileName(CacaoVisualClient client) {
        final Profile profile = client.getProfileManager().getCurrentProfile();
        return profile == null ? "None" : profile.getName();
    }

    private String getModuleSummary(CacaoVisualClient client) {
        final long enabled = client.getModuleManager().getModules().stream()
                .filter(net.cacaovisualclient.mod.module.Module::isEnabled)
                .count();
        final int total = client.getModuleManager().getModules().size();
        return enabled + "/" + total + " enabled";
    }
}
