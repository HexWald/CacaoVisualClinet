package net.cacaovisualclient.mod.module.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.NumberSetting;

@ModuleInfo(name = "Viewmodel Editor", description = "Tweaks first person item position and rotation")
public class ViewmodelEditorModule extends Module {

    private final NumberSetting x = new NumberSetting("X", 0.0, -100.0, 100.0, 1.0);
    private final NumberSetting y = new NumberSetting("Y", 0.0, -100.0, 100.0, 1.0);
    private final NumberSetting z = new NumberSetting("Z", 0.0, -100.0, 100.0, 1.0);
    private final NumberSetting scale = new NumberSetting("Scale", 100.0, 50.0, 150.0, 1.0);
    private final NumberSetting pitch = new NumberSetting("Pitch", 0.0, -90.0, 90.0, 1.0);
    private final NumberSetting yaw = new NumberSetting("Yaw", 0.0, -90.0, 90.0, 1.0);
    private final NumberSetting roll = new NumberSetting("Roll", 0.0, -90.0, 90.0, 1.0);

    public ViewmodelEditorModule() {
        addSettings(x, y, z, scale, pitch, yaw, roll);
    }

    public void apply(PoseStack poseStack, boolean leftHand) {
        if (!isEnabled()) {
            return;
        }

        final float side = leftHand ? -1.0F : 1.0F;
        final float scaleValue = scale.getValue().floatValue() / 100.0F;

        poseStack.translate(
                x.getValue().floatValue() / 100.0F * side,
                y.getValue().floatValue() / 100.0F,
                z.getValue().floatValue() / 100.0F
        );

        poseStack.mulPose(Axis.XP.rotationDegrees(pitch.getValue().floatValue()));
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw.getValue().floatValue() * side));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll.getValue().floatValue() * side));
        poseStack.scale(scaleValue, scaleValue, scaleValue);
    }
}
