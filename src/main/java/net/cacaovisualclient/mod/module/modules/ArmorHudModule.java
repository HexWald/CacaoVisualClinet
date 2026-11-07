package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "ArmorHud", description = "Displays armor with durability")
public class ArmorHudModule extends HudModule {

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_PADDING = 2;
    private static final int BACKGROUND_PADDING = 4;
    private static final List<EquipmentSlot> SLOTS = List.of(
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

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

        List<ItemStack> armor = new ArrayList<>(4);
        for (EquipmentSlot slot : SLOTS) {
            ItemStack item = mc.player.getItemBySlot(slot);
            if (!item.isEmpty()) {
                armor.add(item);
            }
        }
        if (armor.isEmpty()) {
            renderLines(guiGraphics, font, List.of());
            return;
        }

        final int padding = background.getValue() ? BACKGROUND_PADDING : 0;

        final int maxTextWidth = armor.stream()
                .filter(itemStack -> showDurability.getValue())
                .mapToInt(itemStack -> font.width(getDurabilityText(itemStack)))
                .max()
                .orElse(0);

        int width = ITEM_SIZE + padding * 2 + (maxTextWidth > 0 ? 2 + maxTextWidth : 0);
        int height = armor.size() * (ITEM_SIZE + ITEM_PADDING) - ITEM_PADDING + padding * 2;
        layout(guiGraphics, width, height);

        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(getX(), getY());
        pose.scale(scaleFactor(), scaleFactor());
        int offset = padding;
        for (ItemStack itemStack : armor) {
            guiGraphics.renderItem(itemStack, padding, offset);

            if (showDurability.getValue()) {
                guiGraphics.drawString(font, getDurabilityText(itemStack),
                        ITEM_SIZE + padding + 2, offset + 5, -1, textShadow.getValue());
            }

            offset += ITEM_SIZE + ITEM_PADDING;
        }
        pose.popMatrix();
    }

    private String getDurabilityText(ItemStack item) {
        final int maxDamage = item.getMaxDamage();
        if (maxDamage <= 0) {
            return "";
        }
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
