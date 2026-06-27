package net.cacaovisualclient.mod.ui.modmenu;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.config.profile.Profile;
import net.cacaovisualclient.mod.config.profile.ProfileManager;
import net.cacaovisualclient.mod.ui.Widget;
import net.cacaovisualclient.mod.ui.Window;
import net.cacaovisualclient.mod.utils.ColorUtils;
import net.cacaovisualclient.mod.utils.MouseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.List;

public class ProfilesTabWindow extends Window {

    public ProfilesTabWindow(ModMenuScreen parent, String title, int x, int y) {
        super(parent, title, x, y);
    }

    @Override
    public void init() {
        widgets.clear();
        addWidget(new ProfilesPanelWidget(
                x,
                y + ModMenuScreen.BUTTON_TOP_MARGIN,
                ModMenuScreen.MENU_WIDTH - 12
        ));
        super.init();
    }

    private static class ProfilesPanelWidget extends Widget {

        private static final int PADDING = 8;
        private static final int CARD_HEIGHT = 54;
        private static final int CREATE_CARD_HEIGHT = 54;
        private static final int ROW_HEIGHT = 30;
        private static final int BUTTON_HEIGHT = 14;
        private static final int GAP = 8;

        private final ProfileManager profileManager = CacaoVisualClient.getInstance().getProfileManager();

        private EditBox nameBox;
        private String statusMessage = "";
        private long statusUntilMs = 0L;

        public ProfilesPanelWidget(int x, int y, int width) {
            super(x, y, width, 180);
        }

        @Override
        public void init() {
            final Font font = Minecraft.getInstance().font;
            nameBox = new EditBox(font, 0, 0, 10, 16, Component.literal("Profile name"));
            nameBox.setMaxLength(32);
            nameBox.setHint(Component.literal("New profile"));
            nameBox.setTextShadow(false);
            layoutField();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
            height = calculateHeight();
            super.render(guiGraphics, mouseX, mouseY, scrollOffset);
            layoutField();

            final Font font = Minecraft.getInstance().font;
            final int renderY = y - scrollOffset;
            final int contentX = x + PADDING;
            final int contentWidth = width - PADDING * 2;
            final Color themeColor = CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor();

            guiGraphics.drawString(font, "Profiles", contentX, renderY + 2, themeColor.brighter().getRGB(), true);

            if (System.currentTimeMillis() < statusUntilMs && !statusMessage.isBlank()) {
                final String status = trimToWidth(font, statusMessage, contentWidth - 80);
                guiGraphics.drawString(
                        font,
                        status,
                        contentX + contentWidth - font.width(status),
                        renderY + 2,
                        new Color(180, 255, 180).getRGB(),
                        true
                );
            }

            renderCurrentProfile(guiGraphics, font, mouseX, mouseY);
            renderCreateProfile(guiGraphics, font, mouseX, mouseY);
            renderProfileRows(guiGraphics, font, mouseX, mouseY);
        }

        @Override
        public void parentMouseClicked(MouseButtonEvent event) {
            layoutField();

            if (!isOverField(event.x(), event.y())) {
                nameBox.setFocused(false);
            }
        }

        @Override
        public void mouseClicked(MouseButtonEvent event) {
            if (event.button() != 0) {
                return;
            }

            layoutField();

            final boolean overField = isOverField(event.x(), event.y());
            nameBox.setFocused(overField);
            if (overField) {
                nameBox.mouseClicked(event, false);
                return;
            }

            final int mouseX = (int) event.x();
            final int mouseY = (int) event.y();

            if (MouseUtils.isMouseOver(mouseX, mouseY, saveCurrentButtonX(), currentCardY() + 30, 104, BUTTON_HEIGHT)) {
                saveCurrentProfile();
                return;
            }

            if (MouseUtils.isMouseOver(mouseX, mouseY, createButtonX(), createCardY() + 29, 72, BUTTON_HEIGHT)) {
                createProfile();
                return;
            }

            handleRowClick(mouseX, mouseY);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (!nameBox.isFocused()) {
                return false;
            }

            if (event.isEscape()) {
                nameBox.setFocused(false);
                return true;
            }

            if (event.isConfirmation()) {
                createProfile();
                return true;
            }

            return nameBox.keyPressed(event);
        }

