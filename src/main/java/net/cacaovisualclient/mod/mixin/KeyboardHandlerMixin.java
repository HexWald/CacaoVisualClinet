package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.event.KeyPressedEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void onKeyPress(long l, int i, KeyEvent keyEvent, CallbackInfo info) {
        KeyPressedEvent.KEY_PRESSED_EVENT.invoker().onKeyPressed(keyEvent.key());
    }
}
