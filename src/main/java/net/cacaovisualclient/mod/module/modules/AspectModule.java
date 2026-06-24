package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.NumberSetting;

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