        @Override
        public boolean charTyped(CharacterEvent event) {
            return nameBox.isFocused() && nameBox.charTyped(event);
        }

        private void renderCurrentProfile(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
            final int cardX = contentX();
            final int cardY = currentCardY();
            final int cardWidth = contentWidth();
            final String activeName = getActiveProfileName();

            drawCard(guiGraphics, cardX, cardY, cardWidth, CARD_HEIGHT);
            guiGraphics.drawString(font, "Current profile", cardX + 10, cardY + 8, new Color(180, 180, 180).getRGB(), true);
            guiGraphics.drawString(font, activeName, cardX + 10, cardY + 25, -1, true);
            drawButton(guiGraphics, font, "Save current", saveCurrentButtonX(), cardY + 30, 104, BUTTON_HEIGHT, true, false, mouseX, mouseY);
        }

        private void renderCreateProfile(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
            final int cardX = contentX();
            final int cardY = createCardY();
            final int cardWidth = contentWidth();

            drawCard(guiGraphics, cardX, cardY, cardWidth, CREATE_CARD_HEIGHT);
            guiGraphics.drawString(font, "Create from current settings", cardX + 10, cardY + 8, new Color(180, 180, 180).getRGB(), true);
            nameBox.renderWidget(guiGraphics, mouseX, mouseY, 0);
            drawButton(guiGraphics, font, "Create", createButtonX(), cardY + 29, 72, BUTTON_HEIGHT, true, false, mouseX, mouseY);
        }

        private void renderProfileRows(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
            final List<Profile> profiles = profileManager.getProfiles();
            final int rowsY = rowsStartY();

            guiGraphics.drawString(font, "Saved profiles", contentX(), rowsY - 14, new Color(230, 230, 230).getRGB(), true);

            if (profiles.isEmpty()) {
                drawCard(guiGraphics, contentX(), rowsY, contentWidth(), ROW_HEIGHT);
                guiGraphics.drawString(font, "No profiles found.", contentX() + 8, rowsY + 10, new Color(170, 170, 170).getRGB(), true);
                return;
            }

            for (int i = 0; i < profiles.size(); i++) {
                final Profile profile = profiles.get(i);
                final int rowY = rowsY + i * ROW_HEIGHT;
                final boolean active = profile.getName().equals(getActiveProfileName());
                final int rowColor = active
                        ? ColorUtils.blendColors(new Color(30, 30, 30, 220), ColorUtils.modifyAlpha(CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor(), 85)).getRGB()
                        : new Color(25, 25, 25, 190).getRGB();

                guiGraphics.fill(contentX(), rowY, contentX() + contentWidth(), rowY + ROW_HEIGHT - 2, rowColor);
                guiGraphics.drawString(font, trimToWidth(font, profile.getName(), contentWidth() - 224), contentX() + 8, rowY + 10, -1, true);

                if (active) {
                    guiGraphics.drawString(font, "Active", rowLoadX() - 36, rowY + 10, CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor().brighter().getRGB(), true);
                }

                drawButton(guiGraphics, font, "Load", rowLoadX(), rowY + 7, 38, BUTTON_HEIGHT, !active, false, mouseX, mouseY);
                drawButton(guiGraphics, font, "Save", rowSaveX(), rowY + 7, 38, BUTTON_HEIGHT, true, false, mouseX, mouseY);
                drawButton(guiGraphics, font, "Dup", rowDuplicateX(), rowY + 7, 32, BUTTON_HEIGHT, true, false, mouseX, mouseY);
                drawButton(guiGraphics, font, "Ren", rowRenameX(), rowY + 7, 32, BUTTON_HEIGHT, true, false, mouseX, mouseY);
                drawButton(guiGraphics, font, "Del", rowDeleteX(), rowY + 7, 32, BUTTON_HEIGHT, true, true, mouseX, mouseY);
            }
        }

