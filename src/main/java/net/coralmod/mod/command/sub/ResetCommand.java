package net.coralmod.mod.command.sub;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.coralmod.mod.CoralMod;
import net.coralmod.mod.module.Module;
import net.coralmod.mod.utils.Notification;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class ResetCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("reset")
                .executes(ctx -> {
                    // Reset settings and positions of all modules
                    for (Module module : CoralMod.getInstance().getModuleManager().getModules()) {
                        module.setEnabled(false);

                        module.reset();
                    }

                    CoralMod.getInstance().getConfig().setDefaultValues();

                    CoralMod.getInstance().save();

                    Notification.sendNotification("Settings reset", "All settings have been reset");
                    return 1;
                });
    }
}
