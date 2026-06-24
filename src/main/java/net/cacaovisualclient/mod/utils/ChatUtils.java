package net.cacaovisualclient.mod.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

@UtilityClass
public class ChatUtils {

    public void sendToPlayer(String message) {
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal(message));
    }

    public void sendAsPlayer(String message) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        player.connection.sendChat(message);
    }
}