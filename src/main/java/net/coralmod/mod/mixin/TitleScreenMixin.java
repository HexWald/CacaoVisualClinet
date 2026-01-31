package net.coralmod.mod.mixin;

import net.coralmod.mod.CoralMod;
import net.coralmod.mod.utils.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    private static final Identifier GITHUB_LOGO = Identifier.fromNamespaceAndPath("coralmod", "textures/github.png");
    private static final int LOGO_SIZE = 20;
    private static final String GITHUB_REPO = "https://github.com/jirmjahu/coralmod";

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void onInit(CallbackInfo info) {
        final int logoX = 2;
        final int logoY = 2;
        final int logoSize = LOGO_SIZE;

        final int textX = logoX + logoSize + 2;
        final int textY = logoY + (logoSize - font.lineHeight) / 2 + 2;

        addRenderableWidget(RenderUtils.pressableText(
                font,
                Component.literal("CoralMod on Github"),
                textX,
                textY,
                () -> {
                    try {
                        Util.getPlatform().openUri(new URI(GITHUB_REPO));
                    } catch (Exception e) {
                        CoralMod.LOGGER.error("Failed to open GitHub page", e);
                    }
                }
        ));
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo info) {
        RenderUtils.drawTexture(guiGraphics, GITHUB_LOGO, 2, 2, LOGO_SIZE);

        guiGraphics.drawString(
                font,
                CoralMod.MOD_NAME + " v" + CoralMod.MOD_VERSION,
                2,
                height - 10 - font.lineHeight,
                -1,
                true
        );
    }
}
