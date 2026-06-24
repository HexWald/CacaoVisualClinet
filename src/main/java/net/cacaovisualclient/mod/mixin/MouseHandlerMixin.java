package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.event.MouseButtonClickedEvent;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onButton", at = @At("HEAD"))
    public void onButton(long l, MouseButtonInfo mouseButtonInfo, int action, CallbackInfo info) {
        if (action == GLFW.GLFW_PRESS) {
            MouseButtonClickedEvent.MOUSE_BUTTON_CLICKED_EVENT.invoker().onMouseClicked(mouseButtonInfo);
        }
    }
}
