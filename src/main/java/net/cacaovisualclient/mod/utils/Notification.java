package net.cacaovisualclient.mod.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

@UtilityClass
public class Notification {

    public void sendNotification(String title, String text) {
        Minecraft.getInstance().getToastManager().addToast(
                SystemToast.multiline(Minecraft.getInstance(), SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.literal(title), Component.literal(text)));
    }
}
