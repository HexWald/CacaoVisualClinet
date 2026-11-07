package net.cacaovisualclient.mod.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.event.TitleTextEvent;
import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.modules.CustomCrosshairModule;
import net.cacaovisualclient.mod.module.modules.EffectTimersModule;
import net.cacaovisualclient.mod.module.modules.HitmarkerModule;
import net.cacaovisualclient.mod.module.modules.LowHpEffectModule;
import net.cacaovisualclient.mod.module.modules.ScoreboardModule;
import net.cacaovisualclient.mod.ui.editor.EditHudScreen;
import net.cacaovisualclient.mod.utils.Notification;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    public abstract Font getFont();

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void replaceEffectIcons(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo info) {
        EffectTimersModule timers = CacaoVisualClient.getModule(EffectTimersModule.class);
        if (timers != null && timers.replacesIcons()) {
            info.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui || client.screen instanceof EditHudScreen) {
            return;
        }
        final LowHpEffectModule lowHpEffectModule = CacaoVisualClient.getInstance()
                .getModuleManager()
                .getModule(LowHpEffectModule.class);

        if (lowHpEffectModule != null) {
            lowHpEffectModule.renderOverlay(guiGraphics);
        }

        for (HudModule hudModule : CacaoVisualClient.getInstance().getModuleManager().getHudModules()) {
            if (!hudModule.isEnabled()) {
                continue;
            }
            hudModule.render(guiGraphics, getFont());
        }

        Notification.render(guiGraphics, getFont());
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
            ordinal = 0))
    private void drawCrosshair(GuiGraphics graphics, RenderPipeline pipeline, Identifier sprite,
                               int x, int y, int width, int height) {
        var player = Minecraft.getInstance().player;
        boolean playing = player != null && !player.isSpectator();
        CustomCrosshairModule crosshair = CacaoVisualClient.getModule(CustomCrosshairModule.class);
        boolean custom = playing && crosshair != null && crosshair.isEnabled();
        if (custom) {
            crosshair.render(graphics);
        } else {
            graphics.blitSprite(pipeline, sprite, x, y, width, height);
        }
        HitmarkerModule marker = CacaoVisualClient.getModule(HitmarkerModule.class);
        if (playing && marker != null) {
            marker.renderCrosshair(graphics, custom ? graphics.guiWidth() / 2 : x + width / 2,
                    custom ? graphics.guiHeight() / 2 : y + height / 2);
        }
    }

    @Inject(method = "setTitle", at = @At("HEAD"))
    private void onSetTitle(Component component, CallbackInfo info) {
        publishTitleText(component);
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"))
    private void onSetSubtitle(Component component, CallbackInfo info) {
        publishTitleText(component);
    }

    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void onSetOverlayMessage(Component component, boolean tinted, CallbackInfo info) {
        publishTitleText(component);
    }

    private static void publishTitleText(Component component) {
        if (component != null) {
            TitleTextEvent.TITLE_TEXT_EVENT.invoker().onTitleText(component.getString());
        }
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void toggleSidebar(GuiGraphics guiGraphics, Objective objective, CallbackInfo info) {
        final ScoreboardModule module = CacaoVisualClient.getInstance().getModuleManager().getModule(ScoreboardModule.class);
        if (module.isEnabled() && !module.getEnableScoreboard().getValue()) {
            info.cancel();
        }
    }

    @ModifyArg(
            method = "displayScoreboardSidebar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V",
                    ordinal = 2
            )
    )
    public Component removeSidebarNumbers(Component component) {
        final ScoreboardModule module = CacaoVisualClient.getInstance().getModuleManager().getModule(ScoreboardModule.class);
        if (module.isEnabled() && !module.getNumbers().getValue()) {
            return Component.empty();
        }
        return component;
    }

    @ModifyArg(
            method = "displayScoreboardSidebar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V",
                    ordinal = 0
            ),
            index = 5
    )
    public boolean setSidebarTitleShadow(boolean shadow) {
        final ScoreboardModule module = CacaoVisualClient.getInstance().getModuleManager().getModule(ScoreboardModule.class);
        return module.isEnabled() && module.getTextShadow().getValue();
    }

    @ModifyArg(
            method = "displayScoreboardSidebar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V",
                    ordinal = 1
            ),
            index = 5
    )
    public boolean setSidebarTextShadow(boolean shadow) {
        final ScoreboardModule module = CacaoVisualClient.getInstance().getModuleManager().getModule(ScoreboardModule.class);
        return module.isEnabled() && module.getTextShadow().getValue();
    }

    @ModifyArg(
            method = "displayScoreboardSidebar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
                    ordinal = 0
            ),
            index = 4
    )
    public int setSidebarTitleBackgroundColor(int color) {
        final ScoreboardModule module = CacaoVisualClient.getInstance().getModuleManager().getModule(ScoreboardModule.class);
        if (module.isEnabled() && !module.getTitleBackground().getValue()) {
            return 0;
        }
        return color;
    }

    @ModifyArg(
            method = "displayScoreboardSidebar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
                    ordinal = 1
            ),
            index = 4
    )
    public int setSidebarBackgroundColor(int color) {
        final ScoreboardModule module = CacaoVisualClient.getInstance().getModuleManager().getModule(ScoreboardModule.class);
        if (module.isEnabled() && !module.getBackground().getValue()) {
            return 0;
        }
        return color;
    }
}
