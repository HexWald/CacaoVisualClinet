package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;

@ModuleInfo(name = "View Tweaks", description = "Several tweaks for the view")
public class ViewTweaksModule extends Module {

    private final BooleanSetting lowerShield = new BooleanSetting("Lower Shield", true);
    private final BooleanSetting lowerFire = new BooleanSetting("Lower Fire", true);

    public ViewTweaksModule() {
        addSettings(lowerShield, lowerFire);
    }
}
