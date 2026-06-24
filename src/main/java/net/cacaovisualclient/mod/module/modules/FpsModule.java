package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.minecraft.client.Minecraft;

@ModuleInfo(name = "FPS", description = "Displays your FPS")
public class FpsModule extends HudModule {

    public FpsModule() {
        super(20, 20);
    }

    @Override
    public String getText() {
        return "FPS: " + Minecraft.getInstance().getFps();
    }
}
