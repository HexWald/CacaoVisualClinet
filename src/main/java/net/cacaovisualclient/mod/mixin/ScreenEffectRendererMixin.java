package net.cacaovisualclient.mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.modules.ViewTweaksModule;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @Inject(method = "renderFire", at = @At("HEAD"))
    private static void onRenderFire(PoseStack poseStack, MultiBufferSource multiBufferSource, TextureAtlasSprite textureAtlasSprite, CallbackInfo info) {
        final Module viewTweaksModule = CacaoVisualClient.getInstance().getModuleManager().getModule(ViewTweaksModule.class);

        if (!viewTweaksModule.isEnabled()) {
            return;
        }

        if (!(boolean) viewTweaksModule.getSetting("Lower Fire").getValue()) {
            return;
        }

        poseStack.translate(0, -0.3F, 0);
    }
}
