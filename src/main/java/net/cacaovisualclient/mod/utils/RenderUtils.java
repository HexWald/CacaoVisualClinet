package net.cacaovisualclient.mod.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

import java.awt.*;

@UtilityClass
public class RenderUtils {

    public void drawTexture(GuiGraphics guiGraphics, Identifier texture, int x, int y, int size) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0, size, size, size, size, -1);
    }

    public static PlainTextButton pressableText(Font textRenderer, Component text, int x, int y, Runnable onClick) {
        return new PlainTextButton(x, y, textRenderer.width(text), textRenderer.lineHeight, text, (button) -> onClick.run(), textRenderer);
    }

    public void scaledText(Matrix3x2fStack stack, GuiGraphics guiGraphics, String text, int x, int y, float scale, int color, boolean shadow) {
        stack.pushMatrix();
        stack.translate(x, y);
        stack.scale(scale, scale);
        guiGraphics.drawString(Minecraft.getInstance().font, text, 0, 0, color, shadow);
        stack.popMatrix();
    }

    public void scaledItem(Matrix3x2fStack stack, GuiGraphics guiGraphics, Item item, int centerX, int centerY, float scale) {
        stack.pushMatrix();
        float itemSize = 16 * scale;
        stack.translate(centerX - itemSize / 2f, centerY - itemSize / 2f);
        stack.scale(scale, scale);
        guiGraphics.renderItem(new ItemStack(item), 0, 0);
        stack.popMatrix();
    }

    public void outline(GuiGraphics guiGraphics, int x, int y, int x2, int y2, int size, Color color) {
        guiGraphics.fill(x, y, x2, y + size, color.getRGB());
        guiGraphics.fill(x, y2 - size, x2, y2, color.getRGB());
        guiGraphics.fill(x, y + size, x + size, y2 - size, color.getRGB());
        guiGraphics.fill(x2 - size, y + size, x2, y2 - size, color.getRGB());
    }
}
