package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "ArmorHud", description = "Displays armor with durability")
public class ArmorHudModule extends HudModule {

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_PADDING = 2;
    private static final int BACKGROUND_PADDING = 4;

    private final BooleanSetting showDurability = new BooleanSetting("Show Durability", true);
    private final BooleanSetting showDurabilityPercent = new BooleanSetting("Durability as Percent", false);

    public ArmorHudModule() {
        super(20, 20);

        getSettings().remove(brackets);
        addSettings(showDurability, showDurabilityPercent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font) {
        if (mc.player == null) {
            return;
        }

        final List<ItemStack> armor = Arrays.stream(EquipmentSlot.values())
                .filter(slot -> slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR)
                .map(mc.player::getItemBySlot)
                .filter(itemStack -> !itemStack.isEmpty())
                .toList();

        final int padding = background.getValue() ? BACKGROUND_PADDING : 0;

        final int maxTextWidth = armor.stream()
                .filter(itemStack -> showDurability.getValue())
                .mapToInt(itemStack -> font.width(getDurabilityText(itemStack)))
                .max()
                .orElse(0);

        // Add a little more padding if show durability is enabled
        setWidth(ITEM_SIZE + maxTextWidth + padding * 2 + (showDurability.getValue() ? 4 : 0));

        setHeight(armor.size() * (ITEM_SIZE + ITEM_PADDING) - ITEM_PADDING + padding * 2);

        if (background.getValue()) {
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), new Color(0, 0, 0, 150).getRGB());
        }

        int offset = padding;
        for (ItemStack itemStack : armor.reversed()) {
            guiGraphics.renderItem(itemStack, getX() + padding, getY() + offset);

            if (showDurability.getValue()) {
                guiGraphics.drawString(font, getDurabilityText(itemStack), getX() + ITEM_SIZE + padding + 2, getY() + offset + 5, -1, textShadow.getValue());
            }

            offset += ITEM_SIZE + ITEM_PADDING;
        }
    }

    private String getDurabilityText(ItemStack item) {
        final int maxDamage = item.getMaxDamage();
        final int damage = item.getDamageValue();

        if (showDurabilityPercent.getValue()) {
            final int percent = (int) (((float) (maxDamage - damage) / maxDamage) * 100);
            return percent + "%";
        }

        return (maxDamage - damage) + "/" + maxDamage;
    }

    @Override
    public String getText() {
        return "";
    }
}
