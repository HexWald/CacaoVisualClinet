package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.modules.AspectModule;
import net.cacaovisualclient.mod.module.modules.ZoomModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Mutable
    @Shadow
    @Final
    protected CubeMap cubeMap;

    @Mutable
    @Shadow
    @Final
    protected PanoramaRenderer panorama;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(
            Minecraft minecraft,
            ItemInHandRenderer itemInHandRenderer,
            RenderBuffers renderBuffers,
            BlockRenderDispatcher blockRenderDispatcher,
            CallbackInfo info
    ) {
        final Identifier panoramaTexture = Identifier.fromNamespaceAndPath("cacaovisualclient", "textures/gui/title/background/panorama");
        final CubeMap customCubeMap = new CubeMap(panoramaTexture);

        // TODO: Add this back but with a internal resource pack instead of just overwriting the stuff here so the user can change it
        // this.cubeMap = customCubeMap;
        // this.panorama = new PanoramaRenderer(customCubeMap);
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    public void onRenderItemInHand(float f, boolean bl, Matrix4f matrix4f, CallbackInfo info) {
        final ZoomModule module = CacaoVisualClient.getInstance().getModuleManager().getModule(ZoomModule.class);
        if (module == null) {
            return;
        }

        if (module.isEnabled() && module.isZooming()) {
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
        final AspectModule module = CacaoVisualClient.getInstance().getModuleManager().getModule(AspectModule.class);
        if (module == null || !module.isEnabled()) {
            return f;
        }
        return f / (float) module.getStretchFactor();
    }
}
