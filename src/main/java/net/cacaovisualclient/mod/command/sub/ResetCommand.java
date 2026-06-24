package net.cacaovisualclient.mod.command.sub;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.utils.Notification;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class ResetCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("reset")
                .executes(ctx -> {
                    // Reset settings and positions of all modules
                    for (Module module : CacaoVisualClient.getInstance().getModuleManager().getModules()) {
                        module.setEnabled(false);

                        module.reset();
                    }

                    CacaoVisualClient.getInstance().getConfig().setDefaultValues();

                    CacaoVisualClient.getInstance().save();

                    Notification.sendNotification("Settings reset", "All settings have been reset");
                    return 1;
                });
    }
}
