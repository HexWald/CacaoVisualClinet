package net.cacaovisualclient.mod.ui.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CacaoToastManager {

    private static final int MAX_TOASTS = 5;
    private static final List<CacaoToast> TOASTS = new ArrayList<>();

    private CacaoToastManager() {
    }

    public static void push(String title, String message, CacaoToastType type) {
        TOASTS.add(new CacaoToast(title, message, type));

        while (TOASTS.size() > MAX_TOASTS) {
            TOASTS.removeFirst();
        }
    }

    public static void render(GuiGraphics guiGraphics, Font font) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.options.hideGui || TOASTS.isEmpty()) {
            return;
        }

        final long now = System.nanoTime();
        final Iterator<CacaoToast> iterator = TOASTS.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isExpired(now)) {
                iterator.remove();
            }
        }

        final int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        for (int i = 0; i < TOASTS.size(); i++) {
            TOASTS.get(i).render(guiGraphics, font, screenWidth, i, now);
        }
    }
}
