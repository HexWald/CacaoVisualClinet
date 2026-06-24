package net.cacaovisualclient.mod.mixin;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.modules.NoBackgroundModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void removeBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo info) {
        if (!CacaoVisualClient.getInstance().getModuleManager().getModule(NoBackgroundModule.class).isEnabled()) {
            return;
        }

        final Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof AbstractContainerScreen<?>) {
            info.cancel();
        }
    }
}
