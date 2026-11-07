package net.cacaovisualclient.mod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.modules.NoChatIndicatorsModule;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @WrapOperation(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage;tag()Lnet/minecraft/client/GuiMessageTag;"))
    private GuiMessageTag removeChatSigningIndicators(GuiMessage instance, Operation<GuiMessageTag> original) {
        NoChatIndicatorsModule module = CacaoVisualClient.getModule(NoChatIndicatorsModule.class);
        if (module != null && module.isEnabled()) {
            return null;
        }

        return original.call(instance);
    }
}
