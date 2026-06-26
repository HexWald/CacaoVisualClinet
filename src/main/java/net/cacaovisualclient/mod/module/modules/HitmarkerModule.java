package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.event.AddChatMessageEvent;
import net.cacaovisualclient.mod.event.AttackEntityEvent;
import net.cacaovisualclient.mod.feature.hitmarker.HitmarkerStyle;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.module.settings.ModeSetting;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import net.cacaovisualclient.mod.utils.MessageUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@ModuleInfo(
        name = "Hitmarker",
        description = "Adds a clean hitmarker and kill confirmation feedback"
)
public class HitmarkerModule extends Module {

    private static final long HITMARKER_DURATION_NANOS = 280_000_000L;
    private static final long KILL_CONFIRM_DURATION_NANOS = 1_050_000_000L;

    private final BooleanSetting marker = new BooleanSetting("Marker", true);
    private final BooleanSetting hitSound = new BooleanSetting("Hit sound", true);
    private final BooleanSetting killConfirm = new BooleanSetting("Kill confirm", true);
    private final BooleanSetting killSound = new BooleanSetting("Kill sound", true);
    private final NumberSetting markerSize = new NumberSetting("Marker size", 7.0, 4.0, 14.0, 1.0);
    private final ModeSetting markerStyle = new ModeSetting("Marker style", "Cycle", getStyleModes());

    private final Random random = new Random();

    private long lastHitAtNanos = -1L;
    private long lastKillAtNanos = -1L;
    private long lastKillMessageAtNanos;
    private int nextStyleIndex;
    private String lastKillMessage = "";
    private HitmarkerStyle activeStyle = HitmarkerStyle.CLASSIC;

    public HitmarkerModule() {
        addSettings(marker, markerStyle, markerSize, hitSound, killConfirm, killSound);
        setEnabled(true);

        AttackEntityEvent.ATTACK_ENTITY_EVENT.register(this::handleAttack);
        AddChatMessageEvent.ADD_CHAT_MESSAGE_EVENT.register(this::handleChatMessage);
    }

    public void renderKillConfirm(GuiGraphics guiGraphics, Font font) {
        if (!isEnabled() || mc.options.hideGui) {
            return;
        }

        if (killConfirm.getValue()) {
            renderKillConfirmText(guiGraphics, font);
        }
    }

    public void renderCrosshair(GuiGraphics guiGraphics) {
        if (!isEnabled() || mc.options.hideGui || !marker.getValue()) {
            return;
        }

        renderHitmarker(guiGraphics);
    }

