package net.cacaovisualclient.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.modules.FullBrightModule;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @ModifyExpressionValue(
            method = "updateLightTexture",
            at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/Options;gamma()Lnet/minecraft/client/OptionInstance;"))
    )
    private float applyFullBright(float gamma) {
        FullBrightModule module = CacaoVisualClient.getModule(FullBrightModule.class);
        return module != null && module.isEnabled() ? 100_000.0F : gamma;
    }
}
