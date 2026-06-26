package net.cacaovisualclient.mod.ui.toast;

import java.awt.Color;

public enum CacaoToastType {

    INFO(new Color(116, 178, 255)),
    SUCCESS(new Color(93, 220, 145)),
    WARNING(new Color(255, 195, 84)),
    DANGER(new Color(255, 98, 98));

    private final Color color;

    CacaoToastType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
