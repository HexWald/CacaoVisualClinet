package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.BooleanSetting;

@ModuleInfo(name = "Nametags", description = "Modify nametags")
public class NametagsModule extends Module {

    private final BooleanSetting nametagInPerspective = new BooleanSetting("Nametag in Perspective", true);

    public NametagsModule() {
        addSettings(nametagInPerspective);
    }
}
