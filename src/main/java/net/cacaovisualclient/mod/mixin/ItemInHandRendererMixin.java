package net.cacaovisualclient.mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.modules.ViewTweaksModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "renderItem", at = @At("HEAD"))
    public void onRenderItem(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, CallbackInfo info) {
        final Module viewTweaksModule = CacaoVisualClient.getInstance().getModuleManager().getModule(ViewTweaksModule.class);

        if (!viewTweaksModule.isEnabled()) {
            return;
        }

        if (!(boolean) viewTweaksModule.getSetting("Lower Shield").getValue()) {
            return;
        }

        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            return;
        }

        if (!(itemStack.getItem() instanceof ShieldItem)) {
            return;
        }

        poseStack.translate(0, -0.10F, 0);
    }
}
