package net.cacaovisualclient.mod.mixin;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(OptionInstance.UnitDouble.class)
public class UnitDoubleMixin {

    @Inject(method = "validateValue(Ljava/lang/Double;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private void onValidateValue(Double double_, CallbackInfoReturnable<Optional<Double>> info) {
        info.setReturnValue(Optional.of(double_));
    }
}