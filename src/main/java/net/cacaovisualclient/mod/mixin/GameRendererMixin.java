package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.modules.AspectModule;
import net.cacaovisualclient.mod.module.modules.ZoomModule;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void applyZoom(Camera camera, float partialTick, boolean worldFov, CallbackInfoReturnable<Float> info) {
        ZoomModule zoom = CacaoVisualClient.getModule(ZoomModule.class);
        if (worldFov && zoom != null) {
            info.setReturnValue(zoom.applyFov(info.getReturnValue()));
        }
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    public void onRenderItemInHand(float f, boolean bl, Matrix4f matrix4f, CallbackInfo info) {
        ZoomModule module = CacaoVisualClient.getModule(ZoomModule.class);
        if (module != null && module.hidesHand()) {
            info.cancel();
        }
    }

    @ModifyArg(
            method = "getProjectionMatrix",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix4f;perspective(FFFF)Lorg/joml/Matrix4f;",
                    ordinal = 0
            ),
            index = 1
    )
    private float modifyStretchFactor(float f) {
        final AspectModule module = CacaoVisualClient.getModule(AspectModule.class);
        if (module == null || !module.isEnabled()) {
            return f;
        }
        return f / (float) module.getStretchFactor();
    }
}
