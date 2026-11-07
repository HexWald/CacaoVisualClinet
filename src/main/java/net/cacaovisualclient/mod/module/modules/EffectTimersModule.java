package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Effect Timers", description = "Shows the remaining time of your status effects")
public class EffectTimersModule extends HudModule {
    private final BooleanSetting replaceIcons = new BooleanSetting("Replace vanilla icons", true);

    public EffectTimersModule() {
        super(8, 64);
        addSettings(replaceIcons);
    }

    @Override
    public void render(GuiGraphics graphics, Font font) {
        if (mc.player == null || mc.level == null) {
            return;
        }
        float tickRate = mc.level.tickRateManager().tickrate();
        List<String> lines = new ArrayList<>();
        for (MobEffectInstance effect : mc.player.getActiveEffects()) {
            String name = effect.getEffect().value().getDisplayName().getString();
            String level = effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "";
            String duration = MobEffectUtil.formatDuration(effect, 1.0F, tickRate).getString();
            lines.add(name + level + "  " + duration);
        }
        lines.sort(String.CASE_INSENSITIVE_ORDER);
        renderLines(graphics, font, lines);
    }

    public boolean replacesIcons() {
        return isEnabled() && replaceIcons.getValue();
    }

    @Override
    public String getText() {
        return getName();
    }
}
