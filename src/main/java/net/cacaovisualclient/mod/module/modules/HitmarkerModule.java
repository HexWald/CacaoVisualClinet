package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.event.AddChatMessageEvent;
import net.cacaovisualclient.mod.event.AttackEntityEvent;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
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
import java.util.Locale;

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

    private long lastHitAtNanos = -1L;
    private long lastKillAtNanos = -1L;
    private long lastKillMessageAtNanos;
    private String lastKillMessage = "";

    public HitmarkerModule() {
        addSettings(marker, hitSound, killConfirm, killSound, markerSize);
        setEnabled(true);

        AttackEntityEvent.ATTACK_ENTITY_EVENT.register(this::handleAttack);
        AddChatMessageEvent.ADD_CHAT_MESSAGE_EVENT.register(this::handleChatMessage);
    }

    public void render(GuiGraphics guiGraphics, Font font) {
        if (!isEnabled() || mc.options.hideGui) {
            return;
        }

        if (marker.getValue()) {
            renderHitmarker(guiGraphics);
        }

        if (killConfirm.getValue()) {
            renderKillConfirm(guiGraphics, font);
        }
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
        final float alpha = 1.0F - progress;
        final int color = argb(Math.round(235.0F * alpha), Color.WHITE);
        final int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        final int centerY = mc.getWindow().getGuiScaledHeight() / 2;
        final int size = markerSize.getValue().intValue();
        final int gap = 4;

        drawDiagonal(guiGraphics, centerX + gap, centerY + gap, 1, 1, size, color);
        drawDiagonal(guiGraphics, centerX - gap, centerY + gap, -1, 1, size, color);
        drawDiagonal(guiGraphics, centerX + gap, centerY - gap, 1, -1, size, color);
        drawDiagonal(guiGraphics, centerX - gap, centerY - gap, -1, -1, size, color);
    }

    private void renderKillConfirm(GuiGraphics guiGraphics, Font font) {
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
            int startX,
            int startY,
            int directionX,
            int directionY,
            int length,
            int color
    ) {
        for (int i = 0; i < length; i++) {
            final int x = startX + i * directionX;
            final int y = startY + i * directionY;
            guiGraphics.fill(x, y, x + 2, y + 2, color);
        }
    }

    private static int argb(int alpha, Color color) {
        final int safeAlpha = Math.max(0, Math.min(alpha, 255));
        return safeAlpha << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }
}
