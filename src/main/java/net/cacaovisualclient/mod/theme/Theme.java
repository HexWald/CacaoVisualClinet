package net.cacaovisualclient.mod.theme;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Theme {

    TUBE("Tube", Items.TUBE_CORAL, new Color(47, 82, 194), new Color(47, 82, 194).darker()),
    BRAIN("Brain", Items.BRAIN_CORAL, new Color(195, 83, 150), new Color(195, 83, 150).darker()),
    BUBBLE("Bubble", Items.BUBBLE_CORAL, new Color(160, 24, 158), new Color(160, 24, 158).darker()),
    FIRE("Fire", Items.FIRE_CORAL, new Color(165, 37, 46), new Color(165, 37, 46).darker()),
    HORN("Horn", Items.HORN_CORAL, new Color(207, 184, 62), new Color(207, 184, 62).darker());

    private final String name;
    private final Item displayItem;
    private final Color primaryColor;
    private final Color secondaryColor;

    public static Optional<Theme> fromConfigValue(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(theme -> theme.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
