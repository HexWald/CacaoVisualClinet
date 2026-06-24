package net.cacaovisualclient.mod.module.modules;

import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.minecraft.client.multiplayer.PlayerInfo;

@ModuleInfo(name = "Ping", description = "Displays your Ping")
public class PingModule extends HudModule {

    public PingModule() {
        super(20, 20);
    }

    @Override
    public String getText() {
        if (mc.player == null || mc.getConnection() == null) {
            return null;
        }

        final PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());

        if (info == null) {
            return null;
        }

        return "Ping: " + info.getLatency() + "ms";
    }
}
