package net.cacaovisualclient.mod.ui.modmenu.autogg;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.feature.autogg.AutoGGConfig;
import net.cacaovisualclient.mod.feature.autogg.AutoGGMatchMode;
import net.cacaovisualclient.mod.feature.autogg.AutoGGPattern;
import net.cacaovisualclient.mod.feature.autogg.AutoGGPatternStorage;
import net.cacaovisualclient.mod.module.modules.AutoGGModule;
import net.cacaovisualclient.mod.ui.Widget;
import net.cacaovisualclient.mod.ui.modmenu.ModMenuScreen;
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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AutoGGPanelWidget extends Widget {

    private static final int PADDING = 8;
    private static final int FIELD_HEIGHT = 16;
    private static final int BUTTON_HEIGHT = 14;
    private static final int TITLE_HEIGHT = 16;
    private static final int BASIC_CARD_HEIGHT = 52;
    private static final int FORM_CARD_HEIGHT = 82;
    private static final int LIST_HEADER_HEIGHT = 16;
    private static final int ROW_HEIGHT = 30;
    private static final int GAP = 8;

    private final AutoGGPatternStorage storage;

    private EditBox messageBox;
    private EditBox delayBox;
    private EditBox nameBox;
    private EditBox serverBox;
    private EditBox triggerBox;

    private AutoGGMatchMode selectedMode = AutoGGMatchMode.CONTAINS;
    private String statusMessage = "";
    private long statusUntilMs = 0L;

    public AutoGGPanelWidget(AutoGGModule module, int x, int y, int width) {
        super(x, y, width, 260);
        this.storage = module.getCustomStorage();
    }

    @Override
    public void init() {
        final Font font = Minecraft.getInstance().font;

        messageBox = createBox(font, "GG message", "GG", 80);
        delayBox = createBox(font, "Delay", "1000", 5);
        delayBox.setFilter(value -> value.isEmpty() || value.chars().allMatch(Character::isDigit));

        nameBox = createBox(font, "Pattern name", "bedwars", 32);
        serverBox = createBox(font, "Server contains", "hypixel.net", 64);
        triggerBox = createBox(font, "Chat trigger", "Winner:", 160);

        layoutFields();
        syncBasicFields();
        clearPatternForm();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int scrollOffset) {
        height = calculateHeight();
        super.render(guiGraphics, mouseX, mouseY, scrollOffset);

        layoutFields();

        final Font font = Minecraft.getInstance().font;
        final int renderY = y - scrollOffset;
        final int contentX = x + PADDING;
        final int contentWidth = width - PADDING * 2;
        final Color themeColor = CacaoVisualClient.getInstance().getSelectedTheme().getPrimaryColor();

        guiGraphics.drawString(font, "Custom AutoGG", contentX, renderY + 3, themeColor.brighter().getRGB(), true);

        if (System.currentTimeMillis() < statusUntilMs && !statusMessage.isBlank()) {
            guiGraphics.drawString(
                    font,
                    trimToWidth(font, statusMessage, contentWidth - 105),
                    contentX + contentWidth - font.width(trimToWidth(font, statusMessage, contentWidth - 105)),
                    renderY + 3,
                    new Color(180, 255, 180).getRGB(),
                    true
            );
        }

        renderBasicSettings(guiGraphics, font, mouseX, mouseY);
        renderPatternForm(guiGraphics, font, mouseX, mouseY);
        renderPatternList(guiGraphics, font, mouseX, mouseY);
    }

    @Override
    public void parentMouseClicked(MouseButtonEvent event) {
        layoutFields();

        if (!isOverAnyField(event.x(), event.y())) {
            clearFieldFocus();
        }
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        if (event.button() != 0) {
            return;
        }

        layoutFields();

        boolean clickedField = false;
        for (EditBox box : fields()) {
            final boolean overField = isOverField(box, event.x(), event.y());
            box.setFocused(overField);

            if (overField) {
                box.mouseClicked(event, false);
                clickedField = true;
            }
        }

        if (clickedField) {
            return;
        }

        final int mouseX = (int) event.x();
        final int mouseY = (int) event.y();

        if (isSaveBasicButton(mouseX, mouseY)) {
            saveBasicSettings();
            return;
        }

        if (isReloadButton(mouseX, mouseY)) {
            storage.reload();
            syncBasicFields();
            setStatus("Reloaded AutoGG JSON");
            return;
        }

        if (isModeButton(mouseX, mouseY)) {
            selectedMode = selectedMode == AutoGGMatchMode.CONTAINS
                    ? AutoGGMatchMode.REGEX
                    : AutoGGMatchMode.CONTAINS;
            return;
        }

        if (isSavePatternButton(mouseX, mouseY)) {
            savePatternFromForm();
            return;
        }

        if (isClearPatternButton(mouseX, mouseY)) {
            clearPatternForm();
            return;
        }

        handlePatternRowClick(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!isAnyFieldFocused()) {
            return false;
        }

        if (event.isEscape()) {
            clearFieldFocus();
            return true;
        }

        if (event.isConfirmation()) {
            if (messageBox.isFocused() || delayBox.isFocused()) {
                saveBasicSettings();
            } else {
                savePatternFromForm();
            }
            return true;
        }

        for (EditBox box : fields()) {
            if (box.isFocused()) {
                return box.keyPressed(event);
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        for (EditBox box : fields()) {
            if (box.isFocused()) {
                return box.charTyped(event);
            }
        }

        return false;
    }

    private void renderBasicSettings(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
        final int cardX = contentX();
        final int cardY = basicCardY();
        final int cardWidth = contentWidth();

        drawCard(guiGraphics, cardX, cardY, cardWidth, BASIC_CARD_HEIGHT);
        guiGraphics.drawString(font, "Message", cardX + 10, cardY + 8, new Color(230, 230, 230).getRGB(), true);
        guiGraphics.drawString(font, "Delay", cardX + 187, cardY + 8, new Color(230, 230, 230).getRGB(), true);

        messageBox.renderWidget(guiGraphics, mouseX, mouseY, 0);
        delayBox.renderWidget(guiGraphics, mouseX, mouseY, 0);

        drawButton(guiGraphics, font, "Save", saveBasicButtonX(), cardY + 27, 52, BUTTON_HEIGHT, true, false, mouseX, mouseY);
        drawButton(guiGraphics, font, "Reload", reloadButtonX(), cardY + 27, 64, BUTTON_HEIGHT, true, false, mouseX, mouseY);
    }

    private void renderPatternForm(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
        final int cardX = contentX();
        final int cardY = formCardY();
        final int cardWidth = contentWidth();

        drawCard(guiGraphics, cardX, cardY, cardWidth, FORM_CARD_HEIGHT);
        guiGraphics.drawString(font, "Pattern editor", cardX + 10, cardY + 8, new Color(230, 230, 230).getRGB(), true);

        guiGraphics.drawString(font, "Name", nameBox.getX(), cardY + 22, new Color(180, 180, 180).getRGB(), true);
        guiGraphics.drawString(font, "Server", serverBox.getX(), cardY + 22, new Color(180, 180, 180).getRGB(), true);
        guiGraphics.drawString(font, "Trigger", triggerBox.getX(), cardY + 51, new Color(180, 180, 180).getRGB(), true);

        nameBox.renderWidget(guiGraphics, mouseX, mouseY, 0);
        serverBox.renderWidget(guiGraphics, mouseX, mouseY, 0);
        triggerBox.renderWidget(guiGraphics, mouseX, mouseY, 0);

        drawButton(guiGraphics, font, selectedMode.name(), modeButtonX(), cardY + 32, 70, BUTTON_HEIGHT, true, false, mouseX, mouseY);
        drawButton(guiGraphics, font, "Save pattern", savePatternButtonX(), cardY + 32, 100, BUTTON_HEIGHT, true, false, mouseX, mouseY);
        drawButton(guiGraphics, font, "Clear", clearPatternButtonX(), cardY + 61, 94, BUTTON_HEIGHT, true, false, mouseX, mouseY);
    }

    private void renderPatternList(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
        final int listX = contentX();
        final int headerY = listHeaderY();
        final int rowStartY = patternRowsY();
        final int listWidth = contentWidth();
        final List<AutoGGPattern> patterns = storage.getPatterns();

        guiGraphics.drawString(font, "Saved patterns", listX, headerY + 4, new Color(230, 230, 230).getRGB(), true);

        if (patterns.isEmpty()) {
            final int emptyY = rowStartY;
            drawCard(guiGraphics, listX, emptyY, listWidth, ROW_HEIGHT);
            guiGraphics.drawString(
                    font,
                    "No patterns yet. Fill the editor above and press Save pattern.",
                    listX + 8,
                    emptyY + 10,
                    new Color(170, 170, 170).getRGB(),
                    true
            );
            return;
        }

        for (int i = 0; i < patterns.size(); i++) {
            final AutoGGPattern pattern = patterns.get(i);
            final int rowY = rowStartY + i * ROW_HEIGHT;
            final int rowColor = pattern.isEnabled()
                    ? new Color(35, 35, 35, 210).getRGB()
                    : new Color(25, 25, 25, 170).getRGB();

            guiGraphics.fill(listX, rowY, listX + listWidth, rowY + ROW_HEIGHT - 2, rowColor);

            final String title = pattern.getName() + "  /  " + pattern.getServer();
            final String subtitle = pattern.getMode() + "  " + pattern.getTrigger();
            guiGraphics.drawString(font, trimToWidth(font, title, listWidth - 142), listX + 8, rowY + 5, -1, true);
            guiGraphics.drawString(font, trimToWidth(font, subtitle, listWidth - 142), listX + 8, rowY + 17, new Color(170, 170, 170).getRGB(), true);

            drawButton(guiGraphics, font, pattern.isEnabled() ? "On" : "Off", rowToggleX(), rowY + 7, 38, BUTTON_HEIGHT, pattern.isEnabled(), false, mouseX, mouseY);
            drawButton(guiGraphics, font, "Edit", rowEditX(), rowY + 7, 34, BUTTON_HEIGHT, true, false, mouseX, mouseY);
            drawButton(guiGraphics, font, "Delete", rowDeleteX(), rowY + 7, 48, BUTTON_HEIGHT, true, true, mouseX, mouseY);
        }
    }

    private void handlePatternRowClick(int mouseX, int mouseY) {
        final List<AutoGGPattern> patterns = storage.getPatterns();
        final int rowStartY = patternRowsY();

        for (int i = 0; i < patterns.size(); i++) {
            final AutoGGPattern pattern = patterns.get(i);
            final int rowY = rowStartY + i * ROW_HEIGHT;

            if (MouseUtils.isMouseOver(mouseX, mouseY, rowToggleX(), rowY + 7, 38, BUTTON_HEIGHT)) {
                storage.setPatternEnabled(pattern.getName(), !pattern.isEnabled());
                setStatus(pattern.getName() + (pattern.isEnabled() ? " enabled" : " disabled"));
                return;
            }

            if (MouseUtils.isMouseOver(mouseX, mouseY, rowEditX(), rowY + 7, 34, BUTTON_HEIGHT)) {
                loadPattern(pattern);
                return;
            }

            if (MouseUtils.isMouseOver(mouseX, mouseY, rowDeleteX(), rowY + 7, 48, BUTTON_HEIGHT)) {
                storage.removePattern(pattern.getName());
                setStatus("Deleted " + pattern.getName());
                return;
            }
        }
    }

    private void saveBasicSettings() {
        storage.setMessage(messageBox.getValue().trim());
        storage.setDelayMs(parseDelay());
        syncBasicFields();
        setStatus("AutoGG settings saved");
    }

    private void savePatternFromForm() {
        final String name = nameBox.getValue().trim();
        final String server = serverBox.getValue().trim();
        final String trigger = triggerBox.getValue().trim();

        if (name.isBlank() || server.isBlank() || trigger.isBlank()) {
            setStatus("Fill name, server and trigger");
            return;
        }

        if (selectedMode == AutoGGMatchMode.REGEX && !isValidRegex(trigger)) {
            setStatus("Regex is invalid");
            return;
        }

        final AutoGGPattern pattern = new AutoGGPattern(name, server, trigger, selectedMode);
        if (!storage.savePattern(pattern)) {
            setStatus("Pattern is invalid");
            return;
        }

        clearPatternForm();
        setStatus("Saved pattern " + name);
    }

    private void loadPattern(AutoGGPattern pattern) {
        nameBox.setValue(pattern.getName());
        serverBox.setValue(pattern.getServer());
        triggerBox.setValue(pattern.getTrigger());
        selectedMode = AutoGGMatchMode.fromString(pattern.getMode());
        setStatus("Editing " + pattern.getName());
    }

    private void clearPatternForm() {
        nameBox.setValue("");
        serverBox.setValue("");
        triggerBox.setValue("");
        selectedMode = AutoGGMatchMode.CONTAINS;
        clearFieldFocus();
    }

    private void syncBasicFields() {
        final AutoGGConfig config = storage.getConfig();
        messageBox.setValue(config.getMessage());
        delayBox.setValue(String.valueOf(config.getDelayMs()));
    }

    private long parseDelay() {
        final String value = delayBox.getValue().trim();
        if (value.isBlank()) {
            return 1000L;
        }

        try {
            return Math.min(10_000L, Long.parseLong(value));
        } catch (NumberFormatException ignored) {
            return 1000L;
        }
    }

    private boolean isValidRegex(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException ignored) {
            return false;
        }
    }

    private EditBox createBox(Font font, String title, String hint, int maxLength) {
        final EditBox box = new EditBox(font, 0, 0, 10, FIELD_HEIGHT, Component.literal(title));
        box.setMaxLength(maxLength);
        box.setHint(Component.literal(hint));
        box.setTextShadow(false);
        return box;
    }

    private void layoutFields() {
        if (messageBox == null) {
            return;
        }

        final int cardX = contentX();
        final int basicY = basicCardY();
        final int formY = formCardY();

        layoutBox(messageBox, cardX + 10, basicY + 27, 170);
        layoutBox(delayBox, cardX + 187, basicY + 27, 54);

        layoutBox(nameBox, cardX + 10, formY + 32, 70);
        layoutBox(serverBox, cardX + 86, formY + 32, 100);
        layoutBox(triggerBox, cardX + 10, formY + 61, 258);
    }

    private void layoutBox(EditBox box, int boxX, int boxY, int boxWidth) {
        box.setX(boxX);
        box.setY(boxY);
        box.setWidth(boxWidth);
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
        guiGraphics.drawString(
                font,
                text,
                buttonX + buttonWidth / 2 - font.width(text) / 2,
                buttonY + 3,
                -1,
                true
        );
    }

    private boolean isSaveBasicButton(int mouseX, int mouseY) {
        return MouseUtils.isMouseOver(mouseX, mouseY, saveBasicButtonX(), basicCardY() + 27, 52, BUTTON_HEIGHT);
    }

    private boolean isReloadButton(int mouseX, int mouseY) {
        return MouseUtils.isMouseOver(mouseX, mouseY, reloadButtonX(), basicCardY() + 27, 64, BUTTON_HEIGHT);
    }

    private boolean isModeButton(int mouseX, int mouseY) {
        return MouseUtils.isMouseOver(mouseX, mouseY, modeButtonX(), formCardY() + 32, 70, BUTTON_HEIGHT);
    }

    private boolean isSavePatternButton(int mouseX, int mouseY) {
        return MouseUtils.isMouseOver(mouseX, mouseY, savePatternButtonX(), formCardY() + 32, 100, BUTTON_HEIGHT);
    }

    private boolean isClearPatternButton(int mouseX, int mouseY) {
        return MouseUtils.isMouseOver(mouseX, mouseY, clearPatternButtonX(), formCardY() + 61, 94, BUTTON_HEIGHT);
    }

    private boolean isOverAnyField(double mouseX, double mouseY) {
        for (EditBox box : fields()) {
            if (isOverField(box, mouseX, mouseY)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAnyFieldFocused() {
        for (EditBox box : fields()) {
            if (box.isFocused()) {
                return true;
            }
        }

        return false;
    }

    private void clearFieldFocus() {
        for (EditBox box : fields()) {
            box.setFocused(false);
        }
    }

    private EditBox[] fields() {
        return new EditBox[]{messageBox, delayBox, nameBox, serverBox, triggerBox};
    }

    private boolean isOverField(EditBox box, double mouseX, double mouseY) {
        return MouseUtils.isMouseOver(mouseX, mouseY, box.getX(), box.getY(), box.getWidth(), box.getHeight());
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
        return TITLE_HEIGHT
                + BASIC_CARD_HEIGHT
                + GAP
                + FORM_CARD_HEIGHT
                + GAP
                + LIST_HEADER_HEIGHT
                + Math.max(1, storage.getPatterns().size()) * ROW_HEIGHT
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

    private int basicCardY() {
        return renderY() + TITLE_HEIGHT;
    }

    private int formCardY() {
        return basicCardY() + BASIC_CARD_HEIGHT + GAP;
    }

    private int listHeaderY() {
        return formCardY() + FORM_CARD_HEIGHT + GAP;
    }

    private int patternRowsY() {
        return listHeaderY() + LIST_HEADER_HEIGHT;
    }

    private int saveBasicButtonX() {
        return contentX() + 248;
    }

    private int reloadButtonX() {
        return contentX() + 304;
    }

    private int modeButtonX() {
        return contentX() + 192;
    }

    private int savePatternButtonX() {
        return contentX() + 268;
    }

    private int clearPatternButtonX() {
        return contentX() + 274;
    }

    private int rowToggleX() {
        return rowEditX() - 44;
    }

    private int rowEditX() {
        return rowDeleteX() - 40;
    }

    private int rowDeleteX() {
        return contentX() + contentWidth() - 56;
    }
}