        private void handleRowClick(int mouseX, int mouseY) {
            final List<Profile> profiles = profileManager.getProfiles();
            final int rowsY = rowsStartY();

            for (int i = 0; i < profiles.size(); i++) {
                final Profile profile = profiles.get(i);
                final int rowY = rowsY + i * ROW_HEIGHT;

                if (MouseUtils.isMouseOver(mouseX, mouseY, rowLoadX(), rowY + 7, 38, BUTTON_HEIGHT)) {
                    loadProfile(profile);
                    return;
                }

                if (MouseUtils.isMouseOver(mouseX, mouseY, rowSaveX(), rowY + 7, 38, BUTTON_HEIGHT)) {
                    profileManager.saveProfile(profile.getName());
                    CacaoVisualClient.getInstance().save();
                    setStatus("Saved " + profile.getName());
                    return;
                }

                if (MouseUtils.isMouseOver(mouseX, mouseY, rowDuplicateX(), rowY + 7, 32, BUTTON_HEIGHT)) {
                    duplicateProfile(profile);
                    return;
                }

                if (MouseUtils.isMouseOver(mouseX, mouseY, rowRenameX(), rowY + 7, 32, BUTTON_HEIGHT)) {
                    renameProfile(profile);
                    return;
                }

                if (MouseUtils.isMouseOver(mouseX, mouseY, rowDeleteX(), rowY + 7, 32, BUTTON_HEIGHT)) {
                    deleteProfile(profile);
                    return;
                }
            }
        }

        private void saveCurrentProfile() {
            profileManager.saveCurrentProfile();
            CacaoVisualClient.getInstance().save();
            setStatus("Saved " + getActiveProfileName());
        }

        private void createProfile() {
            final String name = nameBox.getValue().trim();
            if (name.isBlank()) {
                setStatus("Enter profile name");
                return;
            }

            if (hasUnsafeFileChars(name)) {
                setStatus("Name has unsafe characters");
                return;
            }

            if (!profileManager.createProfile(name)) {
                setStatus("Profile already exists");
                return;
            }

            CacaoVisualClient.getInstance().save();
            nameBox.setValue("");
            nameBox.setFocused(false);
            setStatus("Created " + name);
        }

        private void loadProfile(Profile profile) {
            if (!profileManager.load(profile.getName())) {
                setStatus("Failed to load " + profile.getName());
                return;
            }

            CacaoVisualClient.getInstance().save();
            setStatus("Loaded " + profile.getName());
        }

        private void duplicateProfile(Profile profile) {
            final String targetName = nameBox.getValue().trim();
            if (!isUsableTargetName(targetName)) {
                return;
            }

            if (!profileManager.duplicateProfile(profile.getName(), targetName)) {
                setStatus("Could not duplicate profile");
                return;
            }

            CacaoVisualClient.getInstance().save();
            nameBox.setValue("");
            setStatus("Duplicated " + profile.getName());
        }

        private void renameProfile(Profile profile) {
            final String targetName = nameBox.getValue().trim();
            if (!isUsableTargetName(targetName)) {
                return;
            }

            if (!profileManager.renameProfile(profile.getName(), targetName)) {
                setStatus("Could not rename profile");
                return;
            }

            CacaoVisualClient.getInstance().save();
            nameBox.setValue("");
            setStatus("Renamed " + profile.getName());
        }

        private void deleteProfile(Profile profile) {
            if (!profileManager.deleteProfile(profile.getName())) {
                setStatus("Could not delete profile");
                return;
            }

            CacaoVisualClient.getInstance().save();
            setStatus("Deleted " + profile.getName());
        }

        private boolean isUsableTargetName(String name) {
            if (name.isBlank()) {
                setStatus("Enter target name first");
                return false;
            }

            if (hasUnsafeFileChars(name)) {
                setStatus("Name has unsafe characters");
                return false;
            }

            return true;
        }

