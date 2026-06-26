package net.cacaovisualclient.mod.utils;

import lombok.experimental.UtilityClass;
import net.cacaovisualclient.mod.ui.toast.CacaoToastManager;
import net.cacaovisualclient.mod.ui.toast.CacaoToastType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

@UtilityClass
public class Notification {

    public void sendNotification(String title, String text) {
        CacaoToastManager.push(title, text, CacaoToastType.INFO);
    }

    public void send(String title, String text, CacaoToastType type) {
        CacaoToastManager.push(title, text, type);
    }

    public void sendSuccess(String title, String text) {
        CacaoToastManager.push(title, text, CacaoToastType.SUCCESS);
    }

    public void sendWarning(String title, String text) {
        CacaoToastManager.push(title, text, CacaoToastType.WARNING);
    }

    public void sendError(String title, String text) {
        CacaoToastManager.push(title, text, CacaoToastType.DANGER);
    }

    public void render(GuiGraphics guiGraphics, Font font) {
        CacaoToastManager.render(guiGraphics, font);
    }
}
