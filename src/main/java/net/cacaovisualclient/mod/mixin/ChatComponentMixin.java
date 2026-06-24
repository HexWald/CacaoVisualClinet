package net.cacaovisualclient.mod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cacaovisualclient.mod.event.AddChatMessageEvent;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"))
    public void onAddMessage(Component component, CallbackInfo info) {
        AddChatMessageEvent.ADD_CHAT_MESSAGE_EVENT.invoker().onChatMessage(component.getString());
    }

    @WrapOperation(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage;tag()Lnet/minecraft/client/GuiMessageTag;"))
    private GuiMessageTag removeChatSigningIndicators(GuiMessage instance, Operation<GuiMessageTag> original) {
        return null;
    }
}