        private boolean hasUnsafeFileChars(String name) {
            return name.indexOf('\\') >= 0
                    || name.indexOf('/') >= 0
                    || name.indexOf(':') >= 0
                    || name.indexOf('*') >= 0
                    || name.indexOf('?') >= 0
                    || name.indexOf('"') >= 0
                    || name.indexOf('<') >= 0
                    || name.indexOf('>') >= 0
                    || name.indexOf('|') >= 0;
        }

        private String getActiveProfileName() {
            final Profile current = profileManager.getCurrentProfile();
            return current == null ? "None" : current.getName();
        }

        private void layoutField() {
            if (nameBox == null) {
                return;
            }

            nameBox.setX(contentX() + 10);
            nameBox.setY(createCardY() + 28);
            nameBox.setWidth(245);
        }

        private boolean isOverField(double mouseX, double mouseY) {
            return MouseUtils.isMouseOver(mouseX, mouseY, nameBox.getX(), nameBox.getY(), nameBox.getWidth(), nameBox.getHeight());
        }

        private void drawCard(GuiGraphics guiGraphics, int cardX, int cardY, int cardWidth, int cardHeight) {
            final Color base = new Color(18, 18, 18, 220);
            guiGraphics.fillGradient(cardX, cardY, cardX + cardWidth, cardY + cardHeight, base.getRGB(), base.darker().getRGB());
            guiGraphics.fill(cardX, cardY, cardX + cardWidth, cardY + 1, new Color(255, 255, 255, 22).getRGB());
        }

        private void drawButton(
                GuiGraphics guiGraphics,
                Font font,
                String text,
                int buttonX,
                int buttonY,
                int buttonWidth,
                int buttonHeight,
                boolean active,
                boolean danger,
                int mouseX,
                int mouseY
        ) {
            final Color themeColor = CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor();
            Color color = danger
                    ? new Color(145, 55, 55, 220)
                    : active
                    ? ColorUtils.modifyAlpha(themeColor, 210)
                    : ModMenuScreen.BASE_GRAY.brighter();

            if (MouseUtils.isMouseOver(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                color = ColorUtils.blendColors(color, ModMenuScreen.HOVER_COLOR);
            }

            guiGraphics.fillGradient(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, color.getRGB(), color.darker().getRGB());
            guiGraphics.drawString(font, text, buttonX + buttonWidth / 2 - font.width(text) / 2, buttonY + 3, -1, true);
        }

        private void setStatus(String message) {
            statusMessage = message;
            statusUntilMs = System.currentTimeMillis() + 2600L;
        }

        private String trimToWidth(Font font, String text, int maxWidth) {
            if (font.width(text) <= maxWidth) {
                return text;
            }

            String trimmed = text;
            while (!trimmed.isEmpty() && font.width(trimmed + "...") > maxWidth) {
                trimmed = trimmed.substring(0, trimmed.length() - 1);
            }
            return trimmed + "...";
        }

        private int calculateHeight() {
            return 16
                    + CARD_HEIGHT
                    + GAP
                    + CREATE_CARD_HEIGHT
                    + GAP
                    + 16
                    + Math.max(1, profileManager.getProfiles().size()) * ROW_HEIGHT
                    + 10;
        }

        private int contentX() {
            return x + PADDING;
        }

        private int contentWidth() {
            return width - PADDING * 2;
        }

        private int renderY() {
            return y - (parent == null ? 0 : parent.getScrollOffset());
        }

        private int currentCardY() {
            return renderY() + 16;
        }

        private int createCardY() {
            return currentCardY() + CARD_HEIGHT + GAP;
        }

        private int rowsStartY() {
            return createCardY() + CREATE_CARD_HEIGHT + GAP + 16;
        }

        private int saveCurrentButtonX() {
            return contentX() + contentWidth() - 114;
        }

        private int createButtonX() {
            return contentX() + contentWidth() - 82;
        }

        private int rowDeleteX() {
            return contentX() + contentWidth() - 38;
        }

        private int rowRenameX() {
            return rowDeleteX() - 36;
        }

        private int rowDuplicateX() {
            return rowRenameX() - 36;
        }

        private int rowSaveX() {
            return rowDuplicateX() - 42;
        }

        private int rowLoadX() {
            return rowSaveX() - 42;
        }
    }
}
