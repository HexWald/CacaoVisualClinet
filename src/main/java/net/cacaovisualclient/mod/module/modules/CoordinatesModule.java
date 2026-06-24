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

        final long x = Math.round(mc.player.getX());
        final long y = Math.round(mc.player.getY());
        final long z = Math.round(mc.player.getZ());

        return "X: " + x + " Y: " + y + " Z: " + z;
    }
}

