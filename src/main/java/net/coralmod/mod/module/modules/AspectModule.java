package net.coralmod.mod.module.modules;

import net.coralmod.mod.module.Module;
import net.coralmod.mod.module.ModuleInfo;
import net.coralmod.mod.module.settings.NumberSetting;

@ModuleInfo(name = "Aspect", description = "Change your aspect factor/ratio")
public class AspectModule extends Module {

    private final NumberSetting stretchFactor = new NumberSetting("Stretch Factor", 100, 1, 1000, 1);

    public AspectModule() {
        addSettings(stretchFactor);
    }

    public double getStretchFactor() {
        return stretchFactor.getValue() / 100;
    }
}
