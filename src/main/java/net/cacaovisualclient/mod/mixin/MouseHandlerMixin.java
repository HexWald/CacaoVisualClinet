package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.event.MouseButtonClickedEvent;
import net.cacaovisualclient.mod.module.modules.ZoomModule;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void scrollZoom(long window, double horizontal, double vertical, CallbackInfo info) {
        ZoomModule zoom = CacaoVisualClient.getModule(ZoomModule.class);
        if (zoom != null && zoom.scroll(vertical)) {
            info.cancel();
        }
    }

    @ModifyArgs(method = "turnPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void scaleZoomMovement(Args args) {
        ZoomModule zoom = CacaoVisualClient.getModule(ZoomModule.class);
        if (zoom != null) {
            double scale = zoom.mouseScale();
            args.set(0, args.<Double>get(0) * scale);
            args.set(1, args.<Double>get(1) * scale);
        }
    }

    @Inject(method = "onButton", at = @At("HEAD"))
    public void onButton(long l, MouseButtonInfo mouseButtonInfo, int action, CallbackInfo info) {
        if (action == GLFW.GLFW_PRESS) {
            MouseButtonClickedEvent.MOUSE_BUTTON_CLICKED_EVENT.invoker().onMouseClicked(mouseButtonInfo);
        }
    }
}
