package net.coralmod.mod.module.modules;

import net.coralmod.mod.module.Module;
import net.coralmod.mod.module.ModuleInfo;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.OptionInstance;

@ModuleInfo(name = "FullBright", description = "Makes everything bright")
public class FullBrightModule extends Module {

    private double oldGamma = -1;

    public FullBrightModule() {
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            if (!isEnabled()) {
                return;
            }

            final OptionInstance<Double> gamma = mc.options.gamma();

            if (oldGamma == -1) {
                oldGamma = gamma.get();
            }

            if (gamma.get() < 1000.0) {
                gamma.set(100000.0);
            }
        });
    }

    @Override
    public void onDisable() {
        if (mc == null) {
            return;
        }

        if (oldGamma != -1) {
            mc.options.gamma().set(oldGamma);
        }

        oldGamma = -1;
    }
}
