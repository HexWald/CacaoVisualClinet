package net.coralmod.mod.command;

import net.coralmod.mod.CoralMod;
import net.coralmod.mod.command.sub.ModuleCommand;
import net.coralmod.mod.command.sub.ResetCommand;
import net.coralmod.mod.utils.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class CoralModCommand {

    public CoralModCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("coralmod")
                            .executes(ctx -> {
                                ChatUtils.sendToPlayer(CoralMod.MOD_NAME + " v" + CoralMod.MOD_VERSION);
                                return 1;
                            })
                            .then(ModuleCommand.build())
                            .then(ResetCommand.build())
            );
        });
    }
}