    private void handleAttack(Player player, Entity target) {
        if (!isEnabled()
                || player != mc.player
                || !(target instanceof LivingEntity livingEntity)
                || target == player
                || !livingEntity.isAlive()) {
            return;
        }

        lastHitAtNanos = System.nanoTime();
        activeStyle = chooseStyle();

        if (hitSound.getValue()) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_ATTACK_STRONG, 1.65F, 0.35F));
        }
    }

    private void handleChatMessage(String message) {
        if (!isEnabled() || !killConfirm.getValue() || mc.player == null) {
            return;
        }

        if (!isOwnKillMessage(message, mc.player.getScoreboardName())) {
            return;
        }

        final String cleaned = MessageUtils.clean(message).toLowerCase(Locale.ROOT);
        final long now = System.nanoTime();
        if (cleaned.equals(lastKillMessage) && now - lastKillMessageAtNanos < KILL_CONFIRM_DURATION_NANOS) {
            return;
        }

        lastKillMessage = cleaned;
        lastKillMessageAtNanos = now;
        lastKillAtNanos = now;
        lastHitAtNanos = now;
        activeStyle = chooseStyle();

        if (killSound.getValue()) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.35F, 0.55F));
        }
    }

    private void renderHitmarker(GuiGraphics guiGraphics) {
        final long age = System.nanoTime() - lastHitAtNanos;
        if (lastHitAtNanos == -1L || age > HITMARKER_DURATION_NANOS) {
            return;
        }

        final float progress = age / (float) HITMARKER_DURATION_NANOS;
        final float alpha = easeOut(1.0F - progress);
        final int color = argb(Math.round(235.0F * alpha), Color.WHITE);
        final int accent = argb(Math.round(210.0F * alpha), new Color(255, 221, 110));
        final float centerX = getVanillaCrosshairCenterX(guiGraphics);
        final float centerY = getVanillaCrosshairCenterY(guiGraphics);
        final int size = markerSize.getValue().intValue();

        switch (activeStyle) {
            case CLASSIC -> renderClassic(guiGraphics, centerX, centerY, size, progress, color);
            case EXPAND -> renderExpand(guiGraphics, centerX, centerY, size, progress, color);
            case DIAMOND -> renderDiamond(guiGraphics, centerX, centerY, size, progress, color);
            case PLUS -> renderPlus(guiGraphics, centerX, centerY, size, progress, color);
            case BURST -> renderBurst(guiGraphics, centerX, centerY, size, progress, color, accent);
        }
    }

    private void renderKillConfirmText(GuiGraphics guiGraphics, Font font) {
        final long age = System.nanoTime() - lastKillAtNanos;
        if (lastKillAtNanos == -1L || age > KILL_CONFIRM_DURATION_NANOS) {
            return;
        }

        final float progress = age / (float) KILL_CONFIRM_DURATION_NANOS;
        final float alpha = progress < 0.18F
                ? progress / 0.18F
                : 1.0F - Math.max(0.0F, (progress - 0.72F) / 0.28F);

        final String text = "KILL CONFIRMED";
        final int x = (mc.getWindow().getGuiScaledWidth() - font.width(text)) / 2;
        final int y = mc.getWindow().getGuiScaledHeight() / 2 + 18;
        final int color = argb(Math.round(245.0F * alpha), new Color(255, 221, 110));

        guiGraphics.drawString(font, text, x, y, color, true);
    }

    private static float getVanillaCrosshairCenterX(GuiGraphics guiGraphics) {
        return ((guiGraphics.guiWidth() - 15) / 2) + 7.0F;
    }

    private static float getVanillaCrosshairCenterY(GuiGraphics guiGraphics) {
        return ((guiGraphics.guiHeight() - 15) / 2) + 7.0F;
    }

    private static boolean isOwnKillMessage(String message, String playerName) {
        final String lower = MessageUtils.lowerClean(message);

        if (!(lower.contains("final kill")
                || lower.contains("you killed")
                || lower.contains("you eliminated")
                || lower.contains(" was killed ")
                || lower.contains(" was slain ")
                || lower.contains("the void")
                || lower.contains(" knocked "))) {
            return false;
        }

        return MessageUtils.mentionsPlayerAsActor(message, playerName);
    }

    private static void drawDiagonal(
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            int directionX,
            int directionY,
            int gap,
            int length,
            int color
    ) {
        for (int offset = gap; offset < gap + length; offset++) {
            drawPoint(
                    guiGraphics,
                    centerX + offset * directionX,
                    centerY + offset * directionY,
                    color
            );
        }
    }

    private static void drawLine(
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            int directionX,
            int directionY,
            int gap,
            int length,
            int color
    ) {
        for (int offset = gap; offset < gap + length; offset++) {
            drawPoint(
                    guiGraphics,
                    centerX + offset * directionX,
                    centerY + offset * directionY,
                    color
            );
        }
    }

    private static void renderClassic(
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            int size,
            float progress,
            int color
    ) {
        final int gap = Math.round(3.0F + progress * 2.0F);

        drawDiagonal(guiGraphics, centerX, centerY, 1, 1, gap, size, color);
        drawDiagonal(guiGraphics, centerX, centerY, -1, 1, gap, size, color);
        drawDiagonal(guiGraphics, centerX, centerY, 1, -1, gap, size, color);
        drawDiagonal(guiGraphics, centerX, centerY, -1, -1, gap, size, color);
    }

    private static void renderExpand(
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            int size,
            float progress,
            int color
    ) {
        final int gap = Math.round(2.0F + easeOut(progress) * 7.0F);
        final int length = Math.max(3, Math.round(size * (1.15F - progress * 0.25F)));

        drawDiagonal(guiGraphics, centerX, centerY, 1, 1, gap, length, color);
        drawDiagonal(guiGraphics, centerX, centerY, -1, 1, gap, length, color);
        drawDiagonal(guiGraphics, centerX, centerY, 1, -1, gap, length, color);
        drawDiagonal(guiGraphics, centerX, centerY, -1, -1, gap, length, color);
    }

    private static void renderDiamond(
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            int size,
            float progress,
            int color
    ) {
        final int radius = Math.max(4, Math.round(size * (0.75F + progress * 0.35F)));

        drawDiamondEdge(guiGraphics, centerX, centerY - radius, 1, 1, radius, color);
        drawDiamondEdge(guiGraphics, centerX + radius, centerY, -1, 1, radius, color);
        drawDiamondEdge(guiGraphics, centerX, centerY + radius, -1, -1, radius, color);
        drawDiamondEdge(guiGraphics, centerX - radius, centerY, 1, -1, radius, color);
    }

    private static void renderPlus(
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            int size,
            float progress,
            int color
    ) {
        final int gap = Math.round(4.0F + progress * 3.0F);
        final int length = Math.max(3, size - 1);

        drawLine(guiGraphics, centerX, centerY, 1, 0, gap, length, color);
        drawLine(guiGraphics, centerX, centerY, -1, 0, gap, length, color);
        drawLine(guiGraphics, centerX, centerY, 0, 1, gap, length, color);
        drawLine(guiGraphics, centerX, centerY, 0, -1, gap, length, color);
    }

    private static void renderBurst(
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            int size,
            float progress,
            int color,
            int accent
    ) {
        final int diagonalGap = Math.round(3.0F + progress * 5.0F);
        final int plusGap = Math.round(5.0F + progress * 4.0F);
        final int diagonalLength = Math.max(3, size - 2);
        final int plusLength = Math.max(2, size / 2);

        drawDiagonal(guiGraphics, centerX, centerY, 1, 1, diagonalGap, diagonalLength, color);
        drawDiagonal(guiGraphics, centerX, centerY, -1, 1, diagonalGap, diagonalLength, color);
        drawDiagonal(guiGraphics, centerX, centerY, 1, -1, diagonalGap, diagonalLength, color);
        drawDiagonal(guiGraphics, centerX, centerY, -1, -1, diagonalGap, diagonalLength, color);

        drawLine(guiGraphics, centerX, centerY, 1, 0, plusGap, plusLength, accent);
        drawLine(guiGraphics, centerX, centerY, -1, 0, plusGap, plusLength, accent);
        drawLine(guiGraphics, centerX, centerY, 0, 1, plusGap, plusLength, accent);
        drawLine(guiGraphics, centerX, centerY, 0, -1, plusGap, plusLength, accent);
    }

    private static void drawDiamondEdge(
            GuiGraphics guiGraphics,
            float startX,
            float startY,
            int directionX,
            int directionY,
            int length,
            int color
    ) {
        for (int i = 0; i <= length; i++) {
            drawPoint(guiGraphics, startX + i * directionX, startY + i * directionY, color);
        }
    }

    private static void drawPoint(GuiGraphics guiGraphics, float x, float y, int color) {
        final int pixelX = Math.round(x);
        final int pixelY = Math.round(y);
        guiGraphics.fill(pixelX, pixelY, pixelX + 1, pixelY + 1, color);
    }

    private HitmarkerStyle chooseStyle() {
        final String mode = markerStyle.getValue();

        if ("Random".equalsIgnoreCase(mode)) {
            final HitmarkerStyle[] styles = HitmarkerStyle.values();
            return styles[random.nextInt(styles.length)];
        }

        if ("Cycle".equalsIgnoreCase(mode)) {
            final HitmarkerStyle[] styles = HitmarkerStyle.values();
            final HitmarkerStyle style = styles[nextStyleIndex % styles.length];
            nextStyleIndex = (nextStyleIndex + 1) % styles.length;
            return style;
        }

        return HitmarkerStyle.fromDisplayName(mode)
                .orElse(HitmarkerStyle.CLASSIC);
    }

    private static List<String> getStyleModes() {
        final List<String> modes = new ArrayList<>();
        modes.add("Cycle");
        modes.add("Random");

        for (HitmarkerStyle style : HitmarkerStyle.values()) {
            modes.add(style.getDisplayName());
        }

        return modes;
    }

    private static float easeOut(float value) {
        final float safeValue = Math.max(0.0F, Math.min(value, 1.0F));
        final float inverted = 1.0F - safeValue;
        return 1.0F - inverted * inverted * inverted;
    }

    private static int argb(int alpha, Color color) {
        final int safeAlpha = Math.max(0, Math.min(alpha, 255));
        return safeAlpha << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }
}
