package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import net.cacaovisualclient.mod.utils.Notification;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.awt.Color;

@ModuleInfo(
        name = "Low HP Effect",
        description = "Shows a soft vignette and heartbeat when your health is low"
)
public class LowHpEffectModule extends Module {

    private final NumberSetting healthThreshold = new NumberSetting("Health threshold", 6.0, 1.0, 20.0, 1.0);
    private final NumberSetting vignetteIntensity = new NumberSetting("Vignette intensity", 0.65, 0.1, 1.0, 0.05);
    private final BooleanSetting vignette = new BooleanSetting("Vignette", true);
    private final BooleanSetting heartbeat = new BooleanSetting("Heartbeat", true);
    private final BooleanSetting warningToast = new BooleanSetting("Warning toast", true);

    private int heartbeatCooldownTicks;
    private boolean wasLowHealth;
    private float overlayStrength;

    public LowHpEffectModule() {
        addSettings(healthThreshold, vignetteIntensity, vignette, heartbeat, warningToast);
        setEnabled(true);

        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    public void renderOverlay(GuiGraphics guiGraphics) {
        final Minecraft client = Minecraft.getInstance();

        if (!isEnabled()
                || !vignette.getValue()
                || overlayStrength <= 0.01F
                || client.player == null
                || client.options.hideGui) {
            return;
        }

        final int width = client.getWindow().getGuiScaledWidth();
        final int height = client.getWindow().getGuiScaledHeight();
        final int edge = Math.max(18, Math.round(48.0F * overlayStrength));

        final float pulse = 0.76F + 0.24F * (float) Math.sin(System.nanoTime() / 155_000_000.0D);
        final int alpha = Math.round(92.0F * vignetteIntensity.getValue().floatValue() * overlayStrength * pulse);
        final int softAlpha = Math.round(alpha * 0.55F);

        final int strong = argb(alpha, new Color(190, 28, 40));
        final int soft = argb(softAlpha, new Color(190, 28, 40));
        final int clear = 0x00000000;

        guiGraphics.fillGradient(0, 0, width, edge, strong, clear);
        guiGraphics.fillGradient(0, height - edge, width, height, clear, strong);

        guiGraphics.fill(0, edge / 2, edge, height - edge / 2, soft);
        guiGraphics.fill(width - edge, edge / 2, width, height - edge / 2, soft);
    }

    private void tick(Minecraft client) {
        if (!isEnabled() || client.player == null || client.player.isCreative() || client.player.isSpectator()) {
            fadeOut();
            return;
        }

        final Player player = client.player;
        final float effectiveHealth = player.getHealth() + player.getAbsorptionAmount();
        final float threshold = healthThreshold.getValue().floatValue();
        final boolean lowHealth = effectiveHealth <= threshold;

        overlayStrength = approach(overlayStrength, lowHealth ? getSeverity(effectiveHealth, threshold) : 0.0F, 0.075F);

        if (!lowHealth) {
            wasLowHealth = false;
            heartbeatCooldownTicks = 0;
            return;
        }

        if (!wasLowHealth && warningToast.getValue()) {
            final int hearts = Math.max(1, Math.round(effectiveHealth / 2.0F));
            Notification.sendWarning("Low HP", hearts + " hearts left");
        }

        wasLowHealth = true;

        if (heartbeat.getValue()) {
            tickHeartbeat(effectiveHealth, threshold);
        }
    }

    private void tickHeartbeat(float effectiveHealth, float threshold) {
        if (heartbeatCooldownTicks-- > 0) {
            return;
        }

        final float severity = getSeverity(effectiveHealth, threshold);
        final float pitch = 0.72F + severity * 0.12F;
        final float volume = 0.22F + severity * 0.18F;

        mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WARDEN_HEARTBEAT, pitch, volume));
        heartbeatCooldownTicks = Math.max(10, Math.round(26.0F - severity * 10.0F));
    }

    private void fadeOut() {
        overlayStrength = approach(overlayStrength, 0.0F, 0.075F);
        wasLowHealth = false;
        heartbeatCooldownTicks = 0;
    }

    private float getSeverity(float health, float threshold) {
        if (threshold <= 0.0F) {
            return 0.0F;
        }

        final float severity = 1.0F - health / threshold;
        return 0.35F + Math.max(0.0F, Math.min(severity, 1.0F)) * 0.65F;
    }

    private static float approach(float current, float target, float step) {
        if (current < target) {
            return Math.min(current + step, target);
        }

        return Math.max(current - step, target);
    }

    private static int argb(int alpha, Color color) {
        final int safeAlpha = Math.max(0, Math.min(alpha, 255));
        return safeAlpha << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }
}
