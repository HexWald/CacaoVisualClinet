package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;

@ModuleInfo(name = "Coordinates", description = "Shows your coordinates")
public class CoordinatesModule extends HudModule {

    public CoordinatesModule() {
        super(20, 20);
    }

    @Override
    public String getText() {
        if (mc.player == null) {
            return null;
        }

        var pos = mc.player.blockPosition();
        return pos.getX() + " / " + pos.getY() + " / " + pos.getZ();
    }
}

