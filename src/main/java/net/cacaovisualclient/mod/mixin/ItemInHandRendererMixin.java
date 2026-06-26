package net.cacaovisualclient.mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.feature.inspect.SwordInspectStyle;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.modules.SwordInspectModule;
import net.cacaovisualclient.mod.module.modules.ViewTweaksModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.tags.ItemTags;
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
    private void onRenderItem(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ItemDisplayContext itemDisplayContext,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int light,
            CallbackInfo info
    ) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (!minecraft.options.getCameraType().isFirstPerson()
                || livingEntity != minecraft.player) {
            return;
        }

        final boolean isRightHand = itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        final boolean isLeftHand = itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        if (!isRightHand && !isLeftHand) {
            return;
        }

        lowerShieldIfEnabled(itemStack, poseStack);
        applySwordInspectIfRunning(itemStack, poseStack, isLeftHand);
    }

    private static void lowerShieldIfEnabled(ItemStack itemStack, PoseStack poseStack) {
        final Module viewTweaksModule = CacaoVisualClient.getInstance()
                .getModuleManager()
                .getModule(ViewTweaksModule.class);

        if (viewTweaksModule != null
                && viewTweaksModule.isEnabled()
                && (boolean) viewTweaksModule.getSetting("Lower Shield").getValue()
                && itemStack.getItem() instanceof ShieldItem) {

            poseStack.translate(0.0F, -0.10F, 0.0F);
        }
    }

    private static void applySwordInspectIfRunning(ItemStack itemStack, PoseStack poseStack, boolean isLeftHand) {
        if (!itemStack.is(ItemTags.SWORDS)) {
            return;
        }

        final SwordInspectModule swordInspectModule = CacaoVisualClient.getInstance()
                .getModuleManager()
                .getModule(SwordInspectModule.class);

        if (swordInspectModule == null || !swordInspectModule.isEnabled()) {
            return;
        }

        final float progress = swordInspectModule.getController().getProgress(swordInspectModule);

        if (progress < 0.0F) {
            return;
        }

        final float side = isLeftHand ? -1.0F : 1.0F;
        final float enter = smoothStep(clamp01(progress / 0.17F));
        final float exit = smoothStep(clamp01((1.0F - progress) / 0.22F));
        final float amount = Math.min(enter, exit);
        final SwordInspectStyle style = swordInspectModule.getController().getActiveStyle();

        switch (style) {
            case BUTTERFLY -> applyClassicButterfly(poseStack, progress, amount, side);
            case AGGRESSIVE_FLIP -> applyAggressiveFlip(poseStack, progress, amount, side);
            case SHOWCASE_SPIN -> applyShowcaseSpin(poseStack, progress, amount, side);
        }
    }

    private static void applyClassicButterfly(
            PoseStack poseStack,
            float progress,
            float amount,
            float side
    ) {
        final float spinProgress = smoothStep(clamp01((progress - 0.08F) / 0.72F));
        final float spin = spinProgress * 720.0F;
        final float wobble = (float) Math.sin(progress * Math.PI * 6.0F);

        poseStack.translate(
                -0.38F * side * amount,
                0.16F * amount,
                0.12F * amount
        );

        poseStack.mulPose(Axis.ZP.rotationDegrees(48.0F * side * amount));
        poseStack.mulPose(Axis.XP.rotationDegrees(-32.0F * amount));
        poseStack.mulPose(Axis.YP.rotationDegrees(spin * side));
        poseStack.mulPose(Axis.ZP.rotationDegrees(wobble * 12.0F * side * amount));
    }

    private static void applyAggressiveFlip(
            PoseStack poseStack,
            float progress,
            float amount,
            float side
    ) {
        final float spinProgress = smoothStep(clamp01((progress - 0.04F) / 0.76F));
        final float spin = spinProgress * 1080.0F;
        final float arc = (float) Math.sin(progress * Math.PI);
        final float kick = (float) Math.sin(progress * Math.PI * 4.0F);

        poseStack.translate(
                -0.30F * side * amount,
                (0.10F + 0.16F * arc) * amount,
                0.15F * amount
        );

        poseStack.mulPose(Axis.ZP.rotationDegrees(
                (34.0F + 24.0F * arc) * side * amount
        ));

        poseStack.mulPose(Axis.XP.rotationDegrees(
                (-48.0F + kick * 10.0F) * amount
        ));

        poseStack.mulPose(Axis.YP.rotationDegrees(spin * side));
    }

    private static void applyShowcaseSpin(
            PoseStack poseStack,
            float progress,
            float amount,
            float side
    ) {
        final float spinProgress = smoothStep(clamp01((progress - 0.12F) / 0.70F));
        final float spin = spinProgress * 720.0F;
        final float arc = (float) Math.sin(progress * Math.PI);
        final float wobble = (float) Math.sin(progress * Math.PI * 4.0F);

        poseStack.translate(
                -0.47F * side * amount,
                (0.07F + 0.18F * arc) * amount,
                0.18F * amount
        );

        poseStack.mulPose(Axis.ZP.rotationDegrees(18.0F * side * amount));
        poseStack.mulPose(Axis.XP.rotationDegrees(-64.0F * amount));
        poseStack.mulPose(Axis.ZP.rotationDegrees(spin * side));
        poseStack.mulPose(Axis.YP.rotationDegrees(
                (42.0F * arc + wobble * 10.0F) * side * amount
        ));
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(value, 1.0F));
    }

    private static float smoothStep(float value) {
        return value * value * (3.0F - 2.0F * value);
    }
}
